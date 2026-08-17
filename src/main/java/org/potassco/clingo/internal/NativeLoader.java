/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

/**
 * Locates and loads the native clingo library.
 * <p>
 * The library is looked up in this order:
 * <ol>
 *     <li>the absolute path given by the system property {@code clingo.library.path},</li>
 *     <li>a bundled resource for the current platform (for example {@code darwin-aarch64/libclingo.dylib}), which is
 *     extracted to a temporary directory before loading,</li>
 *     <li>{@code System.loadLibrary("clingo")}, i.e. the platform library search path.</li>
 * </ol>
 * Loading happens through {@link System#load(String)} on this class' loader, which is what makes the generated
 * bindings resolve their symbols via {@code SymbolLookup.loaderLookup()}.
 */
public final class NativeLoader {

    /** The clingo release this binding was generated against. */
    public static final String EXPECTED_VERSION = "5.8.2";

    private static final String LIBRARY_PATH_PROPERTY = "clingo.library.path";

    private static volatile boolean loaded;

    private NativeLoader() {
    }

    /**
     * Loads the native library once, and verifies that it reports the version this binding was generated against.
     */
    public static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        load();
        loaded = true;
        checkVersion();
    }

    /**
     * @return the platform directory bundled libraries live in, e.g. {@code darwin-aarch64}
     */
    public static String getPlatformDirectory() {
        return getOperatingSystem() + "-" + getArchitecture();
    }

    /**
     * @return the file name of the clingo library on this platform, e.g. {@code libclingo.dylib}
     */
    public static String getLibraryFileName() {
        return switch (getOperatingSystem()) {
            case "darwin" -> "libclingo.dylib";
            case "win32" -> "clingo.dll";
            default -> "libclingo.so";
        };
    }

    private static void load() {
        String explicitPath = System.getProperty(LIBRARY_PATH_PROPERTY);
        if (explicitPath != null && !explicitPath.isBlank()) {
            System.load(Paths.get(explicitPath).toAbsolutePath().toString());
            return;
        }
        Path extracted = extractBundledLibrary();
        if (extracted != null) {
            System.load(extracted.toString());
            return;
        }
        System.loadLibrary("clingo");
    }

    private static Path extractBundledLibrary() {
        String resource = getPlatformDirectory() + "/" + getLibraryFileName();
        try (InputStream stream = NativeLoader.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                return null;
            }
            Path directory = Files.createTempDirectory("jclingo");
            directory.toFile().deleteOnExit();
            Path target = directory.resolve(getLibraryFileName());
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            target.toFile().deleteOnExit();
            return target;
        }
        catch (IOException e) {
            throw new UncheckedIOException("could not extract the bundled clingo library " + resource, e);
        }
    }

    /**
     * Fails fast when a stale library is picked up, instead of letting the mismatch surface as a crash somewhere
     * deep inside a call whose signature silently changed.
     */
    private static void checkVersion() {
        String version = Clingo.getVersion();
        if (!EXPECTED_VERSION.equals(version)) {
            throw new IllegalStateException("loaded clingo " + version + ", but this binding was generated against "
                                            + EXPECTED_VERSION + "; set -D" + LIBRARY_PATH_PROPERTY
                                            + " to a matching library");
        }
    }

    private static String getOperatingSystem() {
        String name = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (name.startsWith("mac") || name.startsWith("darwin")) {
            return "darwin";
        }
        if (name.startsWith("win")) {
            return "win32";
        }
        return "linux";
    }

    private static String getArchitecture() {
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        return switch (arch) {
            case "aarch64", "arm64" -> "aarch64";
            case "amd64", "x86_64", "x86-64" -> "x86-64";
            default -> arch;
        };
    }
}

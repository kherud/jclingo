package org.potassco.clingo.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * The library is looked up per platform and checked against the header the bindings were generated from, so that a
 * stale binary is reported instead of crashing somewhere later.
 */
public class NativeLoaderTest {

    @Test
    public void testLoadedLibraryMatchesTheGeneratedBindings() {
        Assert.assertEquals(NativeLoader.EXPECTED_VERSION, Clingo.getVersion());
    }

    @Test
    public void testPlatformResourceIsNamedAfterOsAndArchitecture() {
        String directory = NativeLoader.getPlatformDirectory();
        Assert.assertTrue(directory, directory.matches("(linux|darwin|win32)-\\S+"));
        Assert.assertTrue(NativeLoader.getLibraryFileName(), NativeLoader.getLibraryFileName().contains("clingo"));
    }
}

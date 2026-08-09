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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.ref.Cleaner;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Warns when a clingo resource is garbage collected without having been closed.
 * <p>
 * The detector never frees anything - clingo's handles have to be released explicitly and in a well defined order,
 * so cleaning up behind the user would only trade a leak for a crash. It exists to make the leak visible.
 * <p>
 * System properties: {@code clingo.leak.traces=true} records the allocation stack trace of every resource (slow),
 * {@code clingo.leak.throw=true} additionally records leaks for {@link #assertNoLeaks()}.
 */
public final class LeakDetector {

    private static final Logger LOGGER = System.getLogger(LeakDetector.class.getName());
    private static final Cleaner CLEANER = Cleaner.create();
    private static final boolean RECORD_TRACES = Boolean.getBoolean("clingo.leak.traces");
    private static final boolean STRICT = Boolean.getBoolean("clingo.leak.throw");
    private static final List<String> LEAKS = new CopyOnWriteArrayList<>();

    private LeakDetector() {
    }

    /**
     * Registers {@code owner} for leak detection; the returned handle has to be closed when the resource is closed.
     *
     * @param owner the resource being watched, which the returned handle must not reference
     * @param kind  a human readable description of the resource, e.g. {@code "Control"}
     */
    public static Registration register(Object owner, String kind) {
        Warner warner = new Warner(kind, RECORD_TRACES || STRICT ? new Throwable("allocated here") : null);
        return new Registration(CLEANER.register(owner, warner), warner);
    }

    /**
     * @throws IllegalStateException if any resource was garbage collected without being closed
     */
    public static void assertNoLeaks() {
        System.gc();
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (!LEAKS.isEmpty()) {
            String message = String.join("\n", LEAKS);
            LEAKS.clear();
            throw new IllegalStateException("clingo resources were not closed:\n" + message);
        }
    }

    /**
     * Handle a resource uses to mark itself as properly closed.
     */
    public static final class Registration {

        private final Cleaner.Cleanable cleanable;
        private final Warner warner;

        private Registration(Cleaner.Cleanable cleanable, Warner warner) {
            this.cleanable = cleanable;
            this.warner = warner;
        }

        /**
         * Marks the resource as closed, so that its collection is not reported as a leak.
         */
        public void close() {
            warner.closed.set(true);
            cleanable.clean();
        }
    }

    /**
     * Must not capture the watched resource, otherwise it would never become collectable.
     */
    private record Warner(String kind, Throwable allocationSite, AtomicBoolean closed) implements Runnable {

        private Warner(String kind, Throwable allocationSite) {
            this(kind, allocationSite, new AtomicBoolean());
        }

        @Override
        public void run() {
            if (closed.get()) {
                return;
            }
            String message = "clingo resource " + kind + " was garbage collected without being closed";
            LOGGER.log(Level.WARNING, message, allocationSite);
            if (STRICT) {
                LEAKS.add(message + (allocationSite == null ? "" : "\n" + format(allocationSite)));
            }
        }

        private static String format(Throwable trace) {
            StringBuilder builder = new StringBuilder();
            for (StackTraceElement element : trace.getStackTrace()) {
                builder.append("\tat ").append(element).append('\n');
            }
            return builder.toString();
        }
    }
}

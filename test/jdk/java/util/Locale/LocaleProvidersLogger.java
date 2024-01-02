/*
 * Copyright (c) 2012, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8245241 8246721 8261919
 * @summary Test the Locale provider preference is logged
 * @library /test/lib
 * @build LocaleProviders
 * @modules java.base/sun.util.locale.provider
 * @run junit/othervm -Djdk.lang.Process.allowAmbiguousCommands=false LocaleProvidersLogger
 */

import java.util.List;
import java.util.stream.Stream;

import jdk.test.lib.Utils;
import jdk.test.lib.process.ProcessTools;

import org.junit.jupiter.api.Test;

public class LocaleProvidersLogger {

    /*
     * 8245241 8246721 8261919: Ensure if an incorrect system property for locale providers is set,
     * it should be logged and presented to the user. The option
     * jdk.lang.Process.allowAmbiguousCommands=false is needed for properly escaping
     * double quotes in the string argument.
     */
    @Test
    public void logIncorrectLocaleProvider() throws Throwable {
        testRun("FOO", "bug8245241Test",
                "Invalid locale provider adapter \"FOO\" ignored.");
    }

    static void testRun(String prefList, String methodName, String... params) throws Throwable {

        List<String> command = List.of(
                "-ea", "-esa",
                "-cp", Utils.TEST_CLASS_PATH,
                "-Djava.util.logging.config.class=LocaleProviders$LogConfig",
                "-Djava.locale.providers=" + prefList,
                "--add-exports=java.base/sun.util.locale.provider=ALL-UNNAMED",
                "LocaleProviders", methodName);

        // Build process with arguments, if required by the method
        ProcessBuilder pb = ProcessTools.createTestJavaProcessBuilder(
                Stream.concat(command.stream(), Stream.of(params)).toList());


        // Evaluate process status
        int exitCode = ProcessTools.executeCommand(pb).getExitValue();
        if (exitCode != 0) {
            throw new RuntimeException("Unexpected exit code: " + exitCode);
        }
    }
}

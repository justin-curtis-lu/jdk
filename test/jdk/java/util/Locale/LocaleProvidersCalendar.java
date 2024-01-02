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
 * @bug 8228465 8232871 8257964
 * @summary Test any Calendar Locale provider related issues
 * @library /test/lib
 * @build LocaleProviders
 * @modules java.base/sun.util.locale.provider
 * @run junit/othervm LocaleProvidersCalendar
 */

import java.util.List;
import java.util.stream.Stream;

import jdk.test.lib.Utils;
import jdk.test.lib.process.ProcessTools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import static org.junit.jupiter.api.condition.OS.MAC;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

public class LocaleProvidersCalendar {

    /*
     * 8228465 (Windows only): Ensure correct ERA display name under HOST Windows
     */
    @Test
    @EnabledOnOs(WINDOWS)
    public void gregCalEraHost() throws Throwable {
        testRun("HOST", "bug8228465Test");
    }

    /*
     * 8232871 (macOS only): Ensure correct Japanese calendar values under
     * HOST Mac.
     */
    @Test
    @EnabledOnOs(MAC)
    public void japaneseCalValuesHost() throws Throwable {
        testRun("HOST", "bug8232871Test");
    }

    /*
     * 8257964 (macOS/Windows only): Ensure correct Calendar::getMinimalDaysInFirstWeek
     * value under HOST Windows / Mac
     */
    @Test
    @EnabledOnOs({WINDOWS, MAC})
    public void minDaysFirstWeekHost() throws Throwable {
        testRun("HOST", "bug8257964Test");
    }

    static void testRun(String prefList, String methodName, String... params) throws Throwable {

        List<String> command = List.of(
                "-ea", "-esa",
                "-cp", Utils.TEST_CLASS_PATH,
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

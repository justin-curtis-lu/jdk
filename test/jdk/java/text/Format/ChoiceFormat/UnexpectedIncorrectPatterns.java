/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 6801704
 * @summary Ensure that the incorrect patterns that created broken ChoiceFormats
 *          or threw unexpected NumberFormatExceptions no longer do so.
 * @run junit PatternsTest
 */

import java.text.ChoiceFormat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

// The difference between this test and PatternsTest, is that this test
// has had behavior changed by 6801704. PatternsTest is unaffected by 6801704.
public class UnexpectedIncorrectPatterns {

    // This test ensures that 1) unexpected NumberFormatExceptions are now replaced
    // with IllegalArgumentException 2) Patterns that created incorrect
    // ChoiceFormats now throw IllegalArgumentException.
    @ParameterizedTest
    @MethodSource
    public void brokenChoiceFormatTest(String pattern) {
        assertThrows(IllegalArgumentException.class, () -> new ChoiceFormat(pattern));
    }
    
    // Example of a pattern incorrectly parsing both a limit and format
    // portion as a limit. These would previously throw an unexpected
    // NumberFormatException or create a broken ChoiceFormat.
    private static Arguments[] brokenChoiceFormatTest() {
        return new Arguments[]{
                // --- Previously threw a NumberFormatException ---
                // Incorrect SubPattern in the middle of the Pattern
                arguments("0#foo|1#bar|baz|1<qux"),
                // Incomplete SubPattern at the start of the Pattern
                arguments("foo|0#bar"),
                //  --- Previously created an incorrect ChoiceFormat ---
                // Incomplete SubPattern at the end of the Pattern
                arguments("0#foo|1#bar|baz|"),
                // Multiple | without Limit or relation between
                arguments("0#foo||1#bar||"),
                // Multiple | without Limit between
                arguments("0#foo|2|1#bar||"),
                // SubPattern with only a Format followed by '|'
                arguments("foo|"),
                // Empty | with no limit
                arguments("|"),
                // Multiple |
                arguments("||"),
                // --- Previously created, but later threw an ArrayIndexOutOfBoundsException
                // when attempting to format with them ---
                // SubPattern with only a Limit (which is interpreted as a Format)
                arguments("0"),
                // SubPattern with only a Format
                arguments("foo"),
                // empty string
                arguments("")
        };
    }
}

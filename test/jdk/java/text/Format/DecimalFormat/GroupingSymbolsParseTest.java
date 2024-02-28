/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 7049000
 * @summary Ensure that when DecimalFormat parses, the grouping symbol as well
 *          as grouping size is considered.
 * @run junit GroupingSymbolsParseTest
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class GroupingSymbolsParseTest {

    // Formatted data expects a format with ',' grouping separator
    // and a grouping size of 3. Equivalent to US Locale values.
    private static final DecimalFormat fmt;

    static {
        fmt = new DecimalFormat();
        fmt.setGroupingSize(3);
        fmt.getDecimalFormatSymbols().setGroupingSeparator(',');
    }

    @ParameterizedTest
    @MethodSource({"fullParseStrings", "partialParseStrings"})
    public void groupingSymbolTest(String toParse, long expectedParsed) {
        try {
            assertEquals(expectedParsed, fmt.parse(toParse));
        } catch (ParseException e) {
            fail(String.format("ParseException should not be thrown. The pattern: '%s'" +
                    " should parse fully, or up to the incorrect portion", toParse));
        }
    }

    // Full parse cases for coverage
    private static Stream<Arguments> fullParseStrings() {
        return Stream.of(
                Arguments.of("1,", 1),
                Arguments.of(",1,", 1),
                Arguments.of("11,111", 11111), // (5) 1s
                Arguments.of("1111,1111", 11111111), // (8) 1s
                // Starts with grouping symbol (valid)
                Arguments.of(",111", 111),
                Arguments.of(",111,", 111),
                Arguments.of(",111,111", 111111)
        );
    }

    // Partial parse cases (which the associated fix addresses)
    private static Stream<Arguments> partialParseStrings() {
        return Stream.of(
                // Original bug report case
                Arguments.of("1,,,,5.5", 1),
                // Subsequent grouping symbols
                Arguments.of("1,,1", 1),
                Arguments.of("1,1,,1", 11),
                Arguments.of("1,,1,1", 1),
                // Invalid grouping sizes
                Arguments.of("1,11,111", 111),
                Arguments.of("111,111,11", 11111111), // (8) 1s
                Arguments.of("111,11,11", 11111), // (5) 1s
                // Combo of previous invalid cases
                Arguments.of(",111,,1,1", 111),
                Arguments.of(",111,11,11", 11111), // (5) 1s
                // Leading Zeros
                Arguments.of("000,1,1", 1),
                Arguments.of(",000,111,11,,1", 11111), // (5) 1s
                Arguments.of("0,000,1,,1,1", 1)
        );
    }

    // Subsequent grouping symbols should break parse. As no numerical value has been parsed,
    // a ParseException should be thrown.
    @Test
    public void groupingThrowsParseExceptionTest() {
        assertThrows(ParseException.class, () -> fmt.parse(",,1,1,1"));
    }
}

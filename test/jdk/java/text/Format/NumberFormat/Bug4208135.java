/*
 * Copyright (c) 2003, 2023, Oracle and/or its affiliates. All rights reserved.
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
 * @summary Confirm that the decimal separator is shown when explicitly requested.
 *          Tests against double, long, BigDecimal, and BigInteger with a combination
 *          of patterns.
 * @bug 4208135
 * @run junit Bug4208135
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Bug4208135 {

    static DecimalFormat df;

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.US);
    }

    // Separator with fractional exponent pattern
    @ParameterizedTest
    @MethodSource("fractionalDigitsWithSeparatorProvider")
    public void fractionalDigitsWithSeparatorTest(Number num, String expected) {
        df = getDF("0.#E0", true);
        String actualFormatted = df.format(num);
        assertEquals(expected, actualFormatted, getErrMsg("0.#E0", true));
    }

    private static Stream<Arguments> fractionalDigitsWithSeparatorProvider() {
        return Stream.of(
                Arguments.of(0.0, "0.E0"),
                Arguments.of(10.0, "1.E1"),
                Arguments.of(1000.0, "1.E3"),
                Arguments.of(0L, "0.E0"),
                Arguments.of(10L, "1.E1"),
                Arguments.of(1000L, "1.E3"),
                Arguments.of(new BigDecimal("0.0"), "0.E0"),
                Arguments.of(new BigDecimal("10.0"), "1.E1"),
                Arguments.of(new BigDecimal("1000.0"), "1.E3"),
                Arguments.of(new BigInteger("00"), "0.E0"),
                Arguments.of(new BigInteger("10"), "1.E1"),
                Arguments.of(new BigInteger("1000"), "1.E3")
        );
    }

    // No separator with fractional exponent pattern
    @ParameterizedTest
    @MethodSource("fractionalDigitsNoSeparatorProvider")
    public void fractionalDigitsNoSeparatorTest(Number num, String expected) {
        df = getDF("0.#E0", false);
        String actualFormatted = df.format(num);
        assertEquals(expected, actualFormatted, getErrMsg("0.#E0", false));
    }

    private static Stream<Arguments> fractionalDigitsNoSeparatorProvider() {
        return Stream.of(
                Arguments.of(0.0, "0E0"),
                Arguments.of(10.0, "1E1"),
                Arguments.of(1000.0, "1E3"),
                Arguments.of(0L, "0E0"),
                Arguments.of(10L, "1E1"),
                Arguments.of(1000L, "1E3"),
                Arguments.of(new BigDecimal("0.0"), "0E0"),
                Arguments.of(new BigDecimal("10.0"), "1E1"),
                Arguments.of(new BigDecimal("1000.0"), "1E3"),
                Arguments.of(new BigInteger("00"), "0E0"),
                Arguments.of(new BigInteger("10"), "1E1"),
                Arguments.of(new BigInteger("1000"), "1E3")
        );
    }

    // Separator with no fractional no exponent pattern
    @ParameterizedTest
    @MethodSource("noFractionalDigitsWithSeparatorProvider")
    public void noFractionalDigitsWithSeparatorTest(Number num, String expected) {
        df = getDF("0.###", true);
        String actualFormatted = df.format(num);
        assertEquals(expected, actualFormatted, getErrMsg("0.###", true));
    }

    private static Stream<Arguments> noFractionalDigitsWithSeparatorProvider() {
        return Stream.of(
                Arguments.of(0.0, "0."),
                Arguments.of(10.0, "10."),
                Arguments.of(1000.0, "1000."),
                Arguments.of(0L, "0."),
                Arguments.of(10L, "10."),
                Arguments.of(1000L, "1000."),
                Arguments.of(new BigDecimal("0.0"), "0."),
                Arguments.of(new BigDecimal("10.0"), "10."),
                Arguments.of(new BigDecimal("1000.0"), "1000."),
                Arguments.of(new BigInteger("00"), "0."),
                Arguments.of(new BigInteger("10"), "10."),
                Arguments.of(new BigInteger("1000"), "1000.")
        );
    }

    // No separator with no fractional no exponent pattern
    @ParameterizedTest
    @MethodSource("noFractionalDigitsNoSeparatorProvider")
    public void noFractionalDigitsNoSeparatorTest(Number num, String expected) {
        df = getDF("0.###", false);
        String actualFormatted = df.format(num);
        assertEquals(expected, actualFormatted, getErrMsg("0.###", false));
    }

    private static Stream<Arguments> noFractionalDigitsNoSeparatorProvider() {
        return Stream.of(
                Arguments.of(0.0, "0"),
                Arguments.of(10.0, "10"),
                Arguments.of(1000.0, "1000"),
                Arguments.of(0L, "0"),
                Arguments.of(10L, "10"),
                Arguments.of(1000L, "1000"),
                Arguments.of(new BigDecimal("0.0"), "0"),
                Arguments.of(new BigDecimal("10.0"), "10"),
                Arguments.of(new BigDecimal("1000.0"), "1000"),
                Arguments.of(new BigInteger("00"), "0"),
                Arguments.of(new BigInteger("10"), "10"),
                Arguments.of(new BigInteger("1000"), "1000")
        );
    }

    // Creates clean DF and sets the pattern and separatorShown value
    private static DecimalFormat getDF(String pattern, boolean separatorShown) {
        df = new DecimalFormat();
        df.applyPattern(pattern);
        df.setDecimalSeparatorAlwaysShown(separatorShown);
        return df;
    }

    private static String getErrMsg(String pattern, boolean separatorShown) {
        return String.format("Fails with pattern= %s, with separatorShown = %s",
                pattern, separatorShown);
    }
}

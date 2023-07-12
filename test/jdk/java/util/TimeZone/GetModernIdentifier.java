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
 * @bug 8311890
 * @summary Test the TimeZone.getModernIdentifier(String ID) method
 * @run junit GetModernIdentifier
 */

import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetModernIdentifier {

    // Ensure these identifiers are converted to the modern equivalent
    @ParameterizedTest
    @MethodSource("historicalIDs")
    public void convertIdentifierTest(String ID, String expectedID) {
        assertEquals(TimeZone.getModernIdentifier(ID), expectedID);
    }

    /*
     * IDs that are considered 'backward' by the tzdb.
     */
    private static Stream<Arguments> historicalIDs() {
        return Stream.of(
                Arguments.of("Africa/Accra", "Africa/Abidjan"),
                Arguments.of("America/Catamarca", "America/Argentina/Catamarca"),
                Arguments.of("Brazil/Acre", "America/Rio_Branco"),
                Arguments.of("Europe/Isle_of_Man", "Europe/London"),
                Arguments.of("Jamaica", "America/Jamaica"),
                Arguments.of("Singapore", "Asia/Singapore")

        );
    }

    // These identifiers are already "modern", ensure they are not converted
    @ParameterizedTest
    @MethodSource("modernIDs")
    public void maintainIdentifierTest(String ID) {
        assertEquals(TimeZone.getModernIdentifier(ID), ID);
    }

    /*
     * IDs that are not considered 'backward' by the tzdb.
     */
    private static Stream<String> modernIDs() {
        return Stream.of(
                "America/Nuuk",
                "Asia/Dhaka",
                "Etc/GMT",
                "Europe/Madrid",
                "Indian/Chagos",
                "Pacific/Easter",
                "Asia/Tokyo"
        );
    }

    // NPE should be thrown if passed null
    @Test
    public void nullTest() {
        assertThrows(NullPointerException.class, ()->TimeZone.getModernIdentifier(null));
    }
}

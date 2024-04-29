/*
 * Copyright (c) 2019, 2024, Oracle and/or its affiliates. All rights reserved.
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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

/**
 * @test
 * @bug 8210583 8217969 8218265 8295153
 * @summary Tests Base64.Encoder.encode and Base64.Decoder.decode
 *          with the large size of input array/buffer
 * @requires (sun.arch.data.model == "64" & os.maxMemory >= 10g)
 * @run main/othervm -Xms6g -Xmx8g TestEncodingDecodingLength
 *
 */

/*
 * Need enough memory to allocate arrays. Run on machine with 10g, as 8g
 * is not enough if we run with -Xmx8g. Run on 64bit only, as
 * -Xmx6g is above the max representable memory for 32-bit VM.
 */
public class TestEncodingDecodingLength {

    public static void main(String[] args) {
        int size = Integer.MAX_VALUE - 8;
        byte[] inputBytes;
        byte[] outputBytes;

        try {
            inputBytes = new byte[size];
            outputBytes = new byte[size];
        } catch (OutOfMemoryError er) {
            // While we should have enough memory to execute the test, the underlying
            // machine may be running other tests simultaneously and there is potential
            // for not enough memory to be available.
            System.out.println("Skipping test, not enough memory to allocate test arrays");
            return;
        }

        // Check encoder with large array length
        Base64.Encoder encoder = Base64.getEncoder();
        checkOOM("encode(byte[])", () -> encoder.encode(inputBytes));
        checkIAE("encode(byte[] byte[])", () -> encoder.encode(inputBytes, outputBytes));
        checkOOM("encodeToString(byte[])", () -> encoder.encodeToString(inputBytes));
        checkOOM("encode(ByteBuffer)", () -> encoder.encode(ByteBuffer.wrap(inputBytes)));

        // Check decoder with large array length,
        // should not throw any exception
        Arrays.fill(inputBytes, (byte) 86);
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            decoder.decode(inputBytes);
            decoder.decode(inputBytes, outputBytes);
            decoder.decode(ByteBuffer.wrap(inputBytes));
        } catch (NegativeArraySizeException nase) {
            // 8210583 - removed NASE being thrown from decode
            throw new RuntimeException("decode should not throw NASE");
        } catch (OutOfMemoryError er) {
            if (er.getMessage().equals("Decoded size is too large")) {
                // 8217969 - removed OOME being thrown from decode
                throw new RuntimeException("decode should not throw OOME");
            } else {
                System.out.println("Skipping test, not enough memory for target array allocated in decode");
            }
        }
    }

    private static final void checkOOM(String methodName, Runnable r) {
        try {
            r.run();
            throw new RuntimeException("OutOfMemoryError should have been thrown by: " + methodName);
        } catch (OutOfMemoryError er) {}
    }

    private static final void checkIAE(String methodName, Runnable r) {
        try {
            r.run();
            throw new RuntimeException("IllegalArgumentException should have been thrown by: " + methodName);
        } catch (IllegalArgumentException iae) {}
    }
}

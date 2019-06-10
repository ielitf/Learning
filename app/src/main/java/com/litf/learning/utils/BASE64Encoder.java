/**
 *
 * Project: ChinaSat_FAS
 * 
 * Copyright 2006 HP FAS. All Rights Reserved
 *
 */

package com.litf.learning.utils;

/**
 * <p>
 * Title: BASE64Encoder
 * </p>
 * 
 * <p>
 * Description: Utility class to do Base64 encoding, as defined by RFC 2045
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: HP
 * </p>
 * 
 * @author Mars Chang
 * @version 1.0
 */
public class BASE64Encoder {

    /**
     * Byte value that maps to 'a' in Base64 encoding
     */
    public static final int LOWER_CASE_A_VALUE = 26;

    /**
     * Byte value that maps to '0' in Base64 encoding
     */
    public static final int ZERO_VALUE = 52;

    /**
     * Byte value that maps to '+' in Base64 encoding
     */
    public static final int PLUS_VALUE = 62;

    /**
     * Byte value that maps to '/' in Base64 encoding
     */
    public static final int SLASH_VALUE = 63;

    /**
     * Bit mask for one character worth of bits in Base64 encoding. Equivalent
     * to binary value 111111b.
     */
    public static final int SIX_BIT_MASK = 63;

    /**
     * Encode an array of bytes using Base64
     * 
     * @param aData
     *            The bytes to be encoded
     * @return A valid Base64 representation of the input
     */
    public String encode(byte aData[]) {
        // Base64 encoding yields a String that is 33% longer than the byte
        // array
        int charCount = ((aData.length * 4) / 3) + 4;

        // New lines will also be needed for every 76 charactesr, so allocate a
        // StringBuffer that is long enough to hold the full result without
        // having to expand later
        StringBuffer result = new StringBuffer((charCount * 77) / 76);

        int byteArrayLength = aData.length;
        int byteArrayIndex = 0;
        int byteTriplet = 0;
        while (byteArrayIndex < byteArrayLength - 2) {
            // Build the 24 bit byte triplet from the input data
            byteTriplet = convertUnsignedByteToInt(aData[byteArrayIndex++]);
            // Each input byte contributes 8 bits to the triplet
            byteTriplet <<= 8;
            byteTriplet |= convertUnsignedByteToInt(aData[byteArrayIndex++]);
            byteTriplet <<= 8;
            byteTriplet |= convertUnsignedByteToInt(aData[byteArrayIndex++]);

            // Look at the lowest order six bits and remember them
            byte b4 = (byte) (SIX_BIT_MASK & byteTriplet);
            // Move the byte triplet to get the next 6 bit value
            byteTriplet >>= 6;
            byte b3 = (byte) (SIX_BIT_MASK & byteTriplet);
            byteTriplet >>= 6;
            byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
            byteTriplet >>= 6;
            byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

            // Add the Base64 encoded character to the result String
            result.append(mapByteToChar(b1));
            result.append(mapByteToChar(b2));
            result.append(mapByteToChar(b3));
            result.append(mapByteToChar(b4));

            // There are 57 bytes for every 76 characters,
            // so wrap the line when needed
            if (byteArrayIndex % 57 == 0) {
                result.append("\n");
            }
        }

        // Check if we have one byte left over
        if (byteArrayIndex == byteArrayLength - 1) {
            // Convert our one byte to an int
            byteTriplet = convertUnsignedByteToInt(aData[byteArrayIndex++]);
            // Right pad the second 6 bit value with zeros
            byteTriplet <<= 4;

            byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
            byteTriplet >>= 6;
            byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

            result.append(mapByteToChar(b1));
            result.append(mapByteToChar(b2));

            // Add "==" to the output to make it a multiple of 4 Base64
            // characters
            result.append("==");
        }

        // Check if we have two byte left over
        if (byteArrayIndex == byteArrayLength - 2) {
            // Convert our two bytes to an int
            byteTriplet = convertUnsignedByteToInt(aData[byteArrayIndex++]);
            byteTriplet <<= 8;
            byteTriplet |= convertUnsignedByteToInt(aData[byteArrayIndex++]);
            // Right pad the third 6 bit value with zeros
            byteTriplet <<= 2;

            byte b3 = (byte) (SIX_BIT_MASK & byteTriplet);
            byteTriplet >>= 6;
            byte b2 = (byte) (SIX_BIT_MASK & byteTriplet);
            byteTriplet >>= 6;
            byte b1 = (byte) (SIX_BIT_MASK & byteTriplet);

            result.append(mapByteToChar(b1));
            result.append(mapByteToChar(b2));
            result.append(mapByteToChar(b3));

            // Add "==" to the output to make it a multiple of 4 Base64
            // characters
            result.append("=");
        }

        return result.toString();
    }

    /**
     * Convert a byte to an integer. Needed because in Java bytes are signed,
     * and for Base64 purposes they are not. If not done this way, when
     * converted to an int, 0xFF will become -127
     * 
     * @param aByte
     *            Byte value to be converted
     * @return Value as an integer, as if byte was unsigned
     */
    private int convertUnsignedByteToInt(byte aByte) {
        if (aByte >= 0) {
            return (int) aByte;
        }

        return 256 + aByte;
    }

    /**
     * Convert a byte between 0 and 63 to its Base64 character equivalent
     * 
     * @param aByte
     *            Byte value to be converted
     * @return Base64 char value
     */
    private char mapByteToChar(byte aByte) {
        if (aByte < LOWER_CASE_A_VALUE) {
            return (char) ('A' + aByte);
        }

        if (aByte < ZERO_VALUE) {
            return (char) ('a' + (aByte - LOWER_CASE_A_VALUE));
        }

        if (aByte < PLUS_VALUE) {
            return (char) ('0' + (aByte - ZERO_VALUE));
        }

        if (aByte == PLUS_VALUE) {
            return '+';
        }

        if (aByte == SLASH_VALUE) {
            return '/';
        }

        throw new IllegalArgumentException("Byte " + new Integer(aByte)
                + " is not a valid Base64 value");
    }

}

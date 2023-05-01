/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data;

import de.dasbabypixel.annotations.Api;

public class DataUtil {

    @Api public static final int BYTES_LONG = 8;
    @Api public static final int BYTES_DOUBLE = 8;
    @Api public static final int BYTES_INT = 4;
    @Api public static final int BYTES_FLOAT = 4;
    @Api public static final int BYTES_SHORT = 2;
    @Api public static final int BYTES_BYTE = 1;

    @Api public static byte getByte(byte[] memory, int index) {
        return memory[index];
    }

    @Api public static short getShort(byte[] memory, int index) {
        return (short) (memory[index] << 8 | memory[index + 1] & 0xFF);
    }

    @Api public static short getShortLE(byte[] memory, int index) {
        return (short) (memory[index] & 0xff | memory[index + 1] << 8);
    }

    @Api public static int getUnsignedMedium(byte[] memory, int index) {
        return (memory[index] & 0xff) << 16 | (memory[index + 1] & 0xff) << 8 | memory[index + 2] & 0xff;
    }

    @Api public static int getUnsignedMediumLE(byte[] memory, int index) {
        return memory[index] & 0xff | (memory[index + 1] & 0xff) << 8 | (memory[index + 2] & 0xff) << 16;
    }

    @Api public static int getInt(byte[] memory, int index) {
        return (memory[index] & 0xff) << 24 | (memory[index + 1] & 0xff) << 16 | (memory[index + 2] & 0xff) << 8 | memory[index + 3] & 0xff;
    }

    @Api public static int getIntLE(byte[] memory, int index) {
        return memory[index] & 0xff | (memory[index + 1] & 0xff) << 8 | (memory[index + 2] & 0xff) << 16 | (memory[index + 3] & 0xff) << 24;
    }

    @Api public static long getLong(byte[] memory, int index) {
        return ((long) memory[index] & 0xff) << 56 | ((long) memory[index + 1] & 0xff) << 48 | ((long) memory[index + 2] & 0xff) << 40 | ((long) memory[index + 3] & 0xff) << 32 | ((long) memory[index + 4] & 0xff) << 24 | ((long) memory[index + 5] & 0xff) << 16 | ((long) memory[index + 6] & 0xff) << 8 | (long) memory[index + 7] & 0xff;
    }

    @Api public static long getLongLE(byte[] memory, int index) {
        return (long) memory[index] & 0xff | ((long) memory[index + 1] & 0xff) << 8 | ((long) memory[index + 2] & 0xff) << 16 | ((long) memory[index + 3] & 0xff) << 24 | ((long) memory[index + 4] & 0xff) << 32 | ((long) memory[index + 5] & 0xff) << 40 | ((long) memory[index + 6] & 0xff) << 48 | ((long) memory[index + 7] & 0xff) << 56;
    }

    @Api public static void setByte(byte[] memory, int index, int value) {
        memory[index] = (byte) value;
    }

    @Api public static void setShort(byte[] memory, int index, int value) {
        memory[index] = (byte) (value >>> 8);
        memory[index + 1] = (byte) value;
    }

    @Api public static void setShortLE(byte[] memory, int index, int value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
    }

    @Api public static void setMedium(byte[] memory, int index, int value) {
        memory[index] = (byte) (value >>> 16);
        memory[index + 1] = (byte) (value >>> 8);
        memory[index + 2] = (byte) value;
    }

    @Api public static void setMediumLE(byte[] memory, int index, int value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
        memory[index + 2] = (byte) (value >>> 16);
    }

    @Api public static void setInt(byte[] memory, int index, int value) {
        memory[index] = (byte) (value >>> 24);
        memory[index + 1] = (byte) (value >>> 16);
        memory[index + 2] = (byte) (value >>> 8);
        memory[index + 3] = (byte) value;
    }

    @Api public static void setIntLE(byte[] memory, int index, int value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
        memory[index + 2] = (byte) (value >>> 16);
        memory[index + 3] = (byte) (value >>> 24);
    }

    @Api public static void setLong(byte[] memory, int index, long value) {
        memory[index] = (byte) (value >>> 56);
        memory[index + 1] = (byte) (value >>> 48);
        memory[index + 2] = (byte) (value >>> 40);
        memory[index + 3] = (byte) (value >>> 32);
        memory[index + 4] = (byte) (value >>> 24);
        memory[index + 5] = (byte) (value >>> 16);
        memory[index + 6] = (byte) (value >>> 8);
        memory[index + 7] = (byte) value;
    }

    @Api public static void setLongLE(byte[] memory, int index, long value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
        memory[index + 2] = (byte) (value >>> 16);
        memory[index + 3] = (byte) (value >>> 24);
        memory[index + 4] = (byte) (value >>> 32);
        memory[index + 5] = (byte) (value >>> 40);
        memory[index + 6] = (byte) (value >>> 48);
        memory[index + 7] = (byte) (value >>> 56);
    }
}

package com.cn.jk.powerwaveviewer.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 小端字节序解析工具（严格适配特高频局放图谱规范，Java 8 兼容）
 */
public class LittleEndianParser {
    // 小端字节序缓冲区（私有成员，通过方法操作）
    private final ByteBuffer buffer;

    public LittleEndianParser(byte[] fileData) {
        this.buffer = ByteBuffer.wrap(fileData).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 读取int8（1字节，有符号）
     */
    public byte readInt8() {
        return buffer.get();
    }

    /**
     * 读取uint8（1字节，无符号）
     */
    public int readUInt8() {
        return buffer.get() & 0xFF;
    }

    /**
     * 读取int16（2字节，小端，有符号）
     */
    public short readInt16() {
        return buffer.getShort();
    }

    /**
     * 读取int32（4字节，小端，有符号）
     */
    public int readInt32() {
        return buffer.getInt();
    }

    /**
     * 读取int64（8字节，小端，有符号）
     */
    public long readInt64() {
        return buffer.getLong();
    }

    /**
     * 读取float（4字节，小端，IEEE 754）
     */
    public float readFloat() {
        return buffer.getFloat();
    }

    /**
     * 读取ASCII字符串（以\0结尾，指定最大长度）
     */
    public String readAsciiString(int maxLength) {
        byte[] temp = new byte[maxLength];
        buffer.get(temp);
        System.out.println("十六进制格式：" + ByteUtils.bytesToHexString(temp));
        System.out.println("ASCII字段原始字节：" + Arrays.toString(temp));

        // 找到\0终止符，截取有效字符串
        int len = 0;
        while (len < maxLength && temp[len] != 0) {
            len++;
        }
        return new String(temp, 0, len, StandardCharsets.US_ASCII);
    }

    /**
     * 读取UNICODE字符串（以0x0000结尾，指定最大长度，2字节/字符）
     */
    public String readUnicodeString(int maxCharCount) {
        int byteLength = maxCharCount;
        byte[] temp = new byte[byteLength];
        buffer.get(temp);
        // 打印前50字节（足够覆盖“500kV 泉城变电站”），查看是否有提前的0x0000
        //System.out.println("UNICODE字段原始字节（前50字节）：" + Arrays.toString(Arrays.copyOf(temp, 118)));
        // 找到0x0000终止符，截取有效字符串
        int len = 0;
        while (len < byteLength - 1) {
            if (temp[len] == 0 && temp[len + 1] == 0) {
                break;
            }
            len += 2;
        }
        return new String(temp, 0, len, StandardCharsets.UTF_16LE); // UTF-16LE对应小端UNICODE
    }

    /**
     * 读取指定长度的字节数组（用于尾部预留字段等）
     */
    public byte[] readBytes(int length) {
        byte[] temp = new byte[length];
        buffer.get(temp);
        return temp;
    }

    /**
     * 跳过指定字节数（用于可选字段或预留字段）
     */
    public void skipBytes(int length) {
        buffer.position(buffer.position() + length);
    }

    /**
     * 获取当前读取位置
     */
    public int getPosition() {
        return buffer.position();
    }

    /**
     * 设置读取位置（用于跳转图谱数据）
     */
    public void setPosition(int position) {
        buffer.position(position);
    }

    /**
     * 增强方法：读取小端序float，并返回原始4字节和解析后的值
     * @return FloatWithBytes 包含原始字节和float值
     */
    public ByteUtils.FloatWithBytes readFloatWithBytes() {
        byte[] bytes = readBytes(4); // 读取原始4字节
        float value = ByteBuffer.wrap(bytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getFloat(); // 解析为float
        return new ByteUtils.FloatWithBytes(bytes, value);
    }
}
package com.cn.jk.powerwaveviewer.utils;

import java.util.Arrays;

public class ByteUtils {

    // 私有化构造方法，禁止实例化（工具类只提供静态方法）
    private ByteUtils() {}

    /**
     * 字节数组转十六进制字符串（调试专用）
     * @param bytes 要转换的字节数组
     * @return 格式："00 11 22 ... FF"
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "空数组";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // %02X：两位十六进制大写，不足补0；%02x 是小写
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim(); // 去掉最后一个空格
    }

    /**
     * 调试专用：读取并打印跳过的字节内容，同时等效跳过指定长度
     * @param parser 小端解析器
     * @param length 要跳过的字节长度
     * @param desc 该段跳过字节的描述（方便区分不同的预留字段）
     */
    public static void debugSkippedBytes(LittleEndianParser parser, int length, String desc) {
        byte[] skippedBytes = parser.readBytes(length);
        System.out.println("\n===== 跳过的 [" + desc + "] 内容（" + length + "字节） =====");
        System.out.println("十六进制格式：" + bytesToHexString(skippedBytes));
        System.out.println("十进制格式：" + Arrays.toString(skippedBytes));
        System.out.println("===============================================\n");
    }

    public static class FloatWithBytes {
        public byte[] bytes; // 原始4字节
        public float value;  // 解析后的float值

        public FloatWithBytes(byte[] bytes, float value) {
            this.bytes = bytes;
            this.value = value;
        }
    }
}

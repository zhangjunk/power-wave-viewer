package com.cn.jk.powerwaveviewer.service.parser;

import com.cn.jk.powerwaveviewer.entity.GraphData;
import com.cn.jk.powerwaveviewer.entity.UHFPDatFile;
import com.cn.jk.powerwaveviewer.utils.LittleEndianParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * .dat文件解析主类（严格遵循特高频局放图谱规范）
 */
public class UHFPDatParser {

    public UHFPDatFile parseDatFileByByte(byte[] waveData){
        // 2. 验证CRC32（规范要求）
        //validateCRC32(fileData);
        // 3. 初始化小端解析器
        LittleEndianParser parser = new LittleEndianParser(waveData);
        // 4. 解析文件头部
        UHFPDatFile datFile = parseFileHeader(parser);
        // 5. 解析N个图谱数据
        List<GraphData> graphList = parseGraphDataList(parser, datFile.getGraphCount(), waveData.length);
        datFile.setGraphList(graphList);
        // 6. 解析文件尾部
        parseFileTail(parser, datFile);
        return datFile;
    }

    /**
     * 解析.dat文件
     * @param filePath .dat文件路径
     * @return 解析后的文件模型
     * @throws IOException 读写异常

    public UHFPDatFile parseDatFile(String filePath) throws IOException {
        // 1. 读取文件全部字节
        byte[] fileData = readFileToBytes(filePath);
        // 2. 验证CRC32（规范要求）
        //validateCRC32(fileData);
        // 3. 初始化小端解析器
        LittleEndianParser parser = new LittleEndianParser(fileData);
        // 4. 解析文件头部
        UHFPDatFile datFile = parseFileHeader(parser);
        // 5. 解析N个图谱数据
        List<GraphData> graphList = parseGraphDataList(parser, datFile.getGraphCount(), fileData.length);
        datFile.setGraphList(graphList);
        // 6. 解析文件尾部
        parseFileTail(parser, datFile);
        return datFile;
    }*/

    /**
     * 读取文件为字节数组
     */
    private byte[] readFileToBytes(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return data;
        }
    }

    /**
     * 验证CRC32校验（规范4.3.4要求）
     * 文件尾部4字节为CRC32，校验范围：0 ~ L-5（L=文件总长度）
     */
    private void validateCRC32(byte[] fileData) {
        int fileLength = fileData.length;
        if (fileLength < 4) {
            throw new IllegalArgumentException("文件长度不足，不符合CRC32校验要求");
        }
        // 提取文件中的CRC32（小端解析）
        int fileCrc32 = ByteBuffer.wrap(fileData, fileLength - 4, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
        // 计算实际CRC32（校验范围：0 ~ L-5）
        CRC32 crc32 = new CRC32();
        crc32.update(fileData, 0, fileLength - 4);
        long actualCrc32 = crc32.getValue();
        // 对比校验
        if ((int) actualCrc32 != fileCrc32) {
            throw new RuntimeException("CRC32校验失败，文件可能损坏（文件CRC：" + fileCrc32 + "，实际CRC：" + actualCrc32 + "）");
        }
    }

    /**
     * 解析文件头部（对应规范附表5）
     */
    public UHFPDatFile parseFileHeader(LittleEndianParser parser) {
        //System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出0
        UHFPDatFile datFile = new UHFPDatFile();
        // 1. 文件长度（int32，必备）
        datFile.setFileLength(parser.readInt32());
        // 2. 规范版本号（4字节uint8，X.X.X.X）
        byte[] specVersion = new byte[4];
        for (int i = 0; i < 4; i++) {
            specVersion[i] = (byte) parser.readUInt8();
        }
        datFile.setSpecVersion(specVersion);
        // 3. 文件生成时间（int64，YYYYMMDDhhmmssfff）
        datFile.setFileCreateTime(parser.readInt64());
        // 4. 站点名称（UNICODE，118字节）
        datFile.setStationName(parser.readUnicodeString(118));
        System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出0

        // 5. 站点编码（ASCII，42字节）
        datFile.setStationCode(parser.readAsciiString(42));
        // 6. 天气（可选，1字节，未记录=0xFF）
        int weather = parser.readUInt8();
        datFile.setWeather(weather == 0xFF ? null : weather);
        // 7. 环境温度（可选，float）
        datFile.setTemperature(parser.readFloat());
        // 8. 环境湿度（可选，int8）
        byte humidity = parser.readInt8();
        datFile.setHumidity(humidity == 0x7F ? null : humidity); // 假设0x7F为未记录
        // 9. 装置厂家（UNICODE，32字节）
        datFile.setManufacturer(parser.readUnicodeString(32));
        // 10. 装置型号（UNICODE，32字节）
        datFile.setDeviceModel(parser.readUnicodeString(32));
        // 11. 装置版本号（可选，4字节）
        byte[] deviceVersion = new byte[4];
        for (int i = 0; i < 4; i++) {
            deviceVersion[i] = (byte) parser.readUInt8();
        }
        datFile.setDeviceVersion(deviceVersion);
        // 12. 装置序列号（ASCII，32字节）
        datFile.setDeviceSerial(parser.readAsciiString(32));
        // 13. 系统频率（float，Hz）
        datFile.setSystemFrequency(parser.readFloat());
        // 14. 图谱数量N（int16）
        System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出0

        datFile.setGraphCount(parser.readInt16());
        // 15. 跳过头部预留字段（224字节）
        parser.skipBytes(224);
        return datFile;
    }

    /**
     * 解析所有图谱数据（对应规范附表6/7）
     */
    public List<GraphData> parseGraphDataList(LittleEndianParser parser, short graphCount, int fileTotalLength) {
        List<GraphData> graphList = new ArrayList<>(graphCount);
        System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出0
        for (int i = 0; i < graphCount; i++) {
            System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出334
            GraphData graph = new GraphData();
            // 1. 图谱类型编码（uint8，0x21=PRPD=33，0x22=PRPS=34，0x23=峰值统计=35）
            graph.setGraphType(parser.readUInt8());
            // 2. 图谱总长度（int32）
            graph.setGraphDataLength(parser.readInt32());
            // 3. 图谱生成时间（int64）
            graph.setGraphCreateTime(parser.readInt64());
            // 4. 图谱性质（uint8）
            graph.setGraphProperty(parser.readUInt8());
            // 5. 被测设备名称（UNICODE，118字节）
            graph.setDeviceName(parser.readUnicodeString(118));
            // 6. 被测设备编码（ASCII，42字节）
            graph.setDeviceCode(parser.readAsciiString(42));
            // 7. 跳过中间预留字段（规范附表6中序号7前的预留，根据实际偏移计算：14+118+42=174字节，到序号7需跳至334字节，故跳过334-174=160字节）
            // 替换 parser.skipBytes(160); 这一行，实现：读取并打印160字节 + 等效跳过
            //parser.skipBytes(160);
            // 替换 parser.skipBytes(160); 查看跳过的字节
            //byte[] skippedBytes = parser.readBytes(160);
            //String hexStr = ByteUtils.bytesToHexString(skippedBytes);
            //System.out.println("跳过的160字节：" + hexStr);

            //ByteUtils.debugSkippedBytes(parser, 160, "图谱预留字段");
            //System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出334

            // 8. 检测通道标志（可选，int16）
            graph.setChannelFlag(parser.readInt16());
            // 9. 存储数据类型编码（uint8，附表3）
            graph.setDataType(parser.readUInt8());
            // 10. 幅值单位编码（uint8，附表4）
            graph.setAmplitudeUnit(parser.readUInt8());
            // 11. 幅值下限（float）
            graph.setAmplitudeMin(parser.readFloat());
            // 12. 幅值上限（float）
            graph.setAmplitudeMax(parser.readFloat());
            // 13. 频带（可选，uint8）
            int freqBand = parser.readUInt8();
            graph.setFrequencyBand(freqBand == 0xFF ? null : freqBand);
            // 14. 下限频率（可选，float）
            graph.setFreqMin(parser.readFloat());
            // 15. 上限频率（可选，float）
            graph.setFreqMax(parser.readFloat());
            // 16. 相位窗数m（int32）
            graph.setPhaseWindowCount(parser.readInt32());
            // 17. 量化幅值n（PRPD必备，PRPS=0；int32）
            int quantAmplitude = parser.readInt32();
            graph.setQuantAmplitudeCount(quantAmplitude);
            // 18. 工频周期数p（PRPS必备，PRPD=0；int32）
            int powerCycle = parser.readInt32();
            graph.setPowerCycleCount(powerCycle);
            // 19. 放电类型概率（8字节uint8）
            byte[] dischargeProb = new byte[8];
            for (int j = 0; j < 8; j++) {
                dischargeProb[j] = (byte) parser.readUInt8();
            }
            graph.setDischargeProb(dischargeProb);
            // 20. 背景文件名称（ASCII，54字节）
            graph.setBackgroundFileName(parser.readAsciiString(54));
            // 21. 跳过预留字段（PRPD/PRPS为83字节，峰值统计为91字节，根据类型判断）
            int reservedLength = (graph.getGraphType() == 0x23) ? 91 : 83;
            graph.setReservedLength(reservedLength);
            parser.skipBytes(reservedLength);
            // 22. 解析图谱具体数据（核心）
            parseGraphActualData(parser, graph);
            System.out.println("读取文件长度前指针位置：" + parser.getPosition()); // 应输出334
            // 添加到列表
            graphList.add(graph);
        }
        return graphList;
    }

    /**
     * 解析图谱实际数据（PRPD/PRPS/峰值统计）
     */
    private void parseGraphActualData(LittleEndianParser parser, GraphData graph) {
        int dataType = graph.getDataType();
        int k = getTypeByteLength(dataType); // 数据类型对应的字节长度（如float=4）
        if (k < 0) {
            throw new UnsupportedOperationException("不支持的数据类型编码：" + dataType);
        }
        float minY = graph.getAmplitudeMin();
        float maxY = graph.getAmplitudeMax();
        switch (graph.getGraphType()) {
            case 0x21: // PRPD图：d[m][n]，m=相位窗数，n=量化幅值
                List<Object> chartData = new ArrayList();
                int m = graph.getPhaseWindowCount();
                int n = graph.getQuantAmplitudeCount();
                float phaseStep = 360/m;
                float ampStep = (maxY-minY)/n;
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        int count = 0;
                        float floatCount = parser.readFloat();
                        if (floatCount<1.0){
                            continue;
                        }
                        count = Math.round(floatCount);
                        float phase = i*phaseStep;
                        float amplitude = minY+ j * ampStep+ampStep/2;
                        Object[] obj = {phase, amplitude,count};
                        chartData.add(obj);
                    }
                }
                graph.setGraphData(chartData);
                break;
            case 0x22: // PRPS图：d[p][m]，p=工频周期数，m=相位窗数
                int p = graph.getPowerCycleCount();
                m = graph.getPhaseWindowCount();
                /**float[][] prpsData = new float[p][m];
                for (int i = 0; i < p; i++) {
                    for (int j = 0; j < m; j++) {
                        prpsData[i][j] = parser.readFloat();
                    }
                }**/
                // ================ 核心新增：后端完成【所有转换+组装】逻辑 ================
                // 1. 计算单个相位窗对应的角度（核心公式，动态适配任意相位窗数m，不止128）
                float singlePhaseAngle = 360.0F / m;
                // 2. 定义最终返回给前端的三维数据集合（ECharts3D 直接可用）
                List<Object[]> prps3dChartData = new ArrayList<>();
                // 3. 定义无效值过滤阈值（过滤硬件噪声：绝对值小于0.0001的都是无效噪声，必加）
                float noiseThreshold = 1e-4F;

                // 4. 双层循环遍历原始二维数组，完成转换+组装
                // i = 第i个工频周期（工频周期序号，从0开始，对应ECharts3D的X轴）
                // j = 第j个相位窗（对应计算为相位角度，ECharts3D的Y轴）
                // prpsData[i][j] = 当前周期+当前相位窗的脉冲幅值（ECharts3D的Z轴高度）
                for (int i = 0; i < p; i++) {
                    for (int j = 0; j < m; j++) {
                        float amplitude = parser.readFloat();
                        // 过滤无效噪声值，只保留有效局放脉冲数据，减轻前端渲染压力
                        if (Math.abs(amplitude) < noiseThreshold) {
                            continue;
                        }
                        // ✅ 核心转换1：工频周期序号 → 直接用i，对应ECharts3D的X轴
                        float powerCycle = i;
                        // ✅ 核心转换2：相位窗数j → 相位角度 (0~360°)，对应ECharts3D的Y轴
                        float phaseAngle = j * singlePhaseAngle;
                        // ✅ 核心处理：幅值取绝对值，适配3D柱子高度（负数会朝下，必须处理）
                        //float amplitudeAbs = Math.abs(amplitude);
                        // ✅ 组装三维数组：【工频周期, 相位角度, 脉冲幅值】 固定顺序！
                        // 这个顺序完美匹配你的ECharts3D配置：X=周期 Y=角度 Z=幅值
                        Object[] prps3dItem = new Object[]{powerCycle, phaseAngle, amplitude};
                        prps3dChartData.add(prps3dItem);
                    }
                }
                // 最终：将组装好的3D数据返回给前端/存入graph对象，前端直接拿这个数据渲染即可
                graph.setGraphData(prps3dChartData);
                break;
            case 0x23: // 峰值统计图：d[2*m]，m=相位窗数（前m个最大幅值，后m个最大脉冲次数）
                m = graph.getPhaseWindowCount();
                float[] peakData = new float[2 * m];
                for (int i = 0; i < 2 * m; i++) {
                    peakData[i] = parser.readFloat();
                }
                graph.setGraphData(peakData);
                break;
            default:
                throw new UnsupportedOperationException("不支持的图谱类型编码：" + graph.getGraphType());
        }
    }

    /**
     * 根据数据类型编码获取字节长度（对应规范附表3）
     */
    private int getTypeByteLength(int dataType) {
        // 改用 Java 8 支持的传统 switch 语句
        switch (dataType) {
            case 0x00: // int8
                return 0;
            case 0x01: // int8
                return 1;
            case 0x02: // uint8
                return 2;
            case 0x03: // int16
                return 3;
            case 0x04: // int32
                return 4;
            case 0x05: // int64
                return 5;
            case 0x06: // float
                return 6;
            case 0x07: // double
                return 7;
            default:
                return -1;
        }
    }

    /**
     * 解析文件尾部（预留+CRC32）
     */
    public void parseFileTail(LittleEndianParser parser, UHFPDatFile datFile) {
        // 1. 尾部预留（32字节）
        byte[] reservedTail = new byte[32];
        datFile.setReservedTail(reservedTail);
        // 2. CRC32校验码（int32）
        datFile.setCrc32(parser.readInt32());
    }

    // 测试方法
    public static void main(String[] args) {
        UHFPDatParser parser = new UHFPDatParser();
        // 替换为实际.dat文件路径
        //UHFPDatFile datFile = parser.parseDatFile("D:\\jk_project\\file\\dat\\MerkPDEC3000_CH-1-5_20251225144500358.dat");
        //UHFPDatFile datFile = parser.parseDatFile("D:\\jk_project\\file\\dat\\xiaocai\\500kV孝彩站_频谱_500kV孝霸二线5032外置传感器A相_20251226162348784.dat");

        // 输出解析结果（示例）
        /**System.out.println("文件总长度：" + datFile.getFileLength());
         System.out.println("站点名称：" + datFile.getStationName());
         System.out.println("图谱数量：" + datFile.getGraphCount());
         System.out.println("第一个图谱类型：" + datFile.getGraphList().get(0).getGraphType());
         System.out.println("CRC32校验码：" + datFile.getCrc32());**/
    }
}
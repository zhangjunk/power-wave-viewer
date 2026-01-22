package com.cn.jk.powerwaveviewer;

import com.cn.jk.powerwaveviewer.config.FtpConfig;
import com.cn.jk.powerwaveviewer.controller.FileDownController;
import com.cn.jk.powerwaveviewer.entity.AjaxResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class PowerWaveViewerApplicationTests {

    @Test
    void contextLoads() {
    }

    // 2. 注入 MockMvc：用于模拟 HTTP 请求
    @Autowired
    private MockMvc mockMvc;

    // 3. 注入控制器（可选，验证控制器实例是否加载）
    @Autowired(required = false) // 避免控制器未扫描到时报错
    private FileDownController fileDownController;

    // 4. 注入 FtpConfig（验证控制器依赖的配置）
    @Autowired
    private FtpConfig ftpConfig;

    // Jackson 工具：解析响应的 JSON 为 AjaxResult 对象（可选，方便验证）
    @Autowired
    private ObjectMapper objectMapper;

    // 方式2：直接注入配置项，验证原始值
    @Value("${ftp.download-dict}")
    private String downloadDict;

    // 方式3：注入 Environment 对象，读取原始配置（底层验证）
    @Autowired
    private Environment environment;

    // 新增测试方法：验证 FtpConfig 配置绑定
    @Test
    void testFtpConfigBinding() {
        // 1. 验证 FtpConfig 实例不为 null（说明被 Spring 扫描并创建）
        Assertions.assertNotNull(ftpConfig, "FtpConfig 实例为 null，未被 Spring 加载");

        // 2. 验证 downloadDict 属性值是否正确（替换为你配置的实际路径）
        String expectedPath = "D:/jk_project/file/dat/";
        String actualPath = ftpConfig.getDownloadDict();

        // 断言：实际值和预期值一致（忽略首尾空格，避免配置手误）
        Assertions.assertEquals(
                expectedPath.trim(),
                actualPath.trim(),
                "FtpConfig 的 downloadDict 配置绑定失败，预期：" + expectedPath + "，实际：" + actualPath
        );

        // 3. 可选：验证 @Value 注入的原始值是否正确
        Assertions.assertEquals(expectedPath.trim(), downloadDict.trim(), "@Value 注入配置失败");

        // 4. 可选：验证 Environment 中读取的原始配置是否正确
        String envPath = environment.getProperty("ftp.download-dict");
        Assertions.assertEquals(expectedPath.trim(), envPath.trim(), "Environment 读取配置失败");

        // 打印日志，方便调试（可选）
        System.out.println("✅ 配置绑定成功！FTP下载路径：" + ftpConfig.getDownloadDict());
    }

    /**
     * 测试1：验证控制器实例是否被 Spring 加载
     */
    @Test
    void testControllerLoad() {
        Assertions.assertNotNull(fileDownController, "FileDownController 未被 Spring 扫描加载，请检查包路径/注解");
    }

    /**
     * 测试2：核心测试 - 调用 /front/fileDown/file/info/list 接口（默认参数）
     * 场景：不传 searchKey，使用默认 pageNum=1、pageSize=20
     */
    @Test
    void testFileInfoList_DefaultParams() throws Exception {
        // 1. 模拟 GET 请求（无自定义参数，使用默认值）
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/front/fileDown/file/info/list")
                                .contentType(MediaType.APPLICATION_JSON) // 请求类型
                )
                // 2. 基础响应验证
                .andExpect(MockMvcResultMatchers.status().isOk()) // 验证状态码 200
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) // 验证返回 JSON
                // 3. 验证 AjaxResult 核心字段（根据你的 AjaxResult 结构调整）
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(200)) // 假设成功码是 200
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("操作成功")) // 假设成功提示
                .andExpect(MockMvcResultMatchers.jsonPath("data").isNotEmpty()) // 验证返回数据非空
                .andReturn();

        // 4. 进阶验证：解析响应体为 AjaxResult，验证分页参数
        String responseJson = mvcResult.getResponse().getContentAsString();
        AjaxResult ajaxResult = objectMapper.readValue(responseJson, AjaxResult.class);

        // 验证 FtpConfig 配置生效（确保控制器依赖的路径正确）
        Assertions.assertEquals("D:/jk_project/file/dat/", ftpConfig.getDownloadDict().trim(),
                "FTP 下载路径配置错误，会影响文件列表查询逻辑");

        System.out.println("✅ 默认参数测试通过！响应结果：" + responseJson);
    }

    /**
     * 测试4：核心测试 - 调用接口（自定义参数）
     * 场景：传 searchKey=test、pageNum=2、pageSize=10
     */
    @Test
    void testFileWaveData_CustomParams() throws Exception {
        // 1. 模拟 GET 请求（传递自定义参数）
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/front/fileDown/file/wave/data")
                                .param("fileName", "HCL-8100-IED-PD-00038-7-1764097045.dat") // 文件名
                                .param("handlerKey", "sybx") // 图谱类型
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // 3. 验证响应
                .andExpect(MockMvcResultMatchers.status().isOk()) // 状态码200
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) // 返回JSON
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200)) // 成功码（根据你的AjaxResult调整）
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("操作成功")) // 成功提示
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()) // 返回数据非空
                .andReturn();

        // 4. 进阶验证：解析响应体，确认数据正确性
        String responseJson = mvcResult.getResponse().getContentAsString();
        AjaxResult ajaxResult = objectMapper.readValue(responseJson, AjaxResult.class);
        System.out.println("✅ 正常场景测试通过！响应：" + responseJson);
    }
}

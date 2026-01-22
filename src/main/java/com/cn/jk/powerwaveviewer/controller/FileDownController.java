package com.cn.jk.powerwaveviewer.controller;

import com.cn.jk.powerwaveviewer.config.FtpConfig;
import com.cn.jk.powerwaveviewer.entity.AjaxResult;
import com.cn.jk.powerwaveviewer.entity.FtpFileInfo;
import com.cn.jk.powerwaveviewer.entity.TableDataInfo;
import com.cn.jk.powerwaveviewer.entity.WaveChartData;
import com.cn.jk.powerwaveviewer.handle.DataWaveHandler;
import com.cn.jk.powerwaveviewer.handle.DataWaveHandlerFactory;
import com.cn.jk.powerwaveviewer.utils.CommonUtils;
import com.cn.jk.powerwaveviewer.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/front/fileDown"})
public class FileDownController extends BaseController {

    @Autowired
    private FtpConfig ftpConfig;
    @Autowired
    private CommonUtils commonUtils;

    private static final Logger log = LoggerFactory.getLogger(FileDownController.class);

    @GetMapping({"/file/info/list"})
    public AjaxResult fileInfoList(@RequestParam(name = "searchKey", required = false) String searchKey, @RequestParam(name = "pageNum", defaultValue = "1") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        String basePath = this.ftpConfig.getDownloadDict();
        File directoryFiles = new File(basePath);

        FilenameFilter filter = (dir, name) -> (StringUtils.isBlank(searchKey) || name.contains(searchKey));
        File[] fileList = directoryFiles.listFiles(filter);
        long total = Arrays.<File>stream(fileList).count();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        List<FtpFileInfo> list = (List<FtpFileInfo>) Arrays.<File>stream(directoryFiles.listFiles(filter)).sorted(Collections.reverseOrder()).skip((pageNum - 1) * pageSize).limit(pageSize).map(item -> {
            FtpFileInfo info = new FtpFileInfo();
            info.setFileName(item.getName());
            info.setFileType(Integer.valueOf(0));
            info.setFilePath(item.getPath());
            info.setCreateTime(sdf.format(Long.valueOf(item.lastModified())));
            info.setFileSize(Long.valueOf(item.length()));
            info.setHasChildren(Boolean.valueOf(false));
            return info;
        }).collect(Collectors.toList());

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("请求成功");
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(total);

        return success(tableDataInfo);
    }

    @GetMapping({"/file/station/list"})
    public AjaxResult fileStationList(@RequestParam(name = "station", required = false) String station, @RequestParam(name = "pageNum", defaultValue = "1") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize
        ,@RequestParam(name = "fileName", required = false) String fileName,@RequestParam(name = "fileType", required = false) String fileType,@RequestParam(name = "beginDate", required = false) String beginDate
        ,@RequestParam(name = "endDate", required = false) String endDate) {
        String fullPathMessage = commonUtils.getFullPath(station);
        File directoryFiles = new File(fullPathMessage);
        //默认查询近一个月的数据
        String beginDate1 = DateUtils.get30DaysAgoDate();
        String endDate1 = DateUtils.getTodayDate();
        log.info("最近一个月日期范围：{},{}",beginDate1,endDate1);

        if (!directoryFiles.exists()) {
            return error(fullPathMessage);
        }
        FilenameFilter filter = (dir, name) -> {
            boolean match = true; // 默认匹配
            // 条件1：兼容原逻辑 - station为空时不限制该条件，否则必须包含station
            boolean condition1 = StringUtils.isBlank(station) || name.contains(station);
            match = match && name.contains(station);

            // 条件2：第二个关键词非空时，必须包含该关键词
            if (StringUtils.isNotBlank(fileName)) {
                match = match && name.contains(fileName);
            }

            // 条件3：文件后缀非空时，必须匹配该后缀
            if (StringUtils.isNotBlank(fileType)) {
                match = match && name.contains(fileType);
            }
            //匹配开始日期和结束日期
            if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)){
                // 核心调用：判断文件是否在日期范围内
                boolean isInRange = CommonUtils.isFileTimeInDateRange(name, beginDate, endDate);
                match = match && isInRange;
            }else{
                boolean isInRange = CommonUtils.isFileTimeInDateRange(name, beginDate1, endDate1);
                match = match && isInRange;
            }
            return match;
        };
        File[] fileList = directoryFiles.listFiles(filter);
        long total = Arrays.<File>stream(fileList).count();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        List<FtpFileInfo> list = Arrays.stream(directoryFiles.listFiles(filter)).sorted(Collections.reverseOrder()).skip((pageNum - 1) * pageSize).limit(pageSize).map(item -> {
            FtpFileInfo info = new FtpFileInfo();
            info.setFileName(item.getName());
            info.setFileType(Integer.valueOf(0));
            info.setFilePath(item.getPath());
            info.setCreateTime(sdf.format(Long.valueOf(item.lastModified())));
            info.setFileSize(Long.valueOf(item.length()));
            info.setHasChildren(Boolean.valueOf(false));
            return info;
        }).collect(Collectors.toList());

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("请求成功");
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(total);

        return success(tableDataInfo);
    }

    @GetMapping({"/file/wave/data"})
    public AjaxResult fileWaveData(@RequestParam(name = "station", required = false) String station, @RequestParam("fileName") String fileName, @RequestParam("handlerKey") String handlerKey) {
        if (StringUtils.isBlank(fileName)) {
            return error("参数有误");
        }
        String fullPathMessage = commonUtils.getFullPath(station);
        String fullPath = Paths.get(fullPathMessage, fileName).toString();
        File directoryFiles = new File(fullPath);

        if (!directoryFiles.exists()) {
            return error(fullPathMessage);
        }
        String prefix = "";
        if (StringUtils.isBlank(station) || station.contains("龙河")){
            prefix="hcl_";
        }else{
            prefix="uhfp_";
        }
        FileInputStream inputStream = null;
        byte[] bytes = null;
        try {
            inputStream = new FileInputStream(fullPath);
            int data;
            /****性能更好 start****/
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((data = inputStream.read()) != -1) {
                bos.write(data); // 直接写入字节，无需包装
            }
            bytes = bos.toByteArray(); // 直接得到byte数组，无需手动循环转换
            /****性能更好 end****/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception exception) {
                }
            }
        }
        WaveChartData waveChartData = new WaveChartData();
        if (bytes != null) {
            DataWaveHandler handler = DataWaveHandlerFactory.handlers.get(prefix+handlerKey);
            if (handler != null) {
                handler.handle(waveChartData, bytes);
            }
        }
        return success(waveChartData);
    }

    @GetMapping("/file/download")
    public ResponseEntity<byte[]> downloadFileByGet(@RequestParam(name = "station", required = true) String station,@RequestParam String fileName) {
        String basePath = this.ftpConfig.getDownloadDict();
        String fullPathMessage = commonUtils.getFullPath(station);
        Path targetFilePath = Paths.get(fullPathMessage, fileName).toAbsolutePath().normalize();

        // 2. 核心校验：检查目标路径是否在基础路径范围内（自动处理分隔符问题）
        if (!targetFilePath.startsWith(basePath)) {
            // 路径越权，返回403禁止访问
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // 3. 检查文件是否存在且是普通文件
        File targetFile = targetFilePath.toFile();

        // 安全校验：只允许下载指定目录下的文件
        /**File canonicalFile = file.getCanonicalFile();
         if (!canonicalFile.getPath().startsWith(basePath)) {
         return new ResponseEntity<>(HttpStatus.FORBIDDEN);
         }**/
        if (!targetFile.exists() || !targetFile.isFile()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return downloadFile(targetFile);
    }
    private ResponseEntity<byte[]> downloadFile(File targetFile) {
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(targetFile);
            //设置响应头，触发浏览器下载
            HttpHeaders headers = new HttpHeaders();
            // 编码文件名，避免中文乱码
            //String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            String encodedFileName = URLEncoder.encode(targetFile.getName(), "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //返回文件内容和响应头
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


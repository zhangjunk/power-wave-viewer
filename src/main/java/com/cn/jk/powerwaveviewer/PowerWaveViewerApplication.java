package com.cn.jk.powerwaveviewer;

import com.cn.jk.powerwaveviewer.config.StationMappingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class PowerWaveViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PowerWaveViewerApplication.class, args);
    }

}

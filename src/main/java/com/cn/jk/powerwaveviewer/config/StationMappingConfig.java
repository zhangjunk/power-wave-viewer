package com.cn.jk.powerwaveviewer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Configuration // 标记为配置类（和你原有代码保持一致）
@PropertySource(
        value = "classpath:station-mapping.properties", // 指定自定义配置文件路径
        encoding = "UTF-8" // 解决中文key/value乱码，必须加
)
public class StationMappingConfig {

    private final Environment env;
    private Map<String, String> stationMap = new HashMap<>();
    private static final String PREFIX = "station.map.";

    public StationMappingConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void autoLoadStationMap() {
        // ======== 适配JDK 1.8的核心修改 ========
        // 第一步：判断类型
        if (env instanceof AbstractEnvironment) {
            // 第二步：手动强转（1.8必须这么写）
            AbstractEnvironment abstractEnv = (AbstractEnvironment) env;
            // 后续逻辑完全不变
            abstractEnv.getPropertySources().forEach(propertySource -> {
                if (propertySource instanceof MapPropertySource) {
                    MapPropertySource mapPropertySource = (MapPropertySource) propertySource;
                    mapPropertySource.getSource().forEach((key, value) -> {
                        if (key.startsWith(PREFIX) && value instanceof String) {
                            String shortKey = key.substring(PREFIX.length());
                            stationMap.put(shortKey, (String) value);
                            System.out.println("✅ 自动加载：" + shortKey + " = " + value);
                        }
                    });
                }
            });
        }
        // ======================================
        System.out.println("✅ 自动加载完成，共加载" + stationMap.size() + "个站点映射");
    }

    public String getValueByKey(String key) {
        return stationMap.getOrDefault(key, "");
    }

    public Map<String, String> getStationMap() {
        return stationMap;
    }
}

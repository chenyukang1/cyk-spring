package com.cyk.spring.web;

import com.cyk.spring.ioc.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * The class ConfigLoader
 *
 * @author yukang.chen
 * @date 2025/7/7
 */
public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public static Properties load() {
        Properties properties = new Properties();
        Map<String, Object> yamlMap = YamlUtils.loadYamlAsPlainMap("/application.yml");
        yamlMap.forEach((key, value) -> {
            if (value instanceof String strValue) {
                properties.put(key, strValue);
            }
        });
        logger.debug("Loaded properties from application.yml: {}", properties);
        return properties;
    }
}

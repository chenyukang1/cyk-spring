package com.cyk.spring.ioc.utils;

import com.cyk.spring.ioc.exception.ClassPathException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parse yaml by snakeyaml:
 * 
 * https://github.com/snakeyaml/snakeyaml
 */
@SuppressWarnings("unused")
public class YamlUtils {

    public static Map<String, Object> loadYaml(String path) throws ClassPathException {
        var loaderOptions = new LoaderOptions();
        var dumperOptions = new DumperOptions();
        var representer = new Representer(dumperOptions);
        var resolver = new NoImplicitResolver();
        var yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions, loaderOptions, resolver);
        return ClassPathUtils.readInputStream(path, yaml::load);
    }

    public static Map<String, Object> loadYamlAsPlainMap(String path) {
        Map<String, Object> plain = new LinkedHashMap<>();
        try {
            Map<String, Object> data = loadYaml(path);
            convertTo(data, "", plain);
        } catch (ClassPathException e) {
            return new HashMap<>();
        }

        return plain;
    }

    static void convertTo(Map<String, Object> source, String prefix, Map<String, Object> plain) {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                convertTo(subMap, prefix + key + ".", plain);
            } else if (value instanceof List) {
                plain.put(prefix + key, value);
            } else {
                plain.put(prefix + key, value.toString());
            }
        }
    }

    /**
     * Disable ALL implicit convert and treat all values as string.
     */
    private static class NoImplicitResolver extends Resolver {

        public NoImplicitResolver() {
            super();
            super.yamlImplicitResolvers.clear();
        }
    }
}


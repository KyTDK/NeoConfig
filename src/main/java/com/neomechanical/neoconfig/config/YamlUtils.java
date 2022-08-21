package com.neomechanical.neoconfig.config;

import java.util.Map;

public class YamlUtils {
    public static boolean isConfigurationSection(Map<String, Object> data, String key) {
        for (Object value : data.values()) {
            if (value instanceof Map) {
                if (value.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean getConfigurationSection(Map<String, Object> data, String key) {
        for (Object value : data.values()) {
            if (value instanceof Map) {
                if (value.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}

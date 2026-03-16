package com.monkcommerce.coupons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkcommerce.coupons.exception.BadRequestException;

import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid detailsJson format");
        }
    }

    public static <T> T convert(Object object, Class<T> clazz) {
        return mapper.convertValue(object, clazz);
    }
}
package br.com.gustavoedev.orders_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static String objectToJSON(Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package com.ecomerce.postmaster.common;

import com.ecomerce.postmaster.common.exception.JsonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

public record GenericJsonDeserializer<T>(ObjectMapper objectMapper,
                                         TypeReference<T> typeReference) implements Deserializer<T> {
    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) return null;
        String s = new String(data, StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(s, typeReference);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }
    }
}
package com.myapp.demo.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.r2dbc.postgresql.codec.Json;

import java.io.IOException;

public class PgJsonObjectDeserializer extends JsonDeserializer<Json> {
    @Override
    public Json deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        var value = ctxt.readTree(p);

        return Json.of(value.toString());
    }
}

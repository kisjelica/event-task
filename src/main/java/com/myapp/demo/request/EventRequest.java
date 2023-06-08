package com.myapp.demo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.myapp.demo.serializers.PgJsonObjectDeserializer;
import com.myapp.demo.serializers.PgJsonObjectSerializer;
import io.r2dbc.postgresql.codec.Json;
import lombok.Data;


import java.util.List;

@Data
public class EventRequest {
    private String source;
    @JsonDeserialize(using = PgJsonObjectDeserializer.class)
    private Json args;
    private String url;
}

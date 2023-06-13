package com.myapp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.myapp.demo.serializers.PgJsonObjectDeserializer;
import com.myapp.demo.serializers.PgJsonObjectSerializer;


import io.r2dbc.postgresql.codec.Json;
import lombok.Data;

import org.springframework.data.annotation.Id;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class Event {
    @Id
    @JsonIgnore
    private Long id;

    private String source;

    @JsonDeserialize(using = PgJsonObjectDeserializer.class)
    @JsonSerialize(using = PgJsonObjectSerializer.class)
    private Json args;

    private String url;

    private Instant recordedAt;


}

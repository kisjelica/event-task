package com.myapp.demo.dto;

import com.myapp.demo.model.Event;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

import java.util.List;

@Getter
@Setter
public class ResponseDTO {
    private List<Event> items;
    private String self;
    private String next;
    private String prev;
    private String last;
    private String first;
}

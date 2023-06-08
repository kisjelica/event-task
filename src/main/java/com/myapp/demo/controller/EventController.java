package com.myapp.demo.controller;

import com.myapp.demo.dto.ResponseDTO;
import com.myapp.demo.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.myapp.demo.request.EventRequest;
import com.myapp.demo.service.EventService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/events")

public class EventController {

    private final EventService service;

    @Autowired
    public EventController(EventService eventService){
        this.service = eventService;
    }

    @PostMapping("/create")
    public Mono<Event> postEvent(@RequestBody EventRequest request){
       return service.save(request);
    }

    @GetMapping("/list")
    public Mono<ResponseDTO> getEvents(@RequestParam(required = false) List<String> url,
                                                       @RequestParam(required = false) Instant date_greater_than,
                                                       @RequestParam(required = false) Instant date_less_than,
                                                       @RequestParam(required = false) String cursor,
                                                       @RequestParam(required = false) Integer limit) {

        return (service.getAll(url, date_greater_than, date_less_than, cursor, limit));
    }


}

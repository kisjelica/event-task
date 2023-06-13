package com.myapp.demo.controller;

import com.myapp.demo.dto.ResponseDTO;
import com.myapp.demo.model.Event;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public Mono<Event> postEvent(@RequestBody EventRequest request){
       return service.save(request);
    }

    @GetMapping
    public Mono<ResponseDTO> getEvents(@RequestParam(required = false) List<String> url,
                                                       @RequestParam(name="date_greater_than",required = false) Instant dateGreaterThan,
                                                       @RequestParam(name="date_less_than",required = false) Instant dateLessThan,
                                                       @RequestParam(required = false) String cursor,
                                                       @RequestParam(required = false) Integer limit) {
        System.out.println(dateGreaterThan);
        return (service.getAll(url, dateGreaterThan, dateLessThan, cursor, limit));
    }


}

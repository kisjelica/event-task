package com.myapp.demo.service;

import com.myapp.demo.dto.ResponseDTO;
import com.myapp.demo.model.Event;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.myapp.demo.repository.EventRepository;
import com.myapp.demo.request.EventRequest;
import reactor.util.function.Tuple2;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service

public class EventService {

    @Autowired private EventRepository repository;

    static final int DEFAULT_LIMIT = 10;
    static final String CURSOR_TOKEN = "indexProperty=id&cursorPos=";

    public Mono<Event> save(EventRequest request) {
        Event event = new Event();
        event.setSource(request.getSource());
        event.setArgs(request.getArgs());
        event.setUrl(request.getUrl());
        event.setRecordedAt(Instant.now());
        return repository.save(event);
    }

    public Mono<ResponseDTO> getAll(List<String> url, Instant dateGreaterThan, Instant dateLessThan, String cursor, Integer limit){
        if(dateGreaterThan == null) dateGreaterThan = Instant.MIN;
        if(dateLessThan == null) dateLessThan = Instant.MAX;
        if(cursor == null) cursor = "";
        if(limit == null) limit = DEFAULT_LIMIT;
       
        Long id = decodeCursor(cursor);
        if(id == null) id = 0l;


        Flux<Event> events = findAll(url, dateGreaterThan, dateLessThan, id).take(limit);

        Mono<Long> maxId = repository.count();


        Long finalId = id;
        Integer finalLimit = limit;
        String finalCursor = cursor;
        return Mono.zip(maxId, events.collectList()).map(
                res-> mapResponseDTO(finalCursor, finalId, finalLimit, res)
        );
    }

    public Flux<Event> findAll(List<String> url, Instant dateGreaterThan, Instant dateLessThan, Long id) {
        if(url != null)
            return repository.findAllByUrlInAndRecordedAtBetweenAndIdGreaterThanEqual(url, dateGreaterThan, dateLessThan, id);
        else
            return repository.findAllByRecordedAtBetweenAndIdGreaterThanEqual(dateGreaterThan, dateLessThan,id);
    }

    private ResponseDTO mapResponseDTO(String finalCursor, Long finalId, Integer finalLimit, Tuple2<Long, List<Event>> res) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setItems(res.getT2());
        responseDTO.setSelf(finalCursor);
        responseDTO.setNext(encodeCursor(Math.min(res.getT1(), finalId + finalLimit)));
        responseDTO.setFirst(encodeCursor(0l));
        responseDTO.setPrev(encodeCursor(Math.max(0l, finalId - finalLimit)));
        responseDTO.setLast(encodeCursor(res.getT1() - finalLimit));
        return responseDTO;
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty())  {
            return null;
        }
        byte[] decodedBytes = Base64.getUrlDecoder().decode(cursor);
        String decodedCursor = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] parsedCursor = decodedCursor.split("=");
        System.out.println("decoded: " + decodedCursor);
        return Long.parseLong(parsedCursor[2]);
    }

    private String encodeCursor(Long id) {
        String cursorToken = CURSOR_TOKEN + id.toString();
        return Base64.getEncoder().encodeToString(cursorToken.getBytes(StandardCharsets.UTF_8));
    }
}
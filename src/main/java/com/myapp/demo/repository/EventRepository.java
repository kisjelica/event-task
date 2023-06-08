package com.myapp.demo.repository;

import com.myapp.demo.model.Event;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;


public interface EventRepository extends R2dbcRepository<Event, Long> {


    @Query("SELECT * FROM event WHERE (length(:urls) = 0 OR url IN (:urls)) AND recorded_at > :date_greater_than AND recorded_at < :date_less_than AND id >= :id ORDER BY id ASC LIMIT :limit")
    Flux<Event> findAll(@Param("urls") List<String> urls, @Param("date_greater_than")Instant date_greater_than,@Param("date_less_than") Instant date_less_than, @Param("id")Long id, @Param("limit") Integer limit);
}

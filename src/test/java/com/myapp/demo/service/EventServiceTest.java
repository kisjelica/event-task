package com.myapp.demo.service;

import com.myapp.demo.model.Event;
import com.myapp.demo.repository.EventRepository;
import com.myapp.demo.request.EventRequest;
import io.r2dbc.postgresql.codec.Json;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {EventServiceTest.Initializer.class})
public class EventServiceTest {
    @Autowired
    private EventRepository repository;

    @Autowired
    private EventService eventService;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
            .withDatabaseName("postgresDB")
            .withUsername("postgresUser")
           .withPassword("postgresPW").withReuse(true);


    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }


    @Test
    public void testSave() {
        Event event = new Event();
        event.setSource("source");
        event.setArgs(Json.of("[\n" +
                "                {\n" +
                "                    \"lifted_ingredients_count\": 4\n" +
                "                },\n" +
                "                {\n" +
                "                    \"has_about\": true\n" +
                "                }\n" +
                "            ]"));
        event.setUrl("url");
        event.setRecordedAt(Instant.now());


        Mono<Event> savedEvent = repository.save(event);

        Assertions.assertThat(savedEvent.block()).usingRecursiveComparison().ignoringFields("id").isEqualTo(event);

    }

    @Test
    public void testGetAllUrl(){
        String randomString = UUID.randomUUID().toString();
        Event event = new Event();
        event.setSource("source");
        event.setArgs(Json.of("[\n" +
                "                {\n" +
                "                    \"lifted_ingredients_count\": 4\n" +
                "                },\n" +
                "                {\n" +
                "                    \"has_about\": true\n" +
                "                }\n" +
                "            ]"));
        event.setUrl(randomString);
        event.setRecordedAt(Instant.now());

        Mono<Event> savedEvent = repository.save(event);
        Long id = savedEvent.block().getId();

        Flux<Event> retrievedEvents = eventService.findAll(Collections.singletonList(randomString), Instant.MIN, Instant.MAX, id);

        Assertions.assertThat(retrievedEvents.collectList().block().size()).isEqualTo(1);
    }

    @Test
    public void testGetAllTimestamp(){
        Event event1 = new Event();
        event1.setSource("source");
        event1.setArgs(Json.of("{}"));
        event1.setUrl("url");
        event1.setRecordedAt(Instant.now());


        Instant dateGreaterThan =Instant.now().minusSeconds(1);
        Instant dateLessThan = Instant.now().plusSeconds(1);

        Event event2 = new Event();
        event2.setSource("source");
        event2.setArgs(Json.of("{}"));
        event2.setUrl("url");
        event2.setRecordedAt(dateGreaterThan);

        Event event3 = new Event();
        event3.setSource("source");
        event3.setArgs(Json.of("{}"));
        event3.setUrl("url");
        event3.setRecordedAt(dateLessThan);

        Long id1 = repository.save(event1).block().getId();
        Long id2 = repository.save(event2).block().getId();
        Long id3 = repository.save(event3).block().getId();

        List<Event> retrievedEvents = eventService.getAll(null,  dateGreaterThan.plusMillis(1), dateLessThan.minusMillis(1), "", 100).block().getItems();



        Assertions.assertThat(retrievedEvents.size()).isEqualTo(1);

    }

}


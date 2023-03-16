package com.hkhexbook.restapitddservice.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("REST API learning")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){

        String name = "Event";
        String spring = "spring";

        Event event = new Event();
        event.setName(name);
        event.setDescription(spring);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(spring);
    }

}
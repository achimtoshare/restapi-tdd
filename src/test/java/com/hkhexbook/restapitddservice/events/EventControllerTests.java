package com.hkhexbook.restapitddservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkhexbook.restapitddservice.common.RestDocsConfiguration;
import com.hkhexbook.restapitddservice.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;


import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

 //   @MockBean                 애노테이션 SpringBootTest, AutoConfigureMokMvc추가함으로 필요없어짐
 //   EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception{
        EventDto event= EventDto.builder()
                .name("Spring")
                .description("REST API with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,3, 15, 16, 15))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,3, 16, 16, 15))
                .beginEventDateTime(LocalDateTime.of(2023,3, 16, 16, 15))
                .endEventDateTime(LocalDateTime.of(2023,3, 17, 23, 15))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();
       // event.setId(10);
       //  Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("free").value(false))


                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-events").exists())
                .andDo(document("create-event",
                        links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-events").description("link to query events"),
                            linkWithRel("update-events").description("link to update events")
                        ),
                        requestHeaders(
                            headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        requestFields(
                               fieldWithPath("name").description("Name of new event"),
                               fieldWithPath("description").description("description of new event"),
                               fieldWithPath("beginEnrollmentDateTime").description("date time of begin new event"),
                               fieldWithPath("closeEnrollmentDateTime").description("date time of close new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin new event"),
                                fieldWithPath("endEventDateTime").description("date time of close new event"),
                                fieldWithPath("location").description("location of close new event"),
                                fieldWithPath("basePrice").description("base price of close new event"),
                                fieldWithPath("maxPrice").description("max price of close new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of close new event")

                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        requestFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin new event"),
                                fieldWithPath("endEventDateTime").description("date time of close new event"),
                                fieldWithPath("location").description("location of close new event"),
                                fieldWithPath("basePrice").description("base price of close new event"),
                                fieldWithPath("maxPrice").description("max price of close new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of close new event"),
                                fieldWithPath("free").description("it tells if this is free or not"),
                                fieldWithPath("offline").description("it tells if this is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-events.href").description("link to update events")

                        )


                 ))
                ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception{
        Event event= Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,3, 15, 16, 15))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,3, 16, 16, 15))
                .endEventDateTime(LocalDateTime.of(2023,3, 17, 23, 15))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();
        // event.setId(10);
        //  Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())

                 ;
    }

    @Test
    @TestDescription("입력 값이 비어 있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_input() throws  Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void createEvent_Bad_Request_Empty_Wrong_input() throws  Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,3, 26, 16, 15))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,3, 25, 16, 15))
                .beginEventDateTime(LocalDateTime.of(2023,3, 24, 16, 15))
                .endEventDateTime(LocalDateTime.of(2023,3, 23, 23, 15))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }
}

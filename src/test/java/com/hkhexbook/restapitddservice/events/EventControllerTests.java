package com.hkhexbook.restapitddservice.events;

import com.hkhexbook.restapitddservice.accounts.Account;
import com.hkhexbook.restapitddservice.accounts.AccountRepository;
import com.hkhexbook.restapitddservice.accounts.AccountRole;
import com.hkhexbook.restapitddservice.accounts.AccountService;
import com.hkhexbook.restapitddservice.common.AppProperties;
import com.hkhexbook.restapitddservice.common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;


import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseTest {

    @Autowired
    EventRepository repository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp(){
        this.repository.deleteAll();
        this.accountRepository.deleteAll();

    }

 //   @MockBean                 애노테이션 SpringBootTest, AutoConfigureMokMvc추가함으로 필요없어짐
 //   EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
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
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
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
                            linkWithRel("update-events").description("link to update events"),
                            linkWithRel("profile").description("link to profile")
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
                        relaxedResponseFields(
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
                                fieldWithPath("_links.update-events.href").description("link to update events"),
                                fieldWithPath("_links.profile.href").description("link to profile")


                        )


                 ))
                ;
    }

    private String getBearToken(boolean needToCreateAccount) throws Exception {
        return "Bearer "+getAccessToken(needToCreateAccount);
    }




    private String getAccessToken(boolean needToCreateAccount) throws Exception {

        if(needToCreateAccount){
            createAccount();
        }

        ResultActions perform =  this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                .param("username",appProperties.getUserUsername())
                .param("password",appProperties.getUserPassword())
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        Account hkh = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        return this.accountService.saveAccount(hkh);
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())

                 ;
    }

    @Test
    @DisplayName("입력 값이 비어 있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_input() throws  Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
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
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }
    
    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception{
        //Given
        IntStream.range(0,30).forEach(this::generateEvent);

        //When
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size","10")
                .param("sort","name,DESC")

                 )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Account account = this.createAccount();
        Event event = this.generateEvent(200,account);

        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))

                ;

    }

    @Test
    @DisplayName("없는 이벤트는 조회했을 때 404응답받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/11111"))
                .andExpect(status().isNotFound())
        ;
    }



    @Test
    @DisplayName("수정하려는 이벤트가 없는 경우")
    public void updateEvent404() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/123123")
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }





    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //계정 만들어서 이벤트에 매니저로 셋팅
        Account account = this.createAccount();
        Event event = this.generateEvent(100,account);

        EventDto upEvent = this.modelMapper.map(event,EventDto.class);
        String eventName = "updated Event";
        upEvent.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken(false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upEvent))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-an-event"))
                ;

    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정실패")
    public void updateEvent400wrong() throws Exception {

        String eventName = "updated Event";
        Event event = this.generateEvent(100);
        EventDto upEvent = this.modelMapper.map(event,EventDto.class);
        upEvent.setBasePrice(20000);
        upEvent.setMaxPrice(1000);

        this.mockMvc.perform(put("/api/events/{id}", event.getId(),upEvent)
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upEvent))
                 )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
     @Test
    @DisplayName("입력값이 빈 경우에 이벤트 수정실패")
    public void updateEvent400empty() throws Exception {

        String eventName = "updated Event";
        Event event = this.generateEvent(100);
        EventDto upEvent = new EventDto();
        upEvent.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}", event.getId(),upEvent)
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(upEvent))
        )
                .andExpect(status().isBadRequest())
        ;

    }
    private Event generateEvent(int idx, Account account){
        Event event = buildEvent(idx);
        event.setManager(account);
        return this.repository.save(event);
    }
    private Event generateEvent(int idx) {
        Event event = buildEvent(idx);
        return this.repository.save(event);
    }

    private Event buildEvent(int idx) {
        return Event.builder()
                .name("event" + idx)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 3, 15, 16, 15))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 3, 16, 16, 15))
                .beginEventDateTime(LocalDateTime.of(2023, 3, 16, 16, 15))
                .endEventDateTime(LocalDateTime.of(2023, 3, 17, 23, 15))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception{
        //Given
        IntStream.range(0,30).forEach(this::generateEvent);

        //When
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearToken(true))
                .param("page", "1")
                .param("size","10")
                .param("sort","name,DESC")

        )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }
}

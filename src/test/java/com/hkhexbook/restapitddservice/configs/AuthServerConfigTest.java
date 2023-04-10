package com.hkhexbook.restapitddservice.configs;

import com.hkhexbook.restapitddservice.accounts.Account;
import com.hkhexbook.restapitddservice.accounts.AccountRepository;
import com.hkhexbook.restapitddservice.accounts.AccountRole;
import com.hkhexbook.restapitddservice.accounts.AccountService;
import com.hkhexbook.restapitddservice.common.AppProperties;
import com.hkhexbook.restapitddservice.common.BaseControllerTest;
import com.hkhexbook.restapitddservice.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp(){
        this.accountRepository.deleteAll();
    }


    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
// 이미 어플리케이션 실행할때 admin, user 유저 생성해주기때문. 필요없음.
//        Account hkh = Account.builder()
//                .email(appProperties.getUserUsername())
//                .password(appProperties.getUserPassword())
//                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                .build();
//
//        this.accountService.saveAccount(hkh);



        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                .param("username",appProperties.getUserUsername())
                .param("password",appProperties.getUserPassword())
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                ;
    }
}
package com.hkhexbook.restapitddservice.configs;

import com.hkhexbook.restapitddservice.accounts.Account;
import com.hkhexbook.restapitddservice.accounts.AccountRepository;
import com.hkhexbook.restapitddservice.accounts.AccountRole;
import com.hkhexbook.restapitddservice.accounts.AccountService;
import com.hkhexbook.restapitddservice.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean //애플리케이션 실행시 사용자계정만듦.
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;


            @Override
            public void run(ApplicationArguments args) throws Exception {

                Account hkh = Account.builder().
                        email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
                        .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                        .build();

                accountService.saveAccount(hkh);

                Account admin = Account.builder().
                        email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
                        .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                        .build();

                accountService.saveAccount(admin);

            }
        };
    }
}

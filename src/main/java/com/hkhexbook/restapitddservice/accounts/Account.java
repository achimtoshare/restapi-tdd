package com.hkhexbook.restapitddservice.accounts;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private  Integer id;
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER) //여러개의 enum을 가질수있음. 필요할때마다가져와야함.
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}

package com.paf.exercise.exercise.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paf.exercise.exercise.authorization.domain.Authority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name="user_id")
    private String userId;

    @Column(name="address")
    private String address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(name = "account_has_authority", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name="authority_id"))
    private Set<Authority> authorities = new HashSet<>();
}

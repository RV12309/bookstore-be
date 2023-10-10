package com.hust.bookstore.entity;

import com.hust.bookstore.enumration.UserType;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "account")
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "type")

    @Enumerated(EnumType.STRING)
    private UserType type;

    @Column(name = "is_enable")
    @Builder.Default
    private Boolean isEnable = false;
}

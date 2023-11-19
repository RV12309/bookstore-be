package com.hust.bookstore.entity;


import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.Gender;
import com.hust.bookstore.enumration.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
public class User extends BaseEntity{
    @Id
    private Long id;
    private String email;
    private String phone;
    private String name;
    private String avatarUrl;
    private LocalDateTime dob;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private UserType type;
    @Column(name = "account_id")
    private Long accountId;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
    }

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

}

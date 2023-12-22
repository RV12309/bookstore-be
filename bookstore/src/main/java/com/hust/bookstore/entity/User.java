package com.hust.bookstore.entity;


import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.Gender;
import com.hust.bookstore.enumration.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
@DynamicUpdate
@EntityListeners({AuditingEntityListener.class})
public class User {
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

    private Long provinceId;
    private Long districtId;
    private Long wardCode;
    private String province;
    private String district;
    private String ward;
    private String firstAddress;

    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
    }

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

}

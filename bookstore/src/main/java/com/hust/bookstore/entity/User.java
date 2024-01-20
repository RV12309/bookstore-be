package com.hust.bookstore.entity;


import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.Gender;
import com.hust.bookstore.enumration.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    private Long id;
    private String email;
    private String phone;
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private LocalDateTime dob;
    @Enumerated(EnumType.STRING)

    private Gender gender;
    @Enumerated(EnumType.STRING)

    private UserType type;
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "province_id")
    private Long provinceId;
    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "ward_code")
    private Long wardCode;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "first_address")
    private String firstAddress;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
    }

//    @OneToOne
//    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
//    private Account account;

}

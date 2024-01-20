package com.hust.bookstore.entity;


import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "user_address")
public class UserAddress extends BaseEntity {
    @Id
    private Long id;
    private Long userId;
    private Long provinceId;
    private Long districtId;
    private Long wardCode;
    private String province;
    private String district;
    private String ward;
    private String firstAddress;
    private boolean isDefault;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
    }

    @Builder.Default
    private Boolean isDeleted = false;

}

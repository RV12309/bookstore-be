package com.hust.bookstore.entity;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.DeliveryProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "store_delivery_partners", indexes = {
        @Index(name = "delivery_partners_user_id_index", columnList = "user_id"),
        @Index(name = "delivery_partners_account_id_index", columnList = "account_id"),
})
public class StoreDeliveryPartners extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "provider")
    @Comment("Nhà vận chuyển")
    @Enumerated(EnumType.STRING)
    private DeliveryProvider provider;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "token")
    @Comment("Token của đối tác vận chuyển")
    private String token;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "shop_id")
    @Comment("Mã shop - tạo đối tác vận chuyển cho shop")
    private Integer shopId;

    @Column(name = "district_id")
    @Comment("Mã phường/xã - với provider là GHN")
    private Long districtId;

    @Column(name = "ward_code")
    @Comment("Mã phường/xã - với provider là GHN")
    private String wardCode;

    @Column(name = "phone")
    @Comment("Số điện thoại - với provider là GHN")
    private String phone;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
        this.setIsDeleted(false);
    }

    @Builder.Default
    private Boolean isDeleted = false;

}

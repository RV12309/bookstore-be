package com.hust.bookstore.entity;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.DeliveryProvider;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "delivery_partners", indexes = {
        @Index(name = "delivery_partners_user_id_index", columnList = "user_id"),
        @Index(name = "delivery_partners_account_id_index", columnList = "account_id"),
})
public class DeliveryPartners extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name="provider")
    private DeliveryProvider provider;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "api_secret")
    private String apiSecret;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;


    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
        this.setIsDeleted(false);
    }

    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;
}

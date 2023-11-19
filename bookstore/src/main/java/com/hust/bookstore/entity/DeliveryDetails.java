package com.hust.bookstore.entity;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.DeliveryProvider;
import com.hust.bookstore.enumration.DeliveryStatus;
import com.hust.bookstore.enumration.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "delivery_details", indexes = {
        @Index(name = "delivery_details_user_id_index", columnList = "user_id"),
        @Index(name = "delivery_details_order_id_index", columnList = "order_id"),
})
public class DeliveryDetails extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "order_id")
    private Long paymentId;

    @Column(name="provider")
    private DeliveryProvider provider;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "tracking_code")
    private String trackingCode;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
        this.setIsDeleted(false);
    }

    @Builder.Default
    private Boolean isDeleted = false;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OrderDetails orderDetails;
}

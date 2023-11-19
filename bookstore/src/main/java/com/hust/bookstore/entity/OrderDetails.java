package com.hust.bookstore.entity;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "order_details", indexes = {
        @Index(name = "order_details_user_id_index", columnList = "user_id", unique = true),
})
public class OrderDetails extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

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
    @JoinColumn(name = "payment_id", referencedColumnName = "id", insertable = false, updatable = false)
    private PaymentDetails paymentDetails;

    @OneToMany
    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<OrderItems> orderItems;
}

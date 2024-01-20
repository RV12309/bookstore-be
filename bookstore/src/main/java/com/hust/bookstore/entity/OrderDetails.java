package com.hust.bookstore.entity;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import com.hust.bookstore.enumration.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@DynamicUpdate
@EntityListeners({AuditingEntityListener.class})
@Table(name = "order_details", indexes = {
        @Index(name = "order_details_user_id_index", columnList = "user_id")
})
public class OrderDetails {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "seller_id")
    private Long sellerId;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
        this.setIsDeleted(false);
    }

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

}

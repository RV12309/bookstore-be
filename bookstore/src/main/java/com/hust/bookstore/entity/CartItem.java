package com.hust.bookstore.entity;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "cart_item", indexes = {
        @Index(name = "cart_item_session_id_index", columnList = "session_id, book_id", unique = true),
})
public class CartItem extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "quantity")
    private Long quantity;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
        this.setIsDeleted(false);
    }

    @Builder.Default
    private Boolean isDeleted = false;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ShoppingSession shoppingSession;

}

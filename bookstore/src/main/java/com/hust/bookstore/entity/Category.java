package com.hust.bookstore.entity;


import cn.ipokerface.snowflake.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    private Long id;

    @Column(name = "code", unique = true)
    private String code;
    private String name;
    private String description;

    @Column(name = "parent_id")
    private Long parentId;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
    }

}

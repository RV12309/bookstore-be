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
@Table(name = "book", indexes = {
        @Index(name = "book_isbn_index", columnList = "isbn"),
        @Index(name = "book_title_index", columnList = "title"),
        @Index(name = "book_author_index", columnList = "author"),
        @Index(name = "book_account_id_index", columnList = "account_id")
})
public class Book extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

    private String isbn;

    private String title;

    private String author;

    private String description;

    private String urlThumbnail;

    private String urlImageCover;

    private String publisher;

    private String publishDate;

    private String language;

    private Long numberOfPages;

    private Long price;

    private Long quantity;

    @Column(name = "account_id")
    private Long accountId;


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BookImages> bookImages;

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Categories> categories;

    @PrePersist
    public void setId() {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0);
        this.id = idGenerator.nextId();
        this.setIsDeleted(false);
    }

    @Builder.Default
    private Boolean isDeleted = false;


}

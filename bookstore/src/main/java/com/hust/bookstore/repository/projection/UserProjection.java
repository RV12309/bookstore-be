package com.hust.bookstore.repository.projection;

import com.hust.bookstore.enumration.UserType;

import java.time.LocalDateTime;

public interface UserProjection {
    Long getId();

    String getName();

    String getAvatarUrl();

    String getUsername();

    LocalDateTime getCreatedAt();

    UserType getType();
}

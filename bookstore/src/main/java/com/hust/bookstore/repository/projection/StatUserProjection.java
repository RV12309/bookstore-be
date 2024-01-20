package com.hust.bookstore.repository.projection;

import com.hust.bookstore.enumration.UserType;

public interface StatUserProjection {
    UserType getType();

    Long getCount();
}

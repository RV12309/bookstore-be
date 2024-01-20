package com.hust.bookstore.repository.projection;

import java.time.LocalDateTime;

public interface StatOderTypeProjection {
    LocalDateTime getTime();

    Long getTotalOrder();
}

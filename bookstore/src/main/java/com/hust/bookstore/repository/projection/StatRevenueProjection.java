package com.hust.bookstore.repository.projection;

import java.time.LocalDateTime;

public interface StatRevenueProjection {
    LocalDateTime getTime();

    Long getTotalAmount();
}

package com.hust.bookstore.repository.projection;

import java.math.BigDecimal;

public interface StatOderProjection {
    String getTime();

    Long getTotalOrder();

    BigDecimal getTotalAmount();
}

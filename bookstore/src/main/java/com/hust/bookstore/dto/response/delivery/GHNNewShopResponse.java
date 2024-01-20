package com.hust.bookstore.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHNNewShopResponse {
    @JsonProperty("shop_id")
    @JsonAlias("shop_id")
    private Integer shopId;
}

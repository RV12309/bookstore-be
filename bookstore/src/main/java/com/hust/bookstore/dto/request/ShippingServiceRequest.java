package com.hust.bookstore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShippingServiceRequest {
    List<Long> bookIds;

    @NotNull
    private Integer fromDistrict;

    @NotNull
    private Integer toDistrict;
}

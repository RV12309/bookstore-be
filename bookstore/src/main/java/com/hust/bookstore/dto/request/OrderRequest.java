package com.hust.bookstore.dto.request;

import com.hust.bookstore.dto.OrderItemDto;
import com.hust.bookstore.enumration.PaymentProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
    @NotNull
    private Long sessionId;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String name;

    @NotNull
    private PaymentProvider paymentProvider;

    private BigDecimal total;

    List<OrderItemRequest> items;

    private Long provinceId;

    @NotNull(message = "District id cannot be null.")
    private Long districtId;

    @NotNull(message = "Ward code cannot be null.")
    private Long wardCode;

    @NotBlank(message = "Province cannot be blank.")
    private String province;

    @NotBlank(message = "District cannot be blank.")
    private String district;

    @NotBlank(message = "Ward cannot be blank.")
    private String ward;

    @NotBlank(message = "First address cannot be blank.")
    private String firstAddress;

    private Long addressId;

}

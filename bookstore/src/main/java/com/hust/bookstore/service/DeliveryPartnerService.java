package com.hust.bookstore.service;

import com.hust.bookstore.dto.request.CalculateShippingFeeRequest;
import com.hust.bookstore.dto.request.OrderRequest;
import com.hust.bookstore.dto.request.ShippingServiceRequest;
import com.hust.bookstore.dto.request.delivery.GHNShopInfoRequest;
import com.hust.bookstore.dto.request.delivery.TokenRequest;
import com.hust.bookstore.dto.response.AvailableShippingServiceResponse;
import com.hust.bookstore.dto.response.PartnerAuthResponse;
import com.hust.bookstore.dto.response.PartnerBaseResponse;
import com.hust.bookstore.dto.response.delivery.DistrictResponse;
import com.hust.bookstore.dto.response.delivery.ProvinceResponse;
import com.hust.bookstore.dto.response.delivery.ShippingFeeResponse;
import com.hust.bookstore.dto.response.delivery.WardResponse;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.entity.User;

import java.util.List;

public interface DeliveryPartnerService {

    PartnerBaseResponse<PartnerAuthResponse> authenticate(TokenRequest tokenRequest);

    void createNewStore(GHNShopInfoRequest shopInfoRequest, User user, Account account);

    PartnerBaseResponse<PartnerAuthResponse> createOrder(OrderRequest orderRequest, Long shopAccountId);


    List<DistrictResponse> getDistricts(int provinceId);

    List<WardResponse> getWards(int districtId);

    List<ProvinceResponse> getProvinces();

    List<AvailableShippingServiceResponse> getShippingServices(ShippingServiceRequest request);

    ShippingFeeResponse getShippingFee(CalculateShippingFeeRequest request);
}

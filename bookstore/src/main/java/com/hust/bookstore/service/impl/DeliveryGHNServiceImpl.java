package com.hust.bookstore.service.impl;

import com.hust.bookstore.dto.request.CalculateShippingFeeRequest;
import com.hust.bookstore.dto.request.ShippingFeeRequest;
import com.hust.bookstore.dto.request.ShippingServiceRequest;
import com.hust.bookstore.dto.request.delivery.*;
import com.hust.bookstore.dto.response.AvailableShippingServiceResponse;
import com.hust.bookstore.dto.response.PartnerAuthResponse;
import com.hust.bookstore.dto.response.PartnerBaseResponse;
import com.hust.bookstore.dto.response.delivery.*;
import com.hust.bookstore.entity.Account;
import com.hust.bookstore.entity.DeliveryPartnersConfig;
import com.hust.bookstore.entity.StoreDeliveryPartners;
import com.hust.bookstore.entity.User;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.exception.BusinessException;
import com.hust.bookstore.helper.BusinessHelper;
import com.hust.bookstore.repository.*;
import com.hust.bookstore.service.AuthService;
import com.hust.bookstore.service.DeliveryPartnerService;
import com.hust.bookstore.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hust.bookstore.common.RestUtils.doPost;
import static com.hust.bookstore.common.RestUtils.objectMapper;
import static com.hust.bookstore.enumration.DeliveryProvider.GHN_EXPRESS;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class DeliveryGHNServiceImpl extends BusinessHelper implements DeliveryPartnerService {
    @Value("${endpoint.partner.ghn.order.add}")
    private String newOrderEndpoint;


    @Value("${endpoint.partner.ghn.shop.add}")
    private String newStoreEndpoint;

    @Value("${endpoint.partner.ghn.order.detail}")
    private String orderDetailEndpoint;

    @Value("${endpoint.partner.ghn.order.cancel}")
    private String cancelOrderEndpoint;

    @Value("${endpoint.partner.ghn.address.province}")
    private String provinceEndpoint;

    @Value("${endpoint.partner.ghn.address.district}")
    private String districtEndpoint;

    @Value("${endpoint.partner.ghn.address.ward}")
    private String wardEndpoint;

    @Value("${endpoint.partner.ghn.shipping.service}")
    private String availableServiceEndpoint;

    @Value("${endpoint.partner.ghn.shipping.fee}")
    private String shippingFeeEndpoint;

    public DeliveryGHNServiceImpl(BookRepository bookRepository, CartRepository cartRepository,
                                  CartItemRepository cartItemRepository, PaymentRepository paymentRepository,
                                  DeliveryPartnerConfigRepository deliveryPartnerConfigRepo,
                                  StoreDeliveryPartnerRepository storeDeliveryPartnerRepo, UserRepository userRepository,
                                  OrderDetailRepository orderDetailsRepository, OrderItemsRepository orderItemsRepository,
                                  CategoryRepository categoryRepository, BookCategoryRepository bookCategoryRepository,
                                  AccountRepository accountRepository, AuthService authService,
                                  BookImageRepository bookImageRepository, ModelMapper modelMapper,
                                  NotificationService notificationService, UserAddressRepository addressRepository,
                                  DeliveryDetailRepository deliveryDetailRepository) {
        super(bookRepository, cartRepository, cartItemRepository, paymentRepository,
                deliveryPartnerConfigRepo, storeDeliveryPartnerRepo, userRepository,
                orderDetailsRepository, orderItemsRepository, categoryRepository,
                bookCategoryRepository, accountRepository, authService, bookImageRepository,
                modelMapper, notificationService, addressRepository, deliveryDetailRepository);
    }

    @Override
    public PartnerBaseResponse<PartnerAuthResponse> authenticate(TokenRequest tokenRequest) {
        return null;
    }

    @Override
    public void createNewStore(GHNShopInfoRequest shopInfoRequest, User user, Account account) {
        DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));

        StoreDeliveryPartners storeDeliveryPartner =
                storeDeliveryPartnerRepo.findByProviderAndAccountId(GHN_EXPRESS, account.getId())
                        .orElse(null);
        if (nonNull(storeDeliveryPartner)) {
            throw new BusinessException(ResponseCode.STORE_ALREADY_EXIST);
        }

        Map<String, String> headers = buildHeaders(deliveryConfig);
        String url = deliveryConfig.getApiUrl() + newStoreEndpoint;
        try {
            PartnerBaseResponse<GHNNewShopResponse> response = doPost(url, shopInfoRequest, headers, getNewTypeReference());
            if (isNull(response) || non(response.isSuccess())) {
                log.error("Error when create new store: {}", response);
                throw new BusinessException(ResponseCode.CREATE_STORE_FAILED);
            }
            GHNNewShopResponse shopResponse = response.getData();
            StoreDeliveryPartners storePartner = modelMapper.map(shopInfoRequest, StoreDeliveryPartners.class);
            storePartner.setProvider(GHN_EXPRESS);
            storePartner.setAccountId(account.getId());
            storePartner.setShopId(shopResponse.getShopId());
            storePartner.setUserId(user.getId());
            storePartner.setIsDefault(true);
            storePartner.setToken(deliveryConfig.getToken());
            storePartner.setApiUrl(deliveryConfig.getApiUrl());
            storeDeliveryPartnerRepo.save(storePartner);
        } catch (Exception e) {
            log.error("Error when create new store: {}", e.getMessage());
            throw new BusinessException(ResponseCode.CREATE_STORE_FAILED);
        }

    }

    @Override
    public String createOrder(DeliveryRequest deliveryRequest) {

        DeliveryPartnersConfig deliveryPartnersConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));
        String createOrderUrl = deliveryPartnersConfig.getApiUrl() + newOrderEndpoint;
        try {
            HashMap<String, Object> response = doPost(createOrderUrl,
                    deliveryRequest, buildHeaders(deliveryPartnersConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when create order: {}", response);
                String message = (String) response.get("message");
                log.error("Message: {}", message);
                throw new BusinessException(ResponseCode.CREATE_ORDER_FAILED);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);
            DeliveryResponse orderResponse = objectMapper.readValue(dataString, DeliveryResponse.class);
            return orderResponse.getOrderCode();
        } catch (Exception e) {
            log.error("Error when create order: {}", e.getMessage());
            throw new BusinessException(ResponseCode.CREATE_ORDER_FAILED);
        }
    }

    @Override
    public List<DistrictResponse> getDistricts(int provinceId) {
        DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));
        String getDistrictUrl = deliveryConfig.getApiUrl() + districtEndpoint;
        DistrictRequest districtRequest = new DistrictRequest();
        districtRequest.setProvinceId(provinceId);
        try {
            HashMap<String, Object> response = doPost(getDistrictUrl,
                    districtRequest, buildHeaders(deliveryConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when get district: {}", response);
                String message = (String) response.get("message");
                log.error("Message: {}", message);
                throw new BusinessException(ResponseCode.GET_DISTRICT_FAILED);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);
            List<Object> shippingServiceResponses = objectMapper.readValue(dataString, getNewTypeReference());
            List<DistrictResponse> districts = new ArrayList<>();
            for (Object districtResponse : shippingServiceResponses) {
                DistrictResponse district = objectMapper.convertValue(districtResponse, DistrictResponse.class);
                districts.add(district);
            }
            return districts;
        } catch (Exception e) {
            log.error("Error when get district: {}", e.getMessage());
            throw new BusinessException(ResponseCode.GET_DISTRICT_FAILED);
        }
    }

    @Override
    public List<WardResponse> getWards(int districtId) {
        DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));
        String getWardUrl = deliveryConfig.getApiUrl() + wardEndpoint;
        WardRequest wardRequest = new WardRequest();
        wardRequest.setDistrictId(districtId);
        try {
            HashMap<String, Object> response = doPost(getWardUrl,
                    wardRequest, buildHeaders(deliveryConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when get ward: {}", response);
                String message = (String) response.get("message");
                log.error("Message: {}", message);
                throw new BusinessException(ResponseCode.GET_WARD_FAILED);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);
            List<Object> shippingServiceResponses = objectMapper.readValue(dataString, getNewTypeReference());
            List<WardResponse> wards = new ArrayList<>();
            for (Object wardResponse : shippingServiceResponses) {
                WardResponse ward = objectMapper.convertValue(wardResponse, WardResponse.class);
                wards.add(ward);
            }
            return wards;
        } catch (Exception e) {
            log.error("Error when get ward: {}", e.getMessage());
            throw new BusinessException(ResponseCode.GET_WARD_FAILED);
        }
    }

    @Override
    public List<ProvinceResponse> getProvinces() {
        DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));
        String getProvinceUrl = deliveryConfig.getApiUrl() + provinceEndpoint;
        try {
            HashMap<String, Object> response = doPost(getProvinceUrl,
                    new HashMap<>(), buildHeaders(deliveryConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when get province: {}", response);
                String message = (String) response.get("message");
                log.error("Message: {}", message);
                throw new BusinessException(ResponseCode.GET_PROVINCE_FAILED);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);
            List<Object> shippingServiceResponses = objectMapper.readValue(dataString, getNewTypeReference());
            List<ProvinceResponse> provinces = new ArrayList<>();
            for (Object proResponse : shippingServiceResponses) {
                ProvinceResponse province = objectMapper.convertValue(proResponse, ProvinceResponse.class);
                provinces.add(province);
            }

            return provinces;
        } catch (Exception e) {
            log.error("Error when get province: {}", e.getMessage());
            throw new BusinessException(ResponseCode.GET_PROVINCE_FAILED);
        }
    }

    @Override
    public List<AvailableShippingServiceResponse> getShippingServices(ShippingServiceRequest request) {
        try {
            DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                    .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));

            AvailableShippingServiceRequest service = modelMapper.map(request, AvailableShippingServiceRequest.class);
            service.setShopId(Integer.valueOf(deliveryConfig.getCode()));
            String getShippingServiceUrl = deliveryConfig.getApiUrl() + availableServiceEndpoint;

            HashMap<String, Object> response = doPost(getShippingServiceUrl,
                    service, buildHeaders(deliveryConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when get shipping service: {}", response);
                String message = (String) response.get("message");
                log.error("Message: {}", message);
                throw new BusinessException(ResponseCode.GET_SHIPPING_SERVICE_FAILED);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);

            List<ShippingServiceResponse> shippingServiceResponses = objectMapper.readValue(dataString, getNewTypeReference());
            List<AvailableShippingServiceResponse> availableServices = new ArrayList<>();
            for (Object shipResponse : shippingServiceResponses) {
                ShippingServiceResponse ship = objectMapper.convertValue(shipResponse, ShippingServiceResponse.class);

                availableServices.add(AvailableShippingServiceResponse.builder()
                        .serviceTypeId(ship.getServiceTypeId())
                        .serviceId(ship.getServiceId())
                        .shortName(ship.getShortName())
                        .configFeeId(ship.getConfigFeeId())
                        .extraCostId(ship.getExtraCostId())
                        .standardConfigFeeId(ship.getStandardConfigFeeId())
                        .standardExtraCostId(ship.getStandardExtraCostId())
                        .build());

            }
            return availableServices;
        } catch (Exception e) {
            log.error("Error when get shipping service: {}", e.getMessage());
            throw new BusinessException(ResponseCode.GET_SHIPPING_SERVICE_FAILED);
        }

    }

    @Override
    public ShippingFeeResponse getShippingFee(CalculateShippingFeeRequest request) {
        try {
            DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                    .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));

            String getShippingFeeUrl = deliveryConfig.getApiUrl() + shippingFeeEndpoint;
            Map<String, String> headers = buildHeaders(deliveryConfig);
            headers.put("ShopId", deliveryConfig.getCode());
            ShippingFeeRequest feeRequest = modelMapper.map(request, ShippingFeeRequest.class);
            feeRequest.setWeight(500);
            feeRequest.setFromDistrictId(1454);
            feeRequest.setFromWardCode("21211");
            HashMap<String, Object> response = doPost(getShippingFeeUrl,
                    request, buildHeaders(deliveryConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when get shipping fee: {}", response);
                String message = (String) response.get("message");
                log.error("Message: {}", message);
                throw new BusinessException(ResponseCode.GET_SHIPPING_FEE_FAILED);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);
            return objectMapper.readValue(dataString, ShippingFeeResponse.class);
        } catch (Exception e) {
            log.error("Error when get shipping fee: {}", e.getMessage());
            throw new BusinessException(ResponseCode.GET_SHIPPING_FEE_FAILED);
        }
    }

    @Override
    public void cancelOrder(String trackingCode) {
        try {
            DeliveryPartnersConfig deliveryConfig = deliveryPartnerConfigRepo.findByProvider(GHN_EXPRESS)
                    .orElseThrow(() -> new BusinessException(ResponseCode.CONFIG_DELIVERY_PARTNER_NOT_FOUND));

            String getShippingFeeUrl = deliveryConfig.getApiUrl() + cancelOrderEndpoint;
            Map<String, String> headers = buildHeaders(deliveryConfig);
            headers.put("ShopId", deliveryConfig.getCode());
            HashMap<String, Object> response = doPost(getShippingFeeUrl,
                    new HashMap<>(), buildHeaders(deliveryConfig), getNewTypeReference());
            if (isNull(response) || non(response.get("code").equals(200))) {
                log.error("Error when get shipping fee: {}", response);
                throw new BusinessException(ResponseCode.DELIVERY_NOT_FOUND);
            }
            Object data = response.get("data");
            String dataString = objectMapper.writeValueAsString(data);
            CancelDeliveryResponse deliveryResponse = objectMapper.readValue(dataString, CancelDeliveryResponse.class);
            if (isNull(deliveryResponse) || non(deliveryResponse.getOrderCode().equals(trackingCode))) {
                throw new BusinessException(ResponseCode.DELIVERY_NOT_FOUND);
            }
            if (non(deliveryResponse.isResult())) {
                log.info("Order {} is not cancel", trackingCode);
            }
            log.info("Order {} is cancel", trackingCode);
        } catch (Exception e) {
            log.error("Error when cancel order: {}", e.getMessage());
            throw new BusinessException(ResponseCode.DELIVERY_NOT_FOUND);
        }
    }

    private Map<String, String> buildHeaders(DeliveryPartnersConfig deliveryPartnersConfig) {
        Map<String, String> headers = new HashMap<>();
        // Adding headers
        headers.put("Token", deliveryPartnersConfig.getToken());
        headers.put("Content-Type", "application/json");

        return headers;
    }
}

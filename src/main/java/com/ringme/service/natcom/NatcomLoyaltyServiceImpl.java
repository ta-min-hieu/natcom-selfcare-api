package com.ringme.service.natcom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringme.config.AppConfig;
import com.ringme.dto.natcom.loyalty.*;
import com.ringme.enums.loyalty.IsSendSMS;
import com.ringme.enums.loyalty.LoyaltyStatus;
import com.ringme.enums.loyalty.PointTransactionType;
import com.ringme.enums.loyalty.PointTypeId;
import com.ringme.enums.loyalty.gift.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public final class NatcomLoyaltyServiceImpl implements NatcomLoyaltyService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AppConfig appConfig;
    @Autowired
    NatcomSelfcareService natcomSelfcareService;

    @Override
    public LoyaltyResponse<List<ViettelAccountDTO>> getCustomerInfo(String isdn, String subId, String idNo, String custId, String vtAccountId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyCustomerInfo())
                .queryParam("isdn", isdn)
                .queryParam("subId", subId)
                .queryParam("idNo", idNo)
                .queryParam("custId", custId)
                .queryParam("vtAccountId", vtAccountId)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<List<ViettelAccountDTO>>() {});
    }

    @Override
    public LoyaltyResponse<List<PointTransferHistoryDTO>> getPointTransferHistory(String isdn, String subId, String idNo, String custId, LocalDate fromDate, LocalDate toDate, Long offset, Long limit, PointTransactionType type, PointTypeId pointId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyPointTransferHistory())
                .queryParam("isdn", isdn)
                .queryParam("subId", subId)
                .queryParam("idNo", idNo)
                .queryParam("custId", custId)
                .queryParam("fromDate", fromDate.format(formatter))
                .queryParam("toDate", toDate.format(formatter))
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("type", type != null ? type.getType() : null)
                .queryParam("pointId", pointId != null ? pointId.getType() : null)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<List<PointTransferHistoryDTO>>() {});
    }

    @Override
    public LoyaltyResponse<List<RankDefineDTO>> getRankDefineInfo(String tenant) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyRankDefineInfo())
                .queryParam("tenant", tenant)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<List<RankDefineDTO>>() {});
    }

    @Override
    public LoyaltyResponse<List<GiftDTO>> getGiftInfo(GiftId giftId, GiftCode giftCode, Long giftType, GiftName giftName, Long point, LoyaltyStatus status) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyGiftInfo())
                .queryParam("giftId", giftId != null ? giftId.getType() : null)
                .queryParam("giftCode", giftCode != null ? giftCode.getType() : null)
                .queryParam("giftType", giftType)
                .queryParam("giftName", giftName != null ? giftName.getType() : null)
                .queryParam("point", point)
                .queryParam("status", status != null ? status.getType() : null)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<List<GiftDTO>>() {});
    }

    @Override
    public LoyaltyResponse<List<AccountPointDto>> getAccountPointInfo(String isdn, String subId, String idNo, String custId, IsSendSMS isSendSms) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyAccountPointInfo())
                .queryParam("isdn", isdn)
                .queryParam("subId", subId)
                .queryParam("idNo", idNo)
                .queryParam("custId", custId)
                .queryParam("isSendSms", isSendSms != null ? isSendSms.getType() : null)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<List<AccountPointDto>>() {});
    }

    @Override
    public LoyaltyResponse<Object> getTotalPoint(String vtAccId, PointTypeId pointTypeId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyTotalPoint())
                .queryParam("vtAccId", vtAccId)
                .queryParam("pointId", pointTypeId != null ? pointTypeId.getType() : null)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<>() {});
    }

    @Override
    public LoyaltyResponse<AccountRankDTO> getAccountRankInfo(String isdn, String subId, String idNo, String custId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyAccountRankInfo())
                .queryParam("isdn", isdn)
                .queryParam("subId", subId)
                .queryParam("idNo", idNo)
                .queryParam("custId", custId)
                .toUriString();
        return callApiLoyalty(HttpMethod.GET, null, url, null, new TypeReference<>() {});
    }

    @Override
    public LoyaltyResponse<Object> redeemPoint(String isdn, long pointAmount, TransTypeId typeId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyRedeemPoint())
                .toUriString();

        Map<String, Object> body = new HashMap<>();
        body.put("isdn", isdn);
        body.put("transferType", "POINT");
        body.put("pointAmount", pointAmount);
        body.put("datatype", "DIFFERENT");
        body.put("pointId", PointTypeId.CONSUMPTION);
        body.put("staffUser", "viettelStaff");
        body.put("transTypeId", typeId.getType());
        return callApiLoyalty(HttpMethod.POST, MediaType.APPLICATION_JSON, url, body, new TypeReference<>() {});
    }

    @Override
    public LoyaltyResponse<Object> adjustAccountPoint(String isdn, String subId, String custId, Long vtAccId, Long pointAmount, PointTypeId pointId, TransTypeId transTypeId, Long userSystem, String description) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyAdjustAccountPoint())
                .toUriString();

        Map<String, Object> body = new HashMap<>();
        body.put("isdn", isdn);
        body.put("pointAmount", pointAmount);
        body.put("transTypeId", transTypeId.getType());
        return callApiLoyalty(HttpMethod.POST, MediaType.APPLICATION_JSON, url, body, new TypeReference<>() {});
    }

    @Override
    public LoyaltyResponse<List<AccountRankHistoryDTO>> accountRankHis(Long vtAccId, LocalDate fromDate, LocalDate toDate) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyAccountRankHis())
                .queryParam("vtAccId", vtAccId)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .toUriString();
        return callApiLoyalty(HttpMethod.POST, null, url, null, new TypeReference<List<AccountRankHistoryDTO>>() {});
    }

    @Override
    public LoyaltyResponse<List<SumPointDTO>> getListPointRankByMonth(Long vtAccId, String productId, String productName, LocalDate fromDate, LocalDate toDate) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyListPointRankByMonth())
                .queryParam("vtAccId", vtAccId)
                .queryParam("productId", productId)
                .queryParam("productName", productName)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .toUriString();
        return callApiLoyalty(HttpMethod.POST, null, url, null, new TypeReference<List<SumPointDTO>>() {});
    }

    @Override
    public LoyaltyResponse<List<SumPointDTO>> getListPointProductByMonth(Long vtAccId, String productId, String productName, LocalDate fromDate, LocalDate toDate) {
        String url = UriComponentsBuilder
                .fromHttpUrl(appConfig.getLoyaltyListPointProductByMonth())
                .queryParam("vtAccId", vtAccId)
                .queryParam("productId", productId)
                .queryParam("productName", productName)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .toUriString();
        return callApiLoyalty(HttpMethod.POST, null, url, null, new TypeReference<List<SumPointDTO>>() {});
    }

    @Override
    public LoyaltyResponse<Object> redeemPointLotteryCode(String productName) {
        Map<String, Object> body = new HashMap<>();
        body.put("productName", productName);

        return natcomSelfcareService.callApiSelfcare(appConfig.getLoyaltyRedeemPointLotteryCode(), body, new TypeReference<LoyaltyResponse<Object>>() {});
    }

    private <T> LoyaltyResponse<T> callApiLoyalty(HttpMethod method, MediaType mediaType, String url, Object body, TypeReference<T> responseType) {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.set("Authorization", appConfig.getSelfcareBearerToken());

            HttpEntity<?> entity = new HttpEntity<>(body, headers);

            response = restTemplate.exchange(url, method, entity, String.class);

//            log.info("Success url: {}, body: {}, responseType: {}, response: {}", url, body, responseType, response);

            LoyaltyResponse<T> wrapper = objectMapper.readValue(response.getBody(), LoyaltyResponse.class);

            return wrapper;
        } catch (Exception e) {
            log.error("Error url: {}, body: {}, responseType: {}, response: {}, error: {}",  url, body, responseType, response, e.getMessage(), e);
        }
        return null;
    }
}

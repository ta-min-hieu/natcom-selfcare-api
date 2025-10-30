package com.ringme.service.ringme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringme.common.Common;
import com.ringme.common.Helper;
import com.ringme.config.AppConfig;
import com.ringme.config.PlusEncoderInterceptor;
import com.ringme.dto.natcom.loyalty.LoyaltyResponse;
import com.ringme.dto.natcom.loyalty.ViettelAccountDTO;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.SubMainInfo;
import com.ringme.dto.ringme.selfcare.UserInfo;
import com.ringme.dto.ringme.selfcare.VtmVaildateResponse;
import com.ringme.service.natcom.NatcomLoyaltyService;
import com.ringme.service.natcom.NatcomSelfcareService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@Log4j2
public class UserInfoService implements UserDetailsService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    AppConfig appConfig;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    NatcomSelfcareService natcomSelfcareService;
    @Autowired
    NatcomLoyaltyService natcomLoyaltyService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new org.springframework.security.core.userdetails.User(username, "", new ArrayList<>());
    }

    public UserInfo validateUserByVTM(String isdn, long timestamp, String cert) {
        if(Common.whiteList.contains(isdn))
            return getUserInfo(isdn);

        if(!callApiValidateUserByVTM(isdn, timestamp, cert))
            return null;

        return getUserInfo(isdn);
    }

//    private boolean callApiValidateUserByVTM(String isdn, long timestamp, String cert) {
//        ResponseEntity<String> response = null;
//
//        String baseUrl = appConfig.getApiValidateSuperappTokenByVTM();
//        URI uri = null;
//        String security = null;
//        try {
//            String msisdn = "+509" + isdn;
//            security = Helper.md5(cert + timestamp);
//
//            uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
//                    .queryParam("msisdn", msisdn)
//                    .queryParam("timestamp", timestamp)
//                    .queryParam("security", security)
//                    .build()
//                    .encode()
//                    .toUri();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//
//            response = restTemplate.postForEntity(uri, entity, String.class);
//
//            VtmVaildateResponse vtmVaildateResponse = objectMapper.readValue(response.getBody(), VtmVaildateResponse.class);
//            if (vtmVaildateResponse == null || !vtmVaildateResponse.isResult()) {
//                log.error("check authen fail | uri: {}, isdn: {}, timestamp: {}, cert: {}, security: {}, vtmVaildateResponse: {}", uri, isdn, timestamp, cert, security, vtmVaildateResponse);
//                return false;
//            }
//
//            log.info("Success url: {}, isdn: {}, timestamp: {}, cert: {}, response: {}, securiry: {}", uri, isdn, timestamp, cert, response, security);
//            return true;
//        } catch (Exception e) {
//            log.error("Error url: {}, uri: {}, isdn: {}, timestamp: {}, cert: {}, security: {}, response: {}, error: {}",
//                    baseUrl, uri, isdn, timestamp, cert, security, response, e.getMessage(), e);
//        }
//        return false;
//    }

    public boolean callApiValidateUserByVTM(String isdn, long timestamp, String cert) {
        String baseUrl = appConfig.getApiValidateSuperappTokenByVTM();

        try {
            String msisdn = isdn;

            if(!isdn.startsWith("+509"))
                msisdn = "+509" + isdn;

            RestTemplate restTemplate = new RestTemplateBuilder()
                    .rootUri(baseUrl)
                    .interceptors(new PlusEncoderInterceptor()).build();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("msisdn", msisdn)
                    .queryParam("timestamp", timestamp)
                    .queryParam("security", cert)
                    .toUriString();

            VtmVaildateResponse response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.POST,
                    entity,
                    VtmVaildateResponse.class
            ).getBody();

            if(response == null || !response.isResult()) {
                log.error("check authen fail | msisdn: {}, timestamp: {}, cert: {}, vtmVaildateResponse: {}", msisdn, timestamp, cert, response);
                return false;
            }

            log.info("Success msisdn: {}, timestamp: {}, cert: {}, response: {}", msisdn, timestamp, cert, response);
            return true;
        } catch (Exception e) {
            log.error("isdn: {}, timestamp: {}, cert: {}, error: {}", isdn, timestamp, cert, e.getMessage(), e);
        }
        return false;
    }

//    public VtmVaildateResponse callApiValidateUserByVTMTest(String msisdn, long timestamp, String security) {
//        ResponseEntity<String> response = null;
//
//        String baseUrl = appConfig.getApiValidateSuperappTokenByVTM();
//        String uri = null;
//        try {
//            uri = baseUrl + "?msisdn=" + msisdn.replace("+", "%2B") + "&timestamp=" + timestamp + "&security=" + security;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<?> entity = new HttpEntity<>(headers);
//
//            response = restTemplate.postForEntity(uri, entity, String.class);
//
//            log.info("Success url: {}, isdn: {}, timestamp: {}, response: {}, securiry: {}", uri, msisdn, timestamp, response, security);
//            return objectMapper.readValue(response.getBody(), VtmVaildateResponse.class);
//        } catch (Exception e) {
//            log.error("Error url: {}, uri: {}, isdn: {}, timestamp: {}, security: {}, response: {}, error: {}",
//                    baseUrl, uri, msisdn, timestamp, security, response, e.getMessage(), e);
//        }
//
//        return null;
//    }

    public VtmVaildateResponse callApiValidateUserByVTMTest(String msisdn, long timestamp, String security) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(appConfig.getApiValidateSuperappTokenByVTM())
                .interceptors(new PlusEncoderInterceptor()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(appConfig.getApiValidateSuperappTokenByVTM())
                .queryParam("msisdn", msisdn)
                .queryParam("timestamp", timestamp)
                .queryParam("security", security)
                .toUriString();

        return restTemplate.exchange(
                urlTemplate,
                HttpMethod.POST,
                entity,
                VtmVaildateResponse.class
        ).getBody();
    }

    private UserInfo getUserInfo(String isdn) {
        SelfcareResponse<SubMainInfo> mainInfoR = natcomSelfcareService.wsGetSubMainInfo(isdn, "en");
        LoyaltyResponse<List<ViettelAccountDTO>> viettelAccountR = natcomLoyaltyService.getCustomerInfo(isdn, null, null, null, null);

        return new UserInfo(isdn, mainInfoR, viettelAccountR);
    }
}
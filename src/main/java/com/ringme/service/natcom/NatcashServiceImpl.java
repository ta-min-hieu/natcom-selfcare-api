package com.ringme.service.natcom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringme.common.Common;
import com.ringme.common.Helper;
import com.ringme.config.AppConfig;
import com.ringme.config.LocaleFactory;
import com.ringme.dao.mysql.SubServiceDao;
import com.ringme.dao.mysql.selfcare.*;
import com.ringme.dto.natcom.ftth.FtthResponse;
import com.ringme.dto.natcom.natcash.request.CallbackRequest;
import com.ringme.dto.natcom.natcash.request.CredentialRequest;
import com.ringme.dto.natcom.natcash.request.MerchantRequest;
import com.ringme.dto.natcom.natcash.request.WebviewRequestDto;
import com.ringme.dto.natcom.natcash.response.CredentialResponse;
import com.ringme.dto.natcom.natcash.response.MerchantResponse;
import com.ringme.dto.natcom.natcash.response.PaymentStatusData;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.natcash.NatcashResult;
import com.ringme.dto.ringme.natcash.NatcashWebviewInfo;
import com.ringme.dto.ringme.selfcare.ResultMessage;
import com.ringme.enums.natcash.NatcashCallbackCode;
import com.ringme.enums.natcash.NatcashCallbackType;
import com.ringme.enums.natcash.NatcashResponseStatus;
import com.ringme.enums.selfcare.LogCdr;
import com.ringme.model.selfcare.FtthPaymentHistory;
import com.ringme.model.selfcare.NatcashSharePlanHistory;
import com.ringme.model.selfcare.PaymentMobileServiceVasHistory;
import com.ringme.model.selfcare.TopupAirtimeHistory;
import com.ringme.service.ringme.JwtService;
import com.ringme.service.ringme.OTPService;
import com.ringme.service.ringme.RingmeSelfcareService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ringme.common.Helper.safeParseDouble;

@Log4j2
@Service
public class NatcashServiceImpl implements NatcashService {
    @Autowired
    AppConfig appConfig;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtService jwtService;
    @Autowired
    LocaleFactory localeFactory;
    @Autowired
    TopupAirtimeHistoryDao topupAirtimeHistoryDao;
    @Autowired
    NatcashMerchantPaymentHistoryDao merchantPaymentHistoryDao;
    @Autowired
    NatcashMerchantRefundHistoryDao merchantRefundHistoryDao;
    @Autowired
    NatcashCredentialDao credentialDao;
    @Autowired
    FtthService ftthService;
    @Autowired
    FtthPaymentHistoryDao ftthPaymentHistoryDao;
    @Autowired
    SubServiceDao subServiceDao;
    @Autowired
    @Qualifier("redisTemplate")
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    NatcomSelfcareService natcomSelfcareService;
    @Autowired
    PaymentMobileServiceVasHistoryDao paymentMobileServiceVasHistoryDao;
    @Autowired
    SharePlanDao sharePlanDao;
    @Autowired
    OTPService otpService;
    @Autowired
    Common common;
    @Autowired
    RingmeSelfcareService ringmeSelfcareService;

    @Value("${natcash.partnerCode.ftth}")
    private String natcashPartnerCodeFtth;
    @Value("${natcash.username.ftth}")
    private String natcashUsernameFtth;
    @Value("${natcash.password.ftth}")
    private String natcashPasswordFtth;
    @Value("${natcash.functionCode.ftth}")
    private String natcashFunctionCodeFtth;
    @Value("${natcash.privateKey.ftth}")
    private String natcashPrivateKeyFtth;

    @Value("${natcash.partnerCode.mobile-service}")
    private String natcashPartnerCodeMB;
    @Value("${natcash.username.mobile-service}")
    private String natcashUsernameMB;
    @Value("${natcash.password.mobile-service}")
    private String natcashPasswordMB;
    @Value("${natcash.functionCode.mobile-service}")
    private String natcashFunctionCodeMB;
    @Value("${natcash.privateKey.mobile-service}")
    private String natcashPrivateKeyMB;

    @Value("${natcash.partnerCode.topup}")
    private String natcashPartnerCodeTopup;
    @Value("${natcash.username.topup}")
    private String natcashUsernameTopup;
    @Value("${natcash.password.topup}")
    private String natcashPasswordTopup;
    @Value("${natcash.functionCode.topup}")
    private String natcashFunctionCodeTopup;
    @Value("${natcash.privateKey.topup}")
    private String natcashPrivateKeyTopup;

    @Value("${natcash.partnerCode.vas}")
    private String natcashPartnerCodeVas;
    @Value("${natcash.username.vas}")
    private String natcashUsernameVas;
    @Value("${natcash.password.vas}")
    private String natcashPasswordVas;
    @Value("${natcash.functionCode.vas}")
    private String natcashFunctionCodeVas;
    @Value("${natcash.privateKey.vas}")
    private String natcashPrivateKeyVas;

    @Override
    public Response credentialHandler(WebviewRequestDto dto) {
        String callbackUrl;

        switch (dto.getType()) {
            case TOPUP_AIRTIME -> callbackUrl = "/natcash/top-up/airtime/call-back?isdnee=" + dto.getIsdnee() + "&language=" + dto.getLanguage() + "&isdner=" + jwtService.getUsernameFromJwt();
            case FTTH -> callbackUrl = "/natcash/ftth/call-back?ftthAccount=" + dto.getFtthAccount() + "&isdner=" + jwtService.getUsernameFromJwt();
            case MOBILE_SERVICE_VAS -> {
                BigDecimal amount = subServiceDao.getPackageAmountByLanguageAndCode(dto.getPackageCode());
                if(amount == null) {
                    log.error("Not found package| language: {}, packageCode: {}", dto.getLanguage(), dto.getPackageCode());
                    return new Response(11, "Not found package", null);
                }
                dto.setAmount(amount);

                callbackUrl = "/natcash/payment-mobile-service-vas/call-back?isdnee=" + jwtService.getUsernameFromJwt() + "&language=" + dto.getLanguage() + "&packageCode=" + dto.getPackageCode() + "&isdner=" + jwtService.getUsernameFromJwt();
            }
            case SHARE_PLAN -> {
                BigDecimal amount = subServiceDao.getPackageAmountByLanguageAndCode(dto.getPackageCode());
                if(amount == null) {
                    log.error("Not found package| language: {}, packageCode: {}", dto.getLanguage(), dto.getPackageCode());
                    return new Response(11, "Not found package", null);
                }
                dto.setAmount(amount);

                callbackUrl = "/natcash/share-plan/call-back?isdner=" + jwtService.getUsernameFromJwt() + "&language=" + dto.getLanguage() + "&packageCode=" + dto.getPackageCode() + "&isdnee=" + dto.getIsdnee();
            }
            default -> {
                return new Response(400, "Type is invalid");
            }
        }

        return new Response(200, "OK", credential(dto, callbackUrl));
    }

    private CredentialResponse credential(WebviewRequestDto dto, String callbackUrl) {
        try {
            NatcashWebviewInfo info = handleNatcashWebviewInfo(dto.getType());
            String isdn = appConfig.getIsdnCoutryCode() + jwtService.getUsernameFromJwt();

            CredentialRequest request = new CredentialRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setPartnerCode(info.getPartnerCode());
            request.setUsername(info.getUsername());
            request.setPassword(info.getPassword());
            request.setDeviceId(dto.getDeviceId());
            request.setDeviceModel(dto.getDeviceModel());
            request.setOsName(dto.getOsName());
            request.setOsVersion(dto.getOsVersion());
            request.setCallbackUrl(callbackUrl.startsWith("http") ? callbackUrl : appConfig.getBaseUrl() + callbackUrl);
            request.setTimestamp(System.currentTimeMillis() + appConfig.getServerTimeAdd());
            request.setOrderNumber(generateOrderNumber());
//            if(dto.getType().equals(NatcashCallbackType.TOPUP_AIRTIME))
//                request.setAmount(Helper.calculateTkg(dto.getAmount()));
//            else
                request.setAmount(dto.getAmount());
            request.setMsisdn(isdn);
            request.setSignature(generateSignatureRequestByPrivateKey(dto.getType(), request.getRequestId(), request.getTimestamp(), request.getOrderNumber(), request.getAmount()));
            request.setLanguage(dto.getLanguage());
            CredentialResponse response = callApiNatcash(appConfig.getNatcashCredential(), request, new TypeReference<>() {
            });

            if (response != null && response.getStatus() == NatcashResponseStatus.MSG_SUCCESS)
                credentialDao.store(request, dto.getType());

            return response;
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }

        return null;
    }

    @Override
    public MerchantResponse checkTransaction(NatcashCallbackType type, String requestId, String orderNumber) {
        try {
            NatcashWebviewInfo info = handleNatcashWebviewInfo(type);
            MerchantRequest request = new MerchantRequest();
            request.setUsername(info.getUsername());
            request.setPassword(info.getPassword());
            request.setPartnerCode(info.getPartnerCode());
            request.setOrderNumber(orderNumber);
            request.setSignature(generateSignatureCheckTransactionAndCancelByPrivateKey(type, requestId, orderNumber));
            request.setRequestId(requestId);

            return callApiNatcash(appConfig.getNatcashCheckTransaction(), request, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }

        return null;
    }

    @Override
    public MerchantResponse cancelTrans(NatcashCallbackType type, String requestId, String orderNumber) {
        try {
            NatcashWebviewInfo info = handleNatcashWebviewInfo(type);

            MerchantRequest request = new MerchantRequest();
            request.setUsername(info.getUsername());
            request.setPassword(info.getPassword());
            request.setPartnerCode(info.getPartnerCode());
            request.setRequestId(requestId);
            request.setOrderNumber(orderNumber);
            request.setSignature(generateSignatureCheckTransactionAndCancelByPrivateKey(type, request.getRequestId(), request.getOrderNumber()));

            return callApiNatcash(appConfig.getNatcashCancelTransaction(), request, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }

        return null;
    }

    public String callbackHandler(Model model, CallbackRequest request, NatcashCallbackType natcashCallbackType) {
        NatcashResult obj = new NatcashResult();
        try {
            String signature = generateSignatureByFunctionCode(natcashCallbackType, request.getOrderNumber(), request.getCode().getType());
            if (signature.equals(request.getSignature())) {
                if (request.getCode().equals(NatcashCallbackCode.SUCCESS)) {
                    if(!isFirstRequest(request)) {
                        log.error("Not first callback| request: {}, natcashCallbackType: {},\ncredRequest: {}", request, natcashCallbackType, request);
                        return setError(model, obj);
                    }

                    CredentialRequest credRequest = credentialDao.selectByOrderNumber(request.getOrderNumber());
                    if (credRequest == null || credRequest.getRequestId() == null) {
                        log.error("Not found credential request| request: {}, natcashCallbackType: {},\ncredRequest: {}", request, natcashCallbackType, request);
                        return setError(model, obj);
                    }

                    MerchantResponse merchantResponse = checkTransaction(natcashCallbackType, credRequest.getRequestId(), request.getOrderNumber());
                    if (merchantResponse != null) {
                        PaymentStatusData data = merchantResponse.getData();
                        if (data != null) {
                            if(!merchantPaymentHistoryDao.store(data, credRequest.getRequestId())) {
                                log.error("Not first callback 2| request: {}, natcashCallbackType: {},\ncredRequest: {}", request, natcashCallbackType, request);
                            }
                            if (data.getResponseCode() == 1) {
                                try {
                                    boolean rs = false;

                                    String amount = data.getAmount().replace(",", "").replace("HTG", "").trim();
                                    if (!Helper.roundingMode(Double.parseDouble(amount)).equals(credRequest.getAmount())) {
                                        log.error("not enough credential amount| request: {}, natcashCallbackType: {},\ncheckTransaction: {}\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                                        refundHandler(natcashCallbackType, credRequest, data.getTransId(), request.getOrderNumber());
                                        return setError(model, obj);
                                    }

                                    String message = null;

                                    switch (natcashCallbackType) {
                                        case TOPUP_AIRTIME -> rs = topupAirtime(request.getIsdnee(), amount, request.getOrderNumber(), merchantResponse.getData().getToPhone(), request.getLanguage(), request.getIsdner());
                                        case FTTH -> rs = ftthPaymentHandle(request.getOrderNumber(), request.getFtthAccount(), safeParseDouble(amount), merchantResponse.getData().getToPhone(), request.getIsdner());
                                        case MOBILE_SERVICE_VAS -> {
                                            ResultMessage rm = paymentMobileServiceVasHandle(request.getLanguage(), request.getOrderNumber(), request.getPackageCode(), safeParseDouble(amount), request.getIsdnee(), merchantResponse.getData().getToPhone(), request.getIsdner());
                                            rs = rm.isResult();
                                            message = rm.getMessage();
                                        }
                                        case SHARE_PLAN -> {
                                            ResultMessage rm = sharePlanHandle(request.getLanguage(), request.getOrderNumber(), request.getPackageCode(), safeParseDouble(amount), request.getIsdnee(), request.getIsdner(), merchantResponse.getData().getToPhone());
                                            rs = rm.isResult();
                                            message = rm.getMessage();
                                        }
                                        default -> log.warn("Unknow type callback| request: {}, natcashCallbackType: {},\ncheckTransaction: {}\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                                    }

                                    if (rs) {
                                        log.info("success request: {},\nnatcashCallbackType: {},\ncheckTransaction: {}\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                                        return setSuccess(model, obj, message);
                                    } else {
                                        log.error("payment error request: {},\nnatcashCallbackType: {},\ncheckTransaction: {},\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                                        refundHandler(natcashCallbackType, credRequest, data.getTransId(), request.getOrderNumber());

                                        return setError(model, obj, message);
                                    }
                                } catch (Exception e) {
                                    log.error("error when buy service| callbackRequest: {},\nnatcashCallbackType: {},\ncheckTransaction: {},\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                                    refundHandler(natcashCallbackType, credRequest, data.getTransId(), request.getOrderNumber());
                                    return setError(model, obj);
                                }
                            } else {
                                log.error("Transaction fail| request: {},\nnatcashCallbackType: {},\ncheckTransaction: {},\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                                return setError(model, obj);
                            }
                        } else {
                            log.error("payment status data is null| request: {},\nnatcashCallbackType: {}\ncheckTransaction: {},\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                            return setError(model, obj);
                        }
                    } else {
                        log.error("merchant response is null| callbackRequest: {},\nnatcashCallbackType: {},\ncheckTransaction: {},\ncredRequest: {}", request, natcashCallbackType, merchantResponse, credRequest);
                        return setError(model, obj);
                    }
                } else {
                    log.error("payment error request: {},\nnatcashCallbackType: {}", request, natcashCallbackType);
                    return setError(model, obj);
                }
            } else {
                log.error("validate signature error request: {}, natcashCallbackType: {}", request, natcashCallbackType);
                return setError(model, obj);
            }
        } catch (Exception e) {
            log.error("system error| request: {}, natcashCallbackType: {}, {}", request, natcashCallbackType, e.getMessage(), e);
            return setError(model, obj);
        }
    }

    private boolean isFirstRequest(CallbackRequest request) {
        String key = request.getOrderNumber();
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) {
            redisTemplate.opsForValue().set(key, request, 1, TimeUnit.MINUTES);
            return true;
        }

        return false;
    }

    private ResultMessage paymentMobileServiceVasHandle(String language, String orderNumber, String packageCode, double money, String isdnee, String msisdnerPay, String isdner) {
        try {
            BigDecimal amount = subServiceDao.getPackageAmountByLanguageAndCode(packageCode);
            if(amount == null || !amount.equals(Helper.roundingMode(money))) {
                log.error("Amount invalid| amountValid: {}, language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", amount, language, orderNumber, packageCode, money, isdnee);
                return new ResultMessage(false, "Amount invalid");
            }

            SelfcareResponse<Object> response = natcomSelfcareService.paymentMobileServiceAndVasFree(language, isdnee, packageCode);

            boolean rsStore = paymentMobileServiceVasHistoryDao.store(new PaymentMobileServiceVasHistory(response, orderNumber, isdnee, packageCode, money));

            if (!rsStore) {
                log.error("Store history error| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", language, orderNumber, packageCode, money, isdnee);
                return new ResultMessage(false, "Store history error");
            }

            if (!response.getErrorCode().equals("0")) {
                log.error("Error code != 0| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", language, orderNumber, packageCode, money, isdnee);
                return new ResultMessage(false, response.getUserMsg());
            }

            log.info("success| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", language, orderNumber, packageCode, money, isdnee);
            Common.logCdr(ringmeSelfcareService.isVasPackage(packageCode) ? LogCdr.NATCASH_BUY_VAS.getType() : LogCdr.NATCASH_BUY_MOBILE_SERVICE.getType(), isdner, packageCode, String.valueOf(amount), "");
            //            return new ResultMessage(true, response.getUserMsg());
            return new ResultMessage(true, "");
        } catch (Exception e) {
            log.error("System error| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", language, orderNumber, packageCode, money, isdnee, e);
            return new ResultMessage(false, "System error");
        }
    }

    private ResultMessage sharePlanHandle(String language, String orderNumber, String packageCode, double money, String isdnee, String isdner, String msisdnerPay) {
        try {
            BigDecimal amount = subServiceDao.getPackageAmountByLanguageAndCode(packageCode);
            if(amount == null || !amount.equals(Helper.roundingMode(money))) {
                log.error("Amount invalid| amountValid: {}, language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", amount, language, orderNumber, packageCode, money, isdnee);
                return new ResultMessage(false, "Amount invalid");
            }

            SelfcareResponse<Object> response = natcomSelfcareService.paymentMobileServiceAndVasFree(language, isdner, isdnee, packageCode);

            boolean rsStore = sharePlanDao.store(new NatcashSharePlanHistory(response, orderNumber, isdner, isdnee, packageCode, money));

            if (!rsStore) {
                log.error("Store history error| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}, response: {}", language, orderNumber, packageCode, money, isdnee, response);
                return new ResultMessage(false, "Store history invalid");
            }

            if (!response.getErrorCode().equals("0")) {
                log.error("Error code != 0| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}, response: {}", language, orderNumber, packageCode, money, isdnee, response);
                return new ResultMessage(false, response.getUserMsg());
            }

            log.info("success| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}, response: {}", language, orderNumber, packageCode, money, isdnee, response);
            Common.logCdr(ringmeSelfcareService.isVasPackage(packageCode) ? LogCdr.NATCASH_SHARE_VAS.getType() : LogCdr.NATCASH_SHARE_MOBILE_SERVICE.getType(), isdner, packageCode, String.valueOf(amount), isdnee);
            //            return new ResultMessage(true, response.getUserMsg());
            return new ResultMessage(true, "");
        } catch (Exception e) {
            log.error("System error| language: {}, orderNumber: {}, packageCode: {}, money: {}, isdnee: {}", language, orderNumber, packageCode, money, isdnee, e);
            return new ResultMessage(false, "System error");
        }
    }

    private boolean ftthPaymentHandle(String orderNumber, String ftthAccount, double moneyHaiti, String msisdnerPay, String isdner) {
        double moneyDolar = 0;
        double rate = 0;
        try {
            rate = ftthService.getExchangeRateHandle();
            moneyDolar = BigDecimal.valueOf(moneyHaiti / rate)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            FtthResponse response = ftthService.wsPayment(ftthAccount, moneyDolar);

            boolean rsStore = ftthPaymentHistoryDao.store(new FtthPaymentHistory(response, orderNumber, ftthAccount, moneyDolar, moneyHaiti));

            if (response.getErrorCode() != 0 || !rsStore) {
                log.error("System error| orderNumber: {}, ftthAccount: {}, moneyHaiti: {}, moneyDolar: {}, rate: {}", orderNumber, ftthAccount, moneyHaiti, moneyDolar, rate);
                return false;
            }

            log.info("Payment ftth success| orderNumber: {}, ftthAccount: {}, moneyHaiti: {}, moneyDolar: {}, rate: {}", orderNumber, ftthAccount, moneyHaiti, moneyDolar, rate);
            Common.logCdr(LogCdr.NATCASH_FTTH.getType(), isdner, "", String.valueOf(Helper.roundingMode(moneyHaiti)), ftthAccount);
            return true;
        } catch (Exception e) {
            log.error("System error| orderNumber: {}, ftthAccount: {}, moneyHaiti: {}, moneyDolar: {}, rate: {}", orderNumber, ftthAccount, moneyHaiti, moneyDolar, rate, e);
            return false;
        }
    }

    private void refundHandler(NatcashCallbackType natcashCallbackType, CredentialRequest credRequest, String transaction, String orderNumber) {
        MerchantResponse cancelTrans = cancelTrans(natcashCallbackType, credRequest.getRequestId(), orderNumber);
        if (cancelTrans != null && cancelTrans.getStatus() != null && cancelTrans.getStatus() == NatcashResponseStatus.MSG_SUCCESS) {
            log.info("Refund success| natcashCallbackType: {},\ncancelTransaction: {},\ncredRequest: {}", natcashCallbackType, cancelTrans, credRequest);

//            otpService.sendOTP(credRequest.getMsisdn(), localeFactory.getMessage("sms.refund-to-sender", credRequest.getLanguage())
//                    .replace("{{amount}}", credRequest.getAmount().toString()));
        } else
            log.error("Refund fail| natcashCallbackType: {},\ncancelTransaction: {},\ncredRequest: {}", natcashCallbackType, cancelTrans, credRequest);

        if (cancelTrans != null)
            merchantRefundHistoryDao.store(cancelTrans.getStatus().getType(), credRequest.getRequestId(), orderNumber, credRequest.getMsisdn(), transaction, credRequest.getAmount());
    }

    @Override
    public boolean topupAirtime(String isdnee, String amount, String orderNumber, String isdner) {
        return topupAirtime(isdnee, amount, orderNumber, null, "en", isdner);
    }

    private boolean topupAirtime(String isdnee, String amount, String orderNumber, String msisdnerPay, String language, String isdner) {
        TopupAirtimeHistory obj = null;
        String requestId = null;
        if (isdnee.startsWith("509"))
            isdnee = isdnee.substring("509".length());

        String amountOriginal = amount;
        amount = Helper.calculateTkg(amount);

        try {
            requestId = UUID.randomUUID().toString();

            String soapRequest = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.vas.viettel.com/">
                       <soapenv:Header/>
                       <soapenv:Body>
                          <web:gwOperation>
                             <username>{username}</username>
                             <password>{password}</password>
                             <wscode>{wscode}</wscode>
                             <requestId>{requestId}</requestId>
                             <msisdn>{isdn}</msisdn>
                             <ammount>{amount}</ammount>
                             <carriedRole>{ROLE_ID}</carriedRole>
                          </web:gwOperation>
                       </soapenv:Body>
                    </soapenv:Envelope>
                    """.replace("{username}", appConfig.getNatcashTopUpUsername())
                    .replace("{password}", appConfig.getNatcashTopUpPassword())
                    .replace("{wscode}", appConfig.getNatcashTopUpWscode())
                    .replace("{requestId}", requestId)
                    .replace("{isdn}", isdnee)
                    .replace("{amount}", amount)
                    .replace("{ROLE_ID}", appConfig.getNatcashTopUpRoleId());

            String response = callSoapToupAirtime(soapRequest);
            if (response == null) {
                log.error("response is null| requestId: {}, amount: {}, isdn: {}, orderNumber: {}, msisdnerPay: {}, isdner: {}, language: {}", requestId, amount, isdnee, orderNumber, msisdnerPay, isdner, language);
                topupAirtimeHistoryDao.store(new TopupAirtimeHistory(requestId, BigDecimal.valueOf(Double.parseDouble(amount)), isdnee));
                return false;
            }

            obj = mapResponse(response, requestId, BigDecimal.valueOf(Double.parseDouble(amount)), isdnee, orderNumber);

            topupAirtimeHistoryDao.store(obj);

            if (obj.getErrorCode() != null && obj.getErrorCode() == 0) {
                log.info("success| history: {}", obj);
                Common.logCdr(LogCdr.NATCASH_RECHARGE.getType(), isdner, "", amountOriginal, (isdnee).equals(isdner) ? "" : isdnee);

                if(isdner != null) {
                    boolean isOnePhone = (isdnee).equals(isdner);
                    if(!isOnePhone)
                        otpService.sendOTP(isdner, localeFactory.getMessage("sms.recharge-to-sender", language)
                                .replace("{{receiver_number}}", isdnee)
                                .replace("{{time}}", common.getTime()));
                }

                return true;
            }

            log.error("fail| history: {}", obj);
            return false;
        } catch (Exception e) {
            log.error("System error| requestId: {}, amount: {}, isdn: {}, msisdner: {}, language: {}, {}", requestId, amount, isdnee, msisdnerPay, language, e.getMessage(), e);
            topupAirtimeHistoryDao.store(new TopupAirtimeHistory(requestId, BigDecimal.valueOf(Double.parseDouble(amount)), isdnee));
        }
        return false;
    }

    private String generateOrderNumber() {
        return (UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()).substring(10);
    }

    private String setError(Model model, NatcashResult obj) {
        obj.setSuccess(false);
        obj.setTitle(localeFactory.getMessage("natcash.payment.fail.title"));
        obj.setDescription(localeFactory.getMessage("natcash.payment.fail.description"));
        model.addAttribute("obj", obj);

        return "natcash/result";
    }

    private String setError(Model model, NatcashResult obj, String message) {
        obj.setSuccess(false);
        obj.setTitle(localeFactory.getMessage("natcash.payment.fail.title"));
        if(message == null || message.isEmpty())
            obj.setDescription(localeFactory.getMessage("natcash.payment.fail.description"));
        else
            obj.setDescription(message);
        model.addAttribute("obj", obj);

        return "natcash/result";
    }

    private String setSuccess(Model model, NatcashResult obj) throws InterruptedException {
        obj.setSuccess(true);
        obj.setTitle(localeFactory.getMessage("natcash.payment.success.tilte"));
        obj.setDescription(localeFactory.getMessage("natcash.payment.success.description"));
        model.addAttribute("obj", obj);
        Thread.sleep(Duration.ofSeconds(2));
        return "natcash/result";
    }

    private String setSuccess(Model model, NatcashResult obj, String message) throws InterruptedException {
        obj.setSuccess(true);
        obj.setTitle(localeFactory.getMessage("natcash.payment.success.tilte"));
        if(message == null || message.isEmpty())
            obj.setDescription(localeFactory.getMessage("natcash.payment.success.description"));
        else
            obj.setDescription(message);
        model.addAttribute("obj", obj);
        Thread.sleep(Duration.ofSeconds(2));
        return "natcash/result";
    }

    private String generateSignatureRequestByPrivateKey(NatcashCallbackType type, String requestId, long timestamp, String orderNumber, BigDecimal amount) throws Exception {
        NatcashWebviewInfo info = handleNatcashWebviewInfo(type);
        String privateKey = info.getPrivateKey();

        String accessKey = Helper.sha256(privateKey + requestId);
        return Helper.hmacSha256(privateKey, accessKey + info.getPartnerCode() + info.getUsername() + info.getPassword() + timestamp + requestId + orderNumber + amount);
    }

    private String generateSignatureByFunctionCode(NatcashCallbackType type, String orderNumber, int code) throws Exception {
        NatcashWebviewInfo info = handleNatcashWebviewInfo(type);
        String functionCode = info.getFunctionCode();

        String accessKey = Helper.sha256(functionCode + orderNumber);
        return Helper.hmacSha256(functionCode, accessKey + orderNumber + code);
    }

    private String generateSignatureCheckTransactionAndCancelByPrivateKey(NatcashCallbackType type, String requestId, String orderNumber) throws Exception {
        NatcashWebviewInfo info = handleNatcashWebviewInfo(type);
        String privateKey = info.getPrivateKey();

        String accessKey = Helper.sha256(privateKey + requestId);
        return Helper.hmacSha256(privateKey, accessKey + info.getPartnerCode() + info.getUsername() + info.getPassword() + orderNumber + requestId);
    }

    private <T> T callApiNatcash(String url, Object body, TypeReference<T> responseType) {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(body, headers);

            response = restTemplate.postForEntity(url, entity, String.class);

            log.info("Success url: {}, body: {}, responseType: {}, response: {}", url, body, responseType, response);
            return objectMapper.readValue(response.getBody(), responseType);
        } catch (Exception e) {
            log.error("Error url: {}, body: {}, responseType: {}, response: {}, error: {}", url, body, responseType, response, e.getMessage(), e);
        }
        return null;
    }

    private String callSoapToupAirtime(String soapRequest) {
        ResponseEntity<String> response = null;
        String url = appConfig.getNatcashTopUpAirtime();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.setAccept(Collections.singletonList(MediaType.TEXT_XML));

            HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);

            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.postForEntity(url, request, String.class);

            log.info("success url: {}, soapRequest: {},\nresponse: {}", url, soapRequest, response);
            return response.getBody();
        } catch (Exception e) {
            log.error("fail url: {}, soapRequest: {},\nresponse: {}, {}", url, soapRequest, response, e.getMessage(), e);
        }
        return null;
    }

    private TopupAirtimeHistory mapResponse(String xml, String requestId, BigDecimal amount, String isdn, String orderNumber) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new java.io.ByteArrayInputStream(xml.getBytes()));

            XPath xpath = XPathFactory.newInstance().newXPath();

            String content = xpath.evaluate("//*[local-name()='content']", doc);
            String description = xpath.evaluate("//*[local-name()='description']", doc);
            String errorCode = xpath.evaluate("//*[local-name()='errorCode']", doc);
            String transactionId = xpath.evaluate("//*[local-name()='transactionId']", doc);

            return new TopupAirtimeHistory(requestId, amount, isdn, transactionId, Integer.valueOf(errorCode), content, description, orderNumber);
        } catch (Exception e) {
            log.error("xml: {}, error: {}", xml, e.getMessage(), e);
        }
        return new TopupAirtimeHistory();
    }

    private NatcashWebviewInfo handleNatcashWebviewInfo(NatcashCallbackType type) {
        NatcashWebviewInfo webviewInfo = new NatcashWebviewInfo();
        switch (type) {
            case TOPUP_AIRTIME -> {
                webviewInfo.setFunctionCode(natcashFunctionCodeTopup);
                webviewInfo.setPrivateKey(natcashPrivateKeyTopup);
                webviewInfo.setUsername(natcashUsernameTopup);
                webviewInfo.setPassword(natcashPasswordTopup);
                webviewInfo.setPartnerCode(natcashPartnerCodeTopup);
            }
            case MOBILE_SERVICE_VAS, SHARE_PLAN -> {
                webviewInfo.setFunctionCode(natcashFunctionCodeMB);
                webviewInfo.setPrivateKey(natcashPrivateKeyMB);
                webviewInfo.setUsername(natcashUsernameMB);
                webviewInfo.setPassword(natcashPasswordMB);
                webviewInfo.setPartnerCode(natcashPartnerCodeMB);
            }
            case FTTH -> {
                webviewInfo.setFunctionCode(natcashFunctionCodeFtth);
                webviewInfo.setPrivateKey(natcashPrivateKeyFtth);
                webviewInfo.setUsername(natcashUsernameFtth);
                webviewInfo.setPassword(natcashPasswordFtth);
                webviewInfo.setPartnerCode(natcashPartnerCodeFtth);
            }
        }

        return webviewInfo;
    }
}

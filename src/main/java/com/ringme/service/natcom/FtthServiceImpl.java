package com.ringme.service.natcom;

import com.ringme.config.AppConfig;
import com.ringme.dao.mysql.FtthDao;
import com.ringme.dto.natcom.ftth.FtthResponse;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.ftth.PaymentFtthInfo;
import com.ringme.dto.ringme.ftth.ViewAccount;
import com.ringme.model.selfcare.FtthPackage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ringme.common.Helper.safeParseDouble;

@Log4j2
@Service
public class FtthServiceImpl implements FtthService {
    @Autowired
    AppConfig appConfig;
    @Autowired
    FtthDao ftthDao;

    @Override
    public Response findAccountInfo(String ftthAccount) {
        double exchangeRate = getExchangeRateHandle();
        if(exchangeRate == 0) {
            log.error("getExchangeRate is empty, exchange rate not found| ftthAccount: {}, exchangeRate: {}", ftthAccount, exchangeRate);
            return new Response(1, "Exchange rate not found");
        }

        FtthResponse v = wsViewAccontAndEmail(ftthAccount);

        if(v == null || v.getErrorCode() != 0) {
            log.error("wsViewAccontAndEmail is empty| ftthAccount: {}, FtthResponse: {}", ftthAccount, v);
            return new Response(2, "Not found FTTH Account");
        }

        PaymentFtthInfo p = ftthDao.getPaymentFtthAccountInfo(ftthAccount);
        if(p == null) {
            log.error("PaymentFtthInfo is empty| ftthAccount: {}, FtthResponse: {}, PaymentFtthInfo: {}", ftthAccount, v, p);
            return new Response(3, "Not found Payment Ftth Info");
        }

        return new Response(200, "Success", viewAccountsHandler(ftthAccount, v, exchangeRate, p));
    }

    @Override
    public FtthResponse wsPayment(String ftthAccount, double money) {
        String response = null;
        FtthResponse obj = new FtthResponse();
        try {
            String soapRequest = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.vas.viettel.com/">
                        <soapenv:Header/>
                        <soapenv:Body>
                            <web:wsPayment>
                                <User>{{user}}</User>
                                <Pass>{{pass}}</Pass>
                                <Account>{{ftthAccount}}</Account>
                                <Money>{{money}}</Money>
                            </web:wsPayment>
                        </soapenv:Body>
                    </soapenv:Envelope>
                    """.replace("{{user}}", appConfig.getFtthUsername())
                    .replace("{{pass}}", appConfig.getFtthPassword())
                    .replace("{{ftthAccount}}", ftthAccount)
                    .replace("{{money}}", String.valueOf(money));

            response = callSoapFtth(soapRequest);
            if(response == null) {
                log.error("response is null| ftthAccount: {}", ftthAccount);
                return new FtthResponse();
            }

            obj = mapFtthResponse(response);

            if(obj.getErrorCode() != null && obj.getErrorCode() == 0) {
                log.info("success| ftthAccount: {}, response: {}, obj: {}", ftthAccount, response, obj);
                return obj;
            }

            log.error("fail| ftthAccount: {}, response: {}", ftthAccount, response);
            return obj;
        } catch (Exception e) {
            log.error("System error| ftthAccount: {}, response: {}, obj: {}, {}", ftthAccount, response, obj, e.getMessage(), e);
        }
        return obj;
    }

    @Override
    public double getExchangeRateHandle() {
        FtthResponse exchangeRate = getExchangeRate();
        if(exchangeRate == null || exchangeRate.getErrorCode() != 0 || exchangeRate.getContent() == null)
            return 0;

        return safeParseDouble(exchangeRate.getContent());
    }

    @Override
    public SelfcareResponse<FtthPackage> getFtthPackageById(String language, long id) {
        SelfcareResponse<FtthPackage> response = new SelfcareResponse<>();
        response.setErrorCode("0");
        response.setUserMsg("Successfully!");
        response.setWsResponse(ftthDao.getFtthPackageById(language, id));
        return response;
    }

    @Override
    public SelfcareResponse<List<FtthPackage>> getFtthPackages(String language) {
        SelfcareResponse<List<FtthPackage>> response = new SelfcareResponse<>();
        response.setErrorCode("0");
        response.setUserMsg("Successfully!");
        response.setWsResponse(ftthDao.getFtthPackages(language));
        return response;
    }

    private List<ViewAccount> viewAccountsHandler(String ftthAccount, FtthResponse v, double exchangeRate, PaymentFtthInfo p) {
        List<ViewAccount> viewAccounts = new ArrayList<>();

        viewAccounts.add(payOffOldDebtHandle(ftthAccount, v, exchangeRate, p));

        if(v.getMonthlyFee() != null && v.getMonthlyFee() > 0) {
            viewAccounts.add(prepaid3MonthsHandle(ftthAccount, v, exchangeRate, p));
            viewAccounts.add(prepaid6MonthsHandle(ftthAccount, v, exchangeRate, p));
            viewAccounts.add(prepaid12MonthsHandle(ftthAccount, v, exchangeRate, p));
        }

        return viewAccounts;
    }

    private ViewAccount payOffOldDebtHandle(String ftthAccount, FtthResponse v, double exchangeRate, PaymentFtthInfo p) {
        ViewAccount viewAccount = new ViewAccount(ftthAccount, v, exchangeRate, p);

        viewAccount.setInfoName("Pay off old debt");
        viewAccount.setInfoDescriptionName("Installation fee");
        viewAccount.setInfoDescriptionValue("Free");
        viewAccount.setPaymentType("Pay off old debt");
        viewAccount.setPaymentAmount(oldDebtHandle(v));
        viewAccount.setPaymentAmountHotCharge(settlementBeforeServiceCancellation(v));
        viewAccount.setInfoAmount(viewAccount.getPaymentAmount());

        return viewAccount;
    }

    private ViewAccount prepaid3MonthsHandle(String ftthAccount, FtthResponse v, double exchangeRate, PaymentFtthInfo p) {
        ViewAccount viewAccount = new ViewAccount(ftthAccount, v, exchangeRate, p);

        viewAccount.setInfoName("Pay 3 months in advance");
        viewAccount.setInfoDescriptionName("Bonus");
        viewAccount.setInfoDescriptionValue("1 month");
        viewAccount.setPaymentType("Pay 3 months in advance");
        viewAccount.setNumberOfMonthsOfUse("4 months ( Including 1 month bonus )");
        viewAccount.setTotalPrice(totalPrice(v, 3));
        viewAccount.setPaymentAmount(prepaidMonths(v, 3));
        viewAccount.setInfoAmount(viewAccount.getTotalPrice());

        return viewAccount;
    }

    private ViewAccount prepaid6MonthsHandle(String ftthAccount, FtthResponse v, double exchangeRate, PaymentFtthInfo p) {
        ViewAccount viewAccount = new ViewAccount(ftthAccount, v, exchangeRate, p);

        viewAccount.setInfoName("Pay 6 months in advance");
        viewAccount.setInfoDescriptionName("Bonus");
        viewAccount.setInfoDescriptionValue("2 months");
        viewAccount.setPaymentType("Pay 6 months in advance");
        viewAccount.setNumberOfMonthsOfUse("8 months ( Including 2 months bonus )");
        viewAccount.setTotalPrice(totalPrice(v, 6));
        viewAccount.setPaymentAmount(prepaidMonths(v, 6));
        viewAccount.setInfoAmount(viewAccount.getTotalPrice());

        return viewAccount;
    }

    private ViewAccount prepaid12MonthsHandle(String ftthAccount, FtthResponse v, double exchangeRate, PaymentFtthInfo p) {
        ViewAccount viewAccount = new ViewAccount(ftthAccount, v, exchangeRate, p);

        viewAccount.setInfoName("Pay 12 months in advance");
        viewAccount.setInfoDescriptionName("Bonus");
        viewAccount.setInfoDescriptionValue("4 months");
        viewAccount.setPaymentType("Pay 12 months in advance");
        viewAccount.setNumberOfMonthsOfUse("16 months ( Including 4 months bonus )");
        viewAccount.setTotalPrice(totalPrice(v, 12));
        viewAccount.setPaymentAmount(prepaidMonths(v, 12));
        viewAccount.setInfoAmount(viewAccount.getTotalPrice());

        return viewAccount;
    }

    private double oldDebtHandle(FtthResponse v) {
        return v.getContractDebt() - v.getPayment() - v.getRemainPayment();
    }

    private double settlementBeforeServiceCancellation(FtthResponse v) {
        return oldDebtHandle(v) + v.getHotcharge();
    }

    private double prepaidMonths(FtthResponse v, int months) {
        return oldDebtHandle(v) + totalPrice(v, months);
    }

    private double totalPrice(FtthResponse v, int months) {
        return v.getMonthlyFee() * months;
    }

    private FtthResponse wsViewAccontAndEmail(String ftthAccount) {
        String response = null;
        FtthResponse obj = new FtthResponse();
        try {

            String soapRequest = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.vas.viettel.com/">
                        <soapenv:Header/>
                        <soapenv:Body>
                            <web:wsViewAccontAndEmail>
                                <User>{{user}}</User>
                                <Pass>{{pass}}</Pass>
                                <Account>{{ftthAccount}}</Account>
                                <Email></Email>
                            </web:wsViewAccontAndEmail>
                        </soapenv:Body>
                    </soapenv:Envelope>
                    """.replace("{{user}}", appConfig.getFtthUsername())
                    .replace("{{pass}}", appConfig.getFtthPassword())
                    .replace("{{ftthAccount}}", ftthAccount);

            response = callSoapFtth(soapRequest);
            if(response == null) {
                log.error("response is null| ftthAccount: {}", ftthAccount);
                return null;
            }

            obj = mapFtthResponse(response);

            if(obj.getErrorCode() != null && obj.getErrorCode() == 0) {
                log.info("success| ftthAccount: {}, response: {}, obj: {}", ftthAccount, response, obj);
                return obj;
            }

            log.error("fail| ftthAccount: {}, response: {}", ftthAccount, response);
            return null;
        } catch (Exception e) {
            log.error("System error| ftthAccount: {}, response: {}, obj: {}, {}", ftthAccount, response, obj, e.getMessage(), e);
        }
        return null;
    }

    private FtthResponse getExchangeRate() {
        String response = null;
        FtthResponse obj = new FtthResponse();
        try {

            String soapRequest = """
                        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.vas.viettel.com/">
                            <soapenv:Header/>
                            <soapenv:Body>
                                <web:getExchangeRate>
                                    <wsUser>{{user}}</wsUser>
                                    <wsPass>{{pass}}</wsPass>
                                </web:getExchangeRate>
                            </soapenv:Body>
                        </soapenv:Envelope>
                    """.replace("{{user}}", appConfig.getFtthUsername())
                    .replace("{{pass}}", appConfig.getFtthPassword());

            response = callSoapFtth(soapRequest);
            if(response == null) {
                log.error("response is null");
                return null;
            }

            obj = mapFtthResponse(response);

            if(obj.getErrorCode() != null && obj.getErrorCode() == 0) {
                log.info("success| response: {}, obj: {}", response, obj);
                return obj;
            }

            log.error("fail| response: {}", response);
            return null;
        } catch (Exception e) {
            log.error("System error| response: {}, obj: {}, {}", response, obj, e.getMessage(), e);
        }
        return null;
    }

    private String callSoapFtth(String soapRequest) {
        ResponseEntity<String> response = null;
        String url = appConfig.getFtthBaseUrl();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.setAccept(Collections.singletonList(MediaType.TEXT_XML));

            HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);

            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.postForEntity(url, request, String.class);

            log.info("success url: {}, soapRequest: {}, response: {}", url, soapRequest, response);
            return response.getBody();
        } catch (Exception e) {
            log.error("fail url: {}, soapRequest: {},\nresponse: {}, {}", url, soapRequest, response, e.getMessage(), e);
        }
        return null;
    }

    private FtthResponse mapFtthResponse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            XPath xpath = XPathFactory.newInstance().newXPath();

            XPathExpression exprReturn = xpath.compile("//*[local-name()='return']");

            Node returnNode = (Node) exprReturn.evaluate(doc, XPathConstants.NODE);

            if (returnNode == null) {
                log.warn("Không tìm thấy thẻ <return> trong XML");
                return new FtthResponse();
            }

            Element returnElement = (Element) returnNode;

            String address        = getText(returnElement,"address");
            String content        = getText(returnElement,"content");
            String contractDebt   = getText(returnElement,"contractDebt");
            String contractNo     = getText(returnElement,"contractNo");
            String errorCode      = getText(returnElement,"errorCode");
            String hotcharge      = getText(returnElement,"hotcharge");
            String monthlyFee     = getText(returnElement,"monthlyFee");
            String payer          = getText(returnElement,"payer");
            String payment        = getText(returnElement,"payment");
            String productCode    = getText(returnElement,"productCode");
            String remainPayment  = getText(returnElement,"remainPayment");
            String service        = getText(returnElement,"service");
            String status         = getText(returnElement,"status");
            String subId          = getText(returnElement,"subId");
            String telFax         = getText(returnElement,"telFax");

            return new FtthResponse(
                    address,
                    content,
                    contractNo,
                    safeParseDouble(contractDebt),
                    safeParseInt(errorCode),
                    safeParseDouble(hotcharge),
                    safeParseDouble(monthlyFee),
                    payer,
                    safeParseDouble(payment),
                    productCode,
                    safeParseDouble(remainPayment),
                    service,
                    status,
                    subId,
                    telFax
            );

        } catch (Exception e) {
            log.error("Lỗi khi parse XML: {}, error: {}", xml, e.getMessage(), e);
            return new FtthResponse();
        }
    }

//    private FtthResponse mapResponseExchangeRate(String xml) {
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
//
//            XPath xpath = XPathFactory.newInstance().newXPath();
//
//            XPathExpression exprReturn = xpath.compile("//*[local-name()='return']");
//
//            Node returnNode = (Node) exprReturn.evaluate(doc, XPathConstants.NODE);
//
//            if (returnNode == null) {
//                log.warn("Không tìm thấy thẻ <return> trong XML");
//                return new FtthResponse();
//            }
//
//            Element returnElement = (Element) returnNode;
//
//            String content        = getText(returnElement,"content");
//            String errorCode      = getText(returnElement,"errorCode");
//
//            return new FtthResponse(
//                    content,
//                    safeParseInt(errorCode)
//            );
//
//        } catch (Exception e) {
//            log.error("Lỗi khi parse XML: {}, error: {}", xml, e.getMessage(), e);
//            return new FtthResponse();
//        }
//    }

    private int safeParseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getText(Element parent, String tag) {
        try {
            Node node = parent.getElementsByTagName(tag).item(0);
            return node != null ? node.getTextContent().trim() : null;
        } catch (Exception e) {
            return null;
        }
    }
}

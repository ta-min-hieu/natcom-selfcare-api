package com.ringme.service.ringme;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.ringme.config.AppConfig;
import com.ringme.dao.mysql.selfcare.UserSurveyDao;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.selfcare.survey.*;
import com.ringme.model.selfcare.UserSurvey;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;


@Log4j2
@Service
public class SurveyService {
    @Autowired
    AppConfig config;
    @Autowired
    UserSurveyDao dao;

    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "text/xml";

    private HttpClient httpTransport;
    private static final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

    static {
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setConnectionTimeout(30000);
        params.setMaxTotalConnections(200);
        params.setDefaultMaxConnectionsPerHost(100);
        connectionManager.setParams(params);
    }

    public boolean checkUserSurvey(String isdn, String surveyFormId) {
        UserSurvey userSurvey = dao.findByIsdnAndSurveyFormOnWeek(isdn, surveyFormId);
        if(userSurvey == null)
            return true;

        return false;
    }

    public Response getSurvey(String isdn, String bussinessId, String channelSurvey, String serviceType, String serviceName) {
        SurveyDto surveyDto = callCreateSurveyForOther(isdn, bussinessId, channelSurvey, serviceType, serviceName);

        if (!"00".equals(surveyDto.getErrorCode()))
            return new Response(1, "create survey error");

        SurveyResponse.SurveyReturn res = getSurveyForm(isdn, surveyDto.getSurveyId(), surveyDto.getSurveyFormId());

//        return new Response(200, "Success", new SurveyForm(res));
        return new Response(200, "Success", res);
    }

    public SurveyDto callCreateSurveyForOther(String msisdn, String bussinessId, String channelSurvey, String serviceType, String serviceName) {
        String pre = msisdn + " |";
        log.info(pre + msisdn + ", bussinessId: " + bussinessId + ", channelSurvey: " + channelSurvey + ", serviceName: " + serviceName + ", serviceType: " + serviceType + "  ]]]]");
        String soapReq = "";
        String res = "-1";
        httpTransport = new HttpClient(SurveyService.connectionManager);
        String wsdl = config.getSurveyUrl();
        PostMethod post = new PostMethod(wsdl);
        SurveyDto rs = new SurveyDto("1", "1", "1", "1", "1");
        try {
            soapReq = buildCreateSurveyForOtherRequest(msisdn, bussinessId, "APP", "", "", "MyNatcom", serviceName);
            log.info(pre + " Soap message_request_X:wsdl= " + wsdl + "|" + soapReq + "|" + wsdl);
            RequestEntity entity = new StringRequestEntity(soapReq, TYPE, CHARSET);
            post.setRequestEntity(entity);
            httpTransport.executeMethod(post);
            String soapResponse = post.getResponseBodyAsString();
            log.info(pre + " Soap message response msisdn=" + msisdn + "|soapResponse=" + soapResponse);
            String returnTag = "errorCode";
            if (soapResponse != null && soapResponse.contains(returnTag)) {
                int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                res = soapResponse.substring(startReturn, endReturn).trim();
                rs.setErrorCode(res);
            }

            if ("00".equals(res)) {
                returnTag = "description";
                if (soapResponse.contains(returnTag)) {
                    int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                    int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                    res = soapResponse.substring(startReturn, endReturn).trim();
                    log.info(pre + "|description=" + res);
                    rs.setDescription(res);
                }

                returnTag = "surveyFormId";
                if (soapResponse != null && soapResponse.contains(returnTag)) {
                    int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                    int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                    res = soapResponse.substring(startReturn, endReturn).trim();
                    log.info(pre + "|surveyFormId=" + res);
                    rs.setSurveyFormId(res);
                    log.info(pre + "|getSurveyFormId=" +  rs.getSurveyFormId());
                }

                returnTag = "surveyId";
                if (soapResponse != null && soapResponse.contains(returnTag)) {
                    int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                    int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                    res = soapResponse.substring(startReturn, endReturn).trim();
                    log.info(pre + "|surveyId=" + res);
                    rs.setSurveyId(res);
                }

                returnTag = "surveyIsdnId";
                if (soapResponse != null && soapResponse.contains(returnTag)) {
                    int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                    int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                    res = soapResponse.substring(startReturn, endReturn).trim();
                    log.info(pre + "|surveyIsdnId=" + res);
                    rs.setSurveyIsdnId(res);
                }
            }

        } catch (Exception ex) {
            if (ex instanceof NoHttpResponseException || ex instanceof SocketTimeoutException) {
                log.info(pre);
            }
            httpTransport = null;
            log.info("Exception| {}", msisdn, ex);
        } finally {
            post.releaseConnection();

        }

        log.info(pre + "|result=" + res);
        return rs;
    }


    public SurveyResponse.SurveyReturn getSurveyForm(String msisdn, String surveyId, String surveyFormId) {
        log.info("[[[[getSurveyForm:" + msisdn + ", surveyId: " + surveyId + ", surveyFormId: " + surveyFormId);
        String soapReq = "";
        String res = "-1";
        httpTransport = new HttpClient(SurveyService.connectionManager);
        String wsdl = config.getSurveyUrl();
        PostMethod post = new PostMethod(wsdl);
        try {
            soapReq = buildGetSurveyFormForOtherRequest(msisdn, surveyId, surveyFormId);
            log.info(msisdn + "getSurveyForm Soap message_request_X:wsdl= " + wsdl + "|" + soapReq + "|" + wsdl);
            RequestEntity entity = new StringRequestEntity(soapReq, TYPE, CHARSET);
            post.setRequestEntity(entity);
            httpTransport.executeMethod(post);
            String soapResponse = post.getResponseBodyAsString();
            log.info("soapResponse: {}", soapResponse);
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SurveyResponse response = xmlMapper.readValue(soapResponse, SurveyResponse.class);


            log.info("getSurveyForm *******Soap message response msisdn=" + msisdn + "|Gson=" + new Gson().toJson(response));
//            return new SurveyResponse.SurveyReturn();
            return response.getBody().getResponse().getSurveyReturn();

        } catch (Exception ex) {
            if (ex instanceof NoHttpResponseException || ex instanceof SocketTimeoutException) {
                log.info("getSurveyForm|" + msisdn + "|");

            }
            httpTransport = null;
            log.info("getSurveyForm Exception|" + msisdn, ex);
        } finally {
            post.releaseConnection();
        }

        log.info("getSurveyForm msisdn|" + msisdn + "|result=" + res);
        return null;
    }


    public void updateSurveyCustomerAnswer(String msisdn, SurveyAnswerRequest surveyAnswerRequest) {
        String soapReq = "";
        String res = "-1";
        httpTransport = new HttpClient(SurveyService.connectionManager);
        String wsdl = config.getSurveyUrl();
        PostMethod post = new PostMethod(wsdl);
        SurveyDto rs = new SurveyDto("1", "1", "1", "1", "1");
        try {
            soapReq = buildUpdateSurveyCustomerAnswerRequest(msisdn, surveyAnswerRequest.getSurveyId(), surveyAnswerRequest.getSurveyFormId(), surveyAnswerRequest.getSectionAnswerDTOs());
            log.info(msisdn + "updateSurveyCustomerAnswer Soap message_request_X:wsdl= " + wsdl + "|" + soapReq + "|" + wsdl);
            RequestEntity entity = new StringRequestEntity(soapReq, TYPE, CHARSET);
            post.setRequestEntity(entity);
            httpTransport.executeMethod(post);
            String soapResponse = post.getResponseBodyAsString();
            log.info("getSurveyFormForOther Soap message response msisdn=" + msisdn + "|soapResponse=" + soapResponse);
            String returnTag = "errorCode";
            if (soapResponse != null && soapResponse.contains(returnTag)) {
                int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                res = soapResponse.substring(startReturn, endReturn).trim();
                rs.setErrorCode(res);

                if(rs.getErrorCode().equals("00"))
                    dao.store(new UserSurvey(msisdn, surveyAnswerRequest));
            }

            returnTag = "description";
            if (soapResponse != null && soapResponse.contains(returnTag)) {
                int startReturn = soapResponse.indexOf("<" + returnTag + ">") + returnTag.length() + 2;
                int endReturn = soapResponse.indexOf("</" + returnTag + ">");
                res = soapResponse.substring(startReturn, endReturn).trim();
                rs.setDescription(res);
            }
            return;
        } catch (Exception ex) {
            if (ex instanceof NoHttpResponseException || ex instanceof SocketTimeoutException)
                log.info("updateSurveyCustomerAnswer|" + msisdn + "|");

            httpTransport = null;
            log.info("updateSurveyCustomerAnswer Exception|" + msisdn, ex);
        } finally {
            post.releaseConnection();
        }
        log.info("updateSurveyCustomerAnswer msisdn|" + msisdn + "|result=" + res);
    }

    private String buildCreateSurveyForOtherRequest(String msisdn, String bussinessId, String channelSurvey, String startTime, String stopTime, String serviceType, String serviceName) {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.survey.bccs.viettel.com/\">\n" +
                "   <soapenv:Header>\n" +
                "      <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                "         <wsse:UsernameToken wsu:Id=\"UsernameToken-dc409006-449e-45af-8a71-9fcc192648a6\">\n" +
                "            <wsse:Username>admin</wsse:Username>\n" +
                "            <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">admin</wsse:Password>\n" +
                "         </wsse:UsernameToken>\n" +
                "      </wsse:Security>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:createSurveyForOther>\n" +
                "         <!--Zero or more repetitions:-->\n" +
                "         <isdn>" + msisdnSurvey(msisdn) + "</isdn>\n" +
                "         <surveyName>?</surveyName>\n" +
                "         <bussinessId>" + bussinessId + "</bussinessId>\n" +
                "         <channelSurvey>" + channelSurvey + "</channelSurvey>\n" +
                "         <!--Optional:-->\n" +
//                "         <startTime>20230711000000</startTime>\n" +
                "         <!--Optional:-->\n" +
//                "         <stopTime>20260711235959</stopTime>\n" +
                "         <!--Zero or more repetitions:-->\n" +
                "         <listSurveyAtt>\n" +
                "            <surveyAtt>SERVICE_TYPE</surveyAtt>\n" +
                "            <surveyAttValue>" + serviceType + "</surveyAttValue>\n" +
                "         </listSurveyAtt>\n" +
                "         <listSurveyAtt>\n" +
                "            <surveyAtt>SERVICE_NAME</surveyAtt>\n" +
                "            <surveyAttValue>" + serviceName + "</surveyAttValue>\n" +
                "         </listSurveyAtt>\n" +
                "      </ser:createSurveyForOther>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return xml;
    }


    private String buildGetSurveyFormForOtherRequest(String msisdn, String surveyId, String surveyFormId) {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.survey.bccs.viettel.com/\">\n" +
                "      <soapenv:Header>\n" +
                "      <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                "         <wsse:UsernameToken wsu:Id=\"UsernameToken-dc409006-449e-45af-8a71-9fcc192648a6\">\n" +
                "            <wsse:Username>admin</wsse:Username>\n" +
                "            <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">admin</wsse:Password>\n" +
                "         </wsse:UsernameToken>\n" +
                "      </wsse:Security>\n" +
                "   </soapenv:Header>\n" +
                "\n" +
                "   <soapenv:Body>\n" +
                "      <ser:getSurveyFormForOther>\n" +
                "         <surveyId>" + surveyId + "</surveyId>\n" +
                "         <surveyFormId>" + surveyFormId + "</surveyFormId>\n" +
                "         <isdn>" + msisdnSurvey(msisdn) + "</isdn>\n" +
                "      </ser:getSurveyFormForOther>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>\n";
        return xml;
    }


    private String buildUpdateSurveyCustomerAnswerRequest(String msisdn, String surveyId, String surveyFormId, List<SectionAnswerDTO> sectionAnswerDTOs) {

        // Khởi tạo StringBuilder để tạo XML
        StringBuilder requestXml = new StringBuilder();

        // Bắt đầu Envelope
        requestXml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.survey.bccs.viettel.com/\">\n")
                .append("   <soapenv:Header>\n")
                .append("      <wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n")
                .append("         <wsse:UsernameToken wsu:Id=\"UsernameToken-dc409006-449e-45af-8a71-9fcc192648a6\">\n")
                .append("            <wsse:Username>admin</wsse:Username>\n")
                .append("            <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">admin</wsse:Password>\n")
                .append("         </wsse:UsernameToken>\n")
                .append("      </wsse:Security>\n")
                .append("   </soapenv:Header>\n")
                .append("   <soapenv:Body>\n")
                .append("      <ser:updateSurveyCustomerAnswerForOther>\n")
                .append("         <surveyId>").append(surveyId).append("</surveyId>\n")
                .append("         <surveyFormId>").append(surveyFormId).append("</surveyFormId>\n")
                .append("         <isdn>").append(msisdnSurvey(msisdn)).append("</isdn>\n");

        // Lặp qua danh sách sectionAnswerDTOList và thêm các phần tử <sectionAnswerDTOs>
        for (SectionAnswerDTO dto : sectionAnswerDTOs) {
            requestXml.append("         <sectionAnswerDTOs>\n")
                    .append("            <answer>").append(dto.getAnswer()).append("</answer>\n")
                    .append("            <answerId>").append(dto.getAnswerId()).append("</answerId>\n")
                    .append("            <question>").append(dto.getQuestion()).append("</question>\n")
                    .append("            <questionId>").append(dto.getQuestionId()).append("</questionId>\n")
                    .append("            <type>").append(dto.getType()).append("</type>\n")
                    .append("         </sectionAnswerDTOs>\n");
        }

        // Kết thúc Body và Envelope
        requestXml.append("      </ser:updateSurveyCustomerAnswerForOther>\n")
                .append("   </soapenv:Body>\n")
                .append("</soapenv:Envelope>\n");


        return requestXml.toString();
    }


    public String msisdnSurvey(String msisdn) {
        String msisdnPref = "+509";
        if (msisdn.startsWith(msisdnPref)) {
            return msisdn.replace(msisdnPref, "");
        }
        return msisdn;
    }
}

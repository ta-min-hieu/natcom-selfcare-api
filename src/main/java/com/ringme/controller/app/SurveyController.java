package com.ringme.controller.app;

import com.google.gson.JsonObject;
import com.ringme.config.LocaleFactory;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.selfcare.survey.SurveyAnswerRequest;
import com.ringme.service.ringme.JwtService;
import com.ringme.service.ringme.SurveyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/survey")
public class SurveyController {
    @Autowired
    SurveyService service;
    @Autowired
    JwtService jwtService;
    @Autowired
    LocaleFactory localeFactory;

    @PostMapping("/check-user-survey")
    public ResponseEntity<?> checkUserSurvey(
            @RequestParam(value = "surveyFormId", required = false, defaultValue = "1001") String surveyFormId
    ) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("isdn: {}, surveyFormId: |{}|", isdn, surveyFormId);

        return ResponseEntity.ok(new Response(200, "Success", service.checkUserSurvey(isdn, surveyFormId)));
    }

    @PostMapping("/get-servey")
    public ResponseEntity<?> getSurvey(
            @RequestParam(value = "bussinessId", required = false, defaultValue = "1001") String bussinessId,
            @RequestParam(value = "channelSurvey", required = false, defaultValue = "APP") String channelSurvey,
            @RequestParam(value = "serviceType", required = false, defaultValue = "MyNatcom") String serviceType,
            @RequestParam(value = "serviceName", required = false, defaultValue = "Data Plus") String serviceName,
            @RequestParam("languageCode") String languageCode,
            @RequestParam("clientType") String clientType,
            @RequestParam("revision") String revision
    ) {
        String isdn = jwtService.getUsernameFromJwt();
        log.info("isdn: {}, bussinessId: {}, channelSurvey: {}, serviceType: {}, serviceName: {}, languageCode: {}, clientType: {}, revision: {}",
                isdn, bussinessId, channelSurvey, serviceType, serviceName, languageCode, clientType, revision);

        return ResponseEntity.ok(service.getSurvey(isdn, bussinessId, channelSurvey, serviceType, serviceName));
    }

    @RequestMapping(value = "/updateSurveyCustomerAnswer", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateSurveyCustomerAnswer(@RequestBody SurveyAnswerRequest surveyAnswerRequest) {

        JsonObject jsonObject = new JsonObject();
        long start = System.currentTimeMillis();
        String msisdn = jwtService.getUsernameFromJwt();
        log.info("msisdn: {}, surveyAnswerRequest: {}", msisdn, surveyAnswerRequest);

        try {
            service.updateSurveyCustomerAnswer(msisdn, surveyAnswerRequest);

            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("desc", localeFactory.getMessage("update-survey-success", surveyAnswerRequest.getLanguageCode()));

            log.info(log + "|executeTime|" + (System.currentTimeMillis() - start));
            return jsonObject.toString();
        } catch (Exception ex) {
            log.error(log + ex.getMessage(), ex);
            jsonObject.addProperty("code", 500);
            jsonObject.addProperty("desc", "Internal error");
            return jsonObject.toString();
        }
    }

//    @RequestMapping(value = "/createSurvey", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    public String createSurvey(@RequestParam(value = "bussinessId") String bussinessId,
//                               @RequestParam(value = "channelSurvey", required = false, defaultValue = "APP") String channelSurvey,
//                               @RequestParam(value = "serviceType", required = false, defaultValue = "MyNatcom") String serviceType,
//                               @RequestParam(value = "serviceName", required = false, defaultValue = "Data Plus") String serviceName,
//                               @RequestParam("languageCode") String languageCode,
//                               @RequestParam("clientType") String clientType,
//                               @RequestParam("revision") String revision) {
//        JsonObject jsonObject = new JsonObject();
//        String msisdn = jwtService.getUsernameFromJwt();
//
//        long start = System.currentTimeMillis();
//        log.info("msisdn " + msisdn + "|bussinessId " + bussinessId + "|channelSurvey" + channelSurvey + "|serviceType" + serviceType + "|serviceName" + serviceName+ "|languageCode" + languageCode+ "|clientType" + clientType+ "|revision" + revision);
//        try {
//            SurveyDto res = service.callCreateSurveyForOther(msisdn, bussinessId, channelSurvey, serviceType, serviceName);
//            if ("00".equals(res.getErrorCode())) {
//                jsonObject.addProperty("code", 200);
//                jsonObject.addProperty("desc", "Success");
//                jsonObject.add("data", new JsonParser().parse(new Gson().toJson(res)));
//            } else {
//                jsonObject.addProperty("code", 201);
//                jsonObject.addProperty("desc", res.getDescription());
//                jsonObject.add("data", new JsonParser().parse(new Gson().toJson(res)));
//            }
//
//            log.info(log + "|executeTime|" + (System.currentTimeMillis() - start));
//            return jsonObject.toString();
//        } catch (Exception ex) {
//            log.error(log + ex.getMessage(), ex);
//            jsonObject.addProperty("code", 500);
//            jsonObject.addProperty("desc", "Internal error");
//            return jsonObject.toString();
//        }
//    }
//
//    @RequestMapping(value = "/getSurveyForm", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    public String getSurveyForm(@RequestParam(value = "surveyId", required = false, defaultValue = "2623050") String surveyId,
//                                @RequestParam(value = "surveyFormId", required = false, defaultValue = "1001") String surveyFormId,
//                                @RequestParam("clientType") String clientType,
//                                @RequestParam("languageCode") String languageCode,
//                                @RequestParam("revision") String revision) {
//        JsonObject jsonObject = new JsonObject();
//        long start = System.currentTimeMillis();
//        String msisdn = jwtService.getUsernameFromJwt();
//        log.info("|surveyFormId" + surveyFormId + "|surveyId" + surveyId+ "|languageCode" + languageCode+ "|clientType" + clientType+ "|revision" + revision + "|msisdn: " + msisdn);
//
//        log.info("|surveyId " + surveyId + "|surveyFormId" + surveyFormId);
//        try {
//            SurveyResponse.SurveyReturn res = service.getSurveyForm(msisdn, surveyId, surveyFormId);
//            if (res == null) {
//                jsonObject.addProperty("code", 201);
//                jsonObject.addProperty("desc", "Fail");
//            } else {
//                if ("00".equals(res.getErrorCode())) {
//                    jsonObject.addProperty("code", 200);
//                    jsonObject.addProperty("desc", "Success");
//                    jsonObject.add("data", new JsonParser().parse(new Gson().toJson(res)));
//                } else {
//                    jsonObject.addProperty("code", 201);
//                    jsonObject.addProperty("desc", "Fail");
//                    jsonObject.add("data", new JsonParser().parse(new Gson().toJson(res)));
//                }
//            }
//
//            log.info(log + "|executeTime|" + (System.currentTimeMillis() - start));
//            return jsonObject.toString();
//        } catch (Exception ex) {
//            log.error(log + ex.getMessage(), ex);
//            jsonObject.addProperty("code", 500);
//            jsonObject.addProperty("desc", "Internal error");
//            return jsonObject.toString();
//        }
//    }
}

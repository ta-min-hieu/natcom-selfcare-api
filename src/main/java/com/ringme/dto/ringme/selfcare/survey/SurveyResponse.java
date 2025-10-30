package com.ringme.dto.ringme.selfcare.survey;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
public class SurveyResponse {

    @JacksonXmlProperty(localName = "Body")
    private Body body;

    @Data
    public static class Body {
        @JacksonXmlProperty(localName = "getSurveyFormForOtherResponse", namespace = "http://service.survey.bccs.viettel.com/")
        private GetSurveyFormForOtherResponse response;
    }

    @Data
    public static class GetSurveyFormForOtherResponse {
        @JacksonXmlProperty(localName = "return")
        private SurveyReturn surveyReturn;
    }

    @Data
    public static class SurveyReturn {
        private String errorCode;
        private String description;
        private String isdn;
        private String surveyId;
        private String surveyName;
        private String surveyFormId;
        private String surveyFormName;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "sectionDTOs")
        private List<SectionDTO> sectionDTOs;
    }

    @Data
    public static class SectionDTO {
        private String id;
        private String title;
        private String description;

        @JacksonXmlProperty(localName = "sectionNextId")
        private String sectionNextId;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "questionDTOs")
        private List<QuestionDTO> questionDTOs;
    }

    @Data
    public static class QuestionDTO {
        private String id;
        private String content;
        private String type;
        private String require;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "answerDTOs")
        private List<AnswerDTO> answerDTOs;
    }

    @Data
    public static class AnswerDTO {
        private String id;
        private String content;
        private String description;

        @JacksonXmlProperty(localName = "sectionNextId")
        private String sectionNextId;
    }
}

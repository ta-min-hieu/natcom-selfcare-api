package com.ringme.dto.ringme.selfcare.survey;

import lombok.Data;

import java.util.List;

@Data
public class SurveyAnswerRequest {
    private String surveyId = "1001";
    private String surveyFormId = "APP";
    private List<SectionAnswerDTO> sectionAnswerDTOs;
    private String languageCode;
    private String clientType;
    private String revision;
}

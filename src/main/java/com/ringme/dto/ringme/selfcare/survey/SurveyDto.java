package com.ringme.dto.ringme.selfcare.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDto {
    private String errorCode;
    private String description;
    private String surveyFormId;
    private String surveyId;
    private String surveyIsdnId;
}

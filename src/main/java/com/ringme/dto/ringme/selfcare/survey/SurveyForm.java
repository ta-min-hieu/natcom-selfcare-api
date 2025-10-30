package com.ringme.dto.ringme.selfcare.survey;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyForm {
    private String errorCode;
    private String description;
    private String isdn;
    private String surveyId;
    private String surveyName;
    private String surveyFormId;
    private String surveyFormName;
    private List<SurveyResponse.QuestionDTO> questionDTOs;

    public SurveyForm(SurveyResponse.SurveyReturn surveyReturn) {
        errorCode = surveyReturn.getErrorCode();
        description = surveyReturn.getDescription();
        isdn = surveyReturn.getIsdn();
        surveyId = surveyReturn.getSurveyId();
        surveyName = surveyReturn.getSurveyName();
        surveyFormId = surveyReturn.getSurveyFormId();
        surveyFormName = surveyReturn.getSurveyFormName();

        List<SurveyResponse.SectionDTO> sectionDTOs = surveyReturn.getSectionDTOs();
        if(sectionDTOs == null || sectionDTOs.isEmpty())
            return;

        questionDTOs = new ArrayList<>();

        for(SurveyResponse.SectionDTO sectionDTO : sectionDTOs) {
            List<SurveyResponse.QuestionDTO> questionDTOss = sectionDTO.getQuestionDTOs();
            if(questionDTOss == null || questionDTOss.isEmpty())
                continue;

            questionDTOs.addAll(questionDTOss);
        }
    }
}

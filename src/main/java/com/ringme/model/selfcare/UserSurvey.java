package com.ringme.model.selfcare;

import com.ringme.dto.ringme.selfcare.survey.SurveyAnswerRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserSurvey {
    private Long id;
    private String isdn;
    private String surveyId;
    private String surveyFormId;
    private Date createdAt;

    public UserSurvey(String isdn, SurveyAnswerRequest request) {
        this.isdn = isdn;
        surveyId = request.getSurveyId();
        surveyFormId = request.getSurveyFormId();
    }
}

package com.ringme.dto.ringme.selfcare.survey;

import lombok.Data;

@Data
public class SectionAnswerDTO {
    private String answer;
    private String answerId;
    private String question;
    private String questionId;
    private String type;
}

package com.ringme.model.loyalty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ringme.enums.loyalty.souvenir.SouvenirStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Souvenir implements Serializable {
    private Long id;
    private String title;
    private SouvenirStatus status;
    private Integer maxPoint;
    private String pointUnit;
    private String imageUrl;
    private String description;
    private Date startDate;
    private Date dateExpired;
    private Date createdAt;
    private Date updatedAt;
}

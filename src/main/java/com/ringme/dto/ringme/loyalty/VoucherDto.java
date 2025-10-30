package com.ringme.dto.ringme.loyalty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherDto implements Serializable {
    private Integer id;
    private String title;
    private int maxPoint;
    private String pointUnit = "points";
    private Date dateExpired;
    private String imageUrl;
    private String description;
    private Integer merchantId;
    private String merchantName;
}

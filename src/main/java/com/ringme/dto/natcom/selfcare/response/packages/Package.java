package com.ringme.dto.natcom.selfcare.response.packages;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Package {
    private String serviceId;
    private String name;
    private String code;
    private String shortDes;
    private String fullDes;
    private String iconUrl;
    private String price;
    private String unit;
    private Date registerDate;
    private List<SubPackage> subServices;
}

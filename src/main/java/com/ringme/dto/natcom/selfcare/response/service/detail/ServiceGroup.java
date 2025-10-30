package com.ringme.dto.natcom.selfcare.response.service.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ringme.dto.natcom.selfcare.response.packages.Package;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceGroup {
    private String groupName;
    private String groupCode;
    private List<Package> services;
}

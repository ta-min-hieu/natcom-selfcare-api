package com.ringme.dto.natcom.selfcare.response.service.detail;

import lombok.Data;

import java.util.List;

@Data
public class ServiceTeam {
    private String teamName;
    private List<ServiceGroup> services;
}

package com.ringme.dto.natcom.selfcare.response;

import lombok.Data;

@Data
public class SubAccountInfo {
    private String name;
    private double mainAcc;
    private double proAcc;
    private String dataPkgName;
    private int dataVolume;
}

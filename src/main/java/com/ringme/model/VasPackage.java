package com.ringme.model;

import lombok.Data;

@Data
public class VasPackage {
    private Integer id;
    private Integer groupId;
    private String name;
    private String description;
    private String iconUrl;
    private String serviceCode;
    private String channel;
}

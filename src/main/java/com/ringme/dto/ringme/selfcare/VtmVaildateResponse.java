package com.ringme.dto.ringme.selfcare;

import lombok.Data;

@Data
public class VtmVaildateResponse {
    private Integer code;
    private String desc;
    private Integer errorCode;
    private boolean result = false;
}

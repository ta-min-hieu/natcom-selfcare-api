package com.ringme.model.loyalty.voucher;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VoucherTopic implements Serializable {
    private Integer id;
    private String name;
    private String iconUrl;
}

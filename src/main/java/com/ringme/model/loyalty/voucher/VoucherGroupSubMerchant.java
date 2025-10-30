package com.ringme.model.loyalty.voucher;

import lombok.Data;

import java.io.Serializable;

@Data
public class VoucherGroupSubMerchant implements Serializable {
    private Long id;
    private Long idVoucherGroup;
    private Long idSubMerchant;
}

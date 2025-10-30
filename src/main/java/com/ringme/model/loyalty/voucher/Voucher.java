package com.ringme.model.loyalty.voucher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ringme.common.Helper;
import com.ringme.enums.loyalty.VoucherStatus;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Voucher implements Serializable {
    private Long id;
    private String code;
    private Long idVoucherGroup;
    private String voucherGroupName;
    private VoucherStatus status;
    private String isdn;
    private Date usedDate;
    private Long topicId;
    private String topicName;
    private Long idMerchant;
    private String merchantName;
    private Integer maxPoint;
    private String discountUnit;
    private String pointUnit;
    private Double discountAmount;
    private String description;
    private String imageUrl;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;

    // đổi voucher
    public Voucher(String isdn, VoucherGroup vg) {
        this.isdn = isdn;
        status = VoucherStatus.EXCHANGED;
        code = Helper.generateRandomString(10);
        idVoucherGroup = vg.getId();
        voucherGroupName = vg.getName();
        topicId = vg.getTopicId();
        topicName = vg.getTopicName();
        idMerchant = vg.getIdMerchant();
        merchantName = vg.getMerchantName();
        maxPoint = vg.getMaxPoint();
        discountUnit = vg.getDiscountUnit();
        pointUnit = vg.getPointUnit();
        discountAmount = vg.getDiscountAmount();
        description = vg.getDescription();
        imageUrl = vg.getImageUrl();
        startDate = vg.getStartDate();
        endDate = vg.getEndDate();
    }
}

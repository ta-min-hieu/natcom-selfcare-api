package com.ringme.dto.ringme.loyalty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ringme.enums.loyalty.gift.MyGiftType;
import com.ringme.enums.loyalty.souvenir.SouvenirOrderStatus;
import com.ringme.model.loyalty.SouvenirOrder;
import com.ringme.model.loyalty.voucher.Voucher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class MyGift {
    private Long id;
    private String showroomId;
    private String showroomName;
    private String orderCode;
    private String isdn;
    private Date startDate;
    private Date dateExpired;
    private String description;
    private String title;
    private Date createdAt;
    private Long idVoucherGroup;
    private Date usedDate;
    private Long topicId;
    private String topicName;
    private Long idMerchant;
    private String merchantName;
    private Integer maxPoint;
    private String discountUnit;
    private String pointUnit;
    private Double discountAmount;
    private String imageUrl;
    private String myGiftType;
    private SouvenirOrderStatus status;
    private String provinceId;
    private String provinceName;
    private String districtId;
    private String districtName;
    private String address;
    private String openTime;
    private Double longitude;
    private Double latitude;
    private Date updateAt;

    public MyGift(Voucher voucher) {
        id = voucher.getId();
        title = voucher.getVoucherGroupName();
        maxPoint = voucher.getMaxPoint();
        pointUnit = voucher.getPointUnit();
        startDate = voucher.getStartDate();
        description = voucher.getDescription();
        imageUrl = voucher.getImageUrl();
        idMerchant = voucher.getIdMerchant();
        merchantName = voucher.getMerchantName();
        usedDate = voucher.getUsedDate();
        dateExpired = voucher.getEndDate();
        discountAmount = voucher.getDiscountAmount();
        discountUnit = voucher.getDiscountUnit();
        status = SouvenirOrderStatus.VALID;
        myGiftType = MyGiftType.VOUCHER.getType();
    }

    public MyGift(SouvenirOrder souvenirOrder) {
        orderCode = souvenirOrder.getOrderCode();
        isdn = souvenirOrder.getIsdn();
        title = souvenirOrder.getTitle();
        maxPoint = souvenirOrder.getPoint();
        pointUnit = souvenirOrder.getUnit();
        dateExpired = souvenirOrder.getDateExpired();
        imageUrl = souvenirOrder.getImageUrl();
        description = souvenirOrder.getDescription();
        showroomId = souvenirOrder.getShowroomId();
        showroomName = souvenirOrder.getShowroomName();
        myGiftType = MyGiftType.SOUVENIR.getType();
        status = souvenirOrder.getStatus();
        provinceId = String.valueOf(souvenirOrder.getProvinceId());
        provinceName = souvenirOrder.getProvinceName();
        districtId = String.valueOf(souvenirOrder.getDistrictId());
        districtName = souvenirOrder.getDistrictName();
        address = souvenirOrder.getAddress();
    }
}

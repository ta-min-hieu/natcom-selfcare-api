package com.ringme.model.loyalty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ringme.common.Helper;
import com.ringme.enums.loyalty.souvenir.SouvenirOrderStatus;
import com.ringme.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SouvenirOrder implements Serializable {
    private Long id;
    private Long souvenirId;
    private String showroomId;
    private String showroomName;
    private String orderCode;
    private String isdn;
    private SouvenirOrderStatus status;
    private Date startDate;
    private Date dateExpired;
    private String imageUrl;
    private String description;
    private String title;
    private Integer point;
    private String unit;
    private Date createdAt;
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private String address;
    private String openTime;
    private BigDecimal longitude;
    private BigDecimal latitude;

    public SouvenirOrder(Souvenir souvenir, Shop shop, String isdn, SouvenirOrderStatus status) {
        if(shop != null) {
            showroomId = shop.getId();
            showroomName = shop.getName();
            provinceId = shop.getProvinceId();
            provinceName = shop.getProvinceName();
            districtId = shop.getDistrictId();
            districtName = shop.getDistrictName();
            address = shop.getAddr();
            openTime = shop.getOpenTime();
            latitude = shop.getLatitude();
            longitude = shop.getLongitude();
        }
        orderCode = Helper.generateRandomString(10);
        this.isdn = isdn;
        this.status = status;
        if(souvenir != null) {
            souvenirId = souvenir.getId();
            dateExpired = souvenir.getDateExpired();
            imageUrl = souvenir.getImageUrl();
            description = souvenir.getDescription();
            title = souvenir.getTitle();
            point = souvenir.getMaxPoint();
            unit = souvenir.getPointUnit();
        }
    }
}

package com.ringme.service.ringme;

import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.StoreInfo;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.loyalty.LoyaltyCustomerInfo;
import com.ringme.dto.ringme.loyalty.MyGift;
import com.ringme.dto.ringme.loyalty.VoucherDto;
import com.ringme.enums.loyalty.gift.MyGiftStatus;
import com.ringme.model.loyalty.Souvenir;
import com.ringme.model.loyalty.SouvenirOrder;
import com.ringme.model.loyalty.voucher.VoucherTopic;

import java.util.List;

public interface RingmeLoyaltyService {
    LoyaltyCustomerInfo getCustomerInfo(String isdn, String vtAccId);

    List<VoucherTopic> getVoucherTopics();

    Response getVoucherGroupsByTopicId(String language, int topicId);

    Response getVoucherGroupsById(long topicId);

    List<Souvenir> getSouvenirs();

    Souvenir getSouvenirById(long id);

    List<MyGift> getMyGifts(String isdn, MyGiftStatus status);

    Response exchangeSouvenir(String language, String isdn, String vtAccId, long souvenirReviewId, String shopId);

    Response exchangeVoucher(String language, String isdn, String vtAccId, long voucherGroupId, int topicId);

    SelfcareResponse<List<StoreInfo>> findStoreHasGiftByAddr(String longitude, String latitude, String provinceId, String districtId);

    void clearCacheMyGift(String isdn);
}

package com.ringme.service.ringme;

import com.ringme.config.LocaleFactory;
import com.ringme.dao.mysql.ShopDao;
import com.ringme.dao.mysql.loyalty.*;
import com.ringme.dto.natcom.loyalty.AccountPointDto;
import com.ringme.dto.natcom.loyalty.LoyaltyResponse;
import com.ringme.dto.natcom.loyalty.ViettelAccountDTO;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.StoreInfo;
import com.ringme.dto.record.Response;
import com.ringme.dto.ringme.loyalty.LoyaltyCustomerInfo;
import com.ringme.dto.ringme.loyalty.MyGift;
import com.ringme.dto.ringme.loyalty.VoucherDto;
import com.ringme.enums.loyalty.IsSendSMS;
import com.ringme.enums.loyalty.PointTypeId;
import com.ringme.enums.loyalty.gift.MyGiftStatus;
import com.ringme.enums.loyalty.gift.TransTypeId;
import com.ringme.enums.loyalty.souvenir.SouvenirOrderStatus;
import com.ringme.model.Shop;
import com.ringme.model.loyalty.SouvenirOrder;
import com.ringme.model.loyalty.Souvenir;
import com.ringme.model.loyalty.voucher.Voucher;
import com.ringme.model.loyalty.voucher.VoucherGroup;
import com.ringme.model.loyalty.voucher.VoucherTopic;
import com.ringme.service.natcom.NatcomLoyaltyService;
import com.ringme.service.natcom.NatcomSelfcareService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class RingmeLoyaltyServiceImpl implements RingmeLoyaltyService {
    @Autowired
    NatcomLoyaltyService natcomLoyaltyService;
    @Autowired
    NatcomSelfcareService natcomSelfcareService;
    @Autowired
    SouvenirDao souvenirDao;
    @Autowired
    SouvenirOrderDao souvenirOrderDao;
    @Autowired
    ShopDao shopDao;
    @Autowired
    VoucherTopicDao voucherTopicDao;
    @Autowired
    VoucherGroupDao voucherGroupDao;
    @Autowired
    LocaleFactory localeFactory;
    @Autowired
    VoucherDao voucherDao;
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoyaltyCustomerInfo getCustomerInfo(String isdn, String vtAccId) {
        LoyaltyResponse<List<ViettelAccountDTO>> viettelAccountR = natcomLoyaltyService.getCustomerInfo(isdn, null, null, null, null);

        LoyaltyResponse<Object> consumptionR = natcomLoyaltyService.getTotalPoint(vtAccId, PointTypeId.CONSUMPTION);
        LoyaltyResponse<Object> rankingR = natcomLoyaltyService.getTotalPoint(vtAccId, PointTypeId.RANKING);
        LoyaltyResponse<List<AccountPointDto>> accountPointsR = natcomLoyaltyService.getAccountPointInfo(isdn, null, null, null, IsSendSMS.NOT_SEND);

        return new LoyaltyCustomerInfo(viettelAccountR, consumptionR, rankingR, accountPointsR);
    }

    @Override
    public List<VoucherTopic> getVoucherTopics() {
        return voucherTopicDao.getVoucherTopics();
    }

    @Override
    public Response getVoucherGroupsByTopicId(String language, int topicId) {
        List<VoucherDto> list = voucherGroupDao.getVouchersGroupByTopicId(topicId);
        if(list == null || list.isEmpty())
            return new Response(404, localeFactory.getMessage("message.voucher.empty", language));

        return new Response(200, "Success", list);
    }

    @Override
    public Response getVoucherGroupsById(long id) {
        return new Response(200, "Success", voucherGroupDao.getVouchersGroupById(id));
    }

    @Override
    public List<MyGift> getMyGifts(String isdn, MyGiftStatus status) {
        List<MyGift> myGifts = new ArrayList<>();


        List<MyGift> souvenirMyGift = souvenirOrderDao.getMySouvenirByIsdnAndStatus(isdn, status.getType());
        if(souvenirMyGift != null)
            myGifts.addAll(souvenirMyGift);

        List<MyGift> voucherMyGift = voucherDao.getMyVoucherByIsdnAndStatus(isdn, status.getType());
        if(voucherMyGift != null)
            myGifts.addAll(voucherMyGift);

        myGifts.sort(Comparator.comparing(MyGift::getUpdateAt).reversed());

        return myGifts;
    }

    @Override
    public List<Souvenir> getSouvenirs() {
        return souvenirDao.getSouvenirs();
    }

    @Override
    public Souvenir getSouvenirById(long id) {
        return souvenirDao.getSouvenirById(id);
    }

    @Override
    public Response exchangeSouvenir(String language, String isdn, String vtAccId, long souvenirId, String shopId) {
        Shop shop = null;
        Souvenir souvenir = null;
        LoyaltyCustomerInfo customerInfo = null;

        try {
            shop = shopDao.getShopById(shopId);
            if (shop == null) {
                log.error("Shop invalid| isdn: {}, souvenirReviewId: {}, shopId: {}", isdn, souvenirId, shopId);
                return new Response(400, localeFactory.getMessage("showroom-invalid", language));
            }

            souvenir = souvenirDao.getSouvenirByIdAndShowroomId(souvenirId);
            if (souvenir == null) {
                log.error("Souvenir invalid| isdn: {}, souvenirReviewId: {}, shopId: {}", isdn, souvenirId, shopId);
                return new Response(400, localeFactory.getMessage("souvenir-invalid", language));
            }

            customerInfo = getCustomerInfo(isdn, vtAccId);
            if (customerInfo == null || customerInfo.getTotalUsePoint() == null || customerInfo.getTotalUsePoint() <= 0 || customerInfo.getTotalUsePoint() < souvenir.getMaxPoint()) {
                log.error("User's souvenir loyalty point invalid| isdn: {}, souvenirReviewId: {}, shopId: {},\nCustomerInfo: {}\nreview: {}", isdn, souvenirId, shopId, customerInfo, souvenir);
                return new Response(400, localeFactory.getMessage("user-souvenir-loyalty-invalid", language));
            }

            if(!redeemPoint(isdn, souvenir.getMaxPoint(), TransTypeId.SOUVENIR)) {
                log.error("Change loyalty fail| isdn: {}, souvenirReviewId: {}, shopId: {}", isdn, souvenirId, shopId);
                return new Response(400, localeFactory.getMessage("change-loyalty-fail", language));
            }

            SouvenirOrder souvenirOrder = new SouvenirOrder(souvenir, shop, isdn, SouvenirOrderStatus.PENDING);
            if(!souvenirOrderDao.store(souvenirOrder)) {
                log.error("Fail to store souvenir| isdn: {}, shopId: {}, souvenirCode: {}", isdn, souvenirId, shopId);
                // Hoàn điểm
                if(!adjustAccountPoint(isdn, souvenir.getMaxPoint(), TransTypeId.SOUVENIR))
                    log.error("Hoàn điểm is fail| isdn: {}, shopId: {}, souvenirCode: {}", isdn, souvenirId, shopId);
                else
                    log.info("Hoàn điểm success| isdn: {}, shopId: {}, souvenirCode: {}", isdn, souvenirId, shopId);

                return new Response(400, localeFactory.getMessage("souvenir-invalid", language));
            }

            souvenirDao.updateQuantityExchangedById(souvenirId);
            clearCacheMyGift(isdn);
            souvenirDao.clearCacheSouvenirById(souvenirId);
            souvenirDao.clearCacheSouvenirByIdAndShowroomId(souvenirId);
            log.info("Success: isdn: {}, souvenirReviewId: {}, shopId: {}", isdn, souvenirId, shopId);
            return new Response(200, souvenirOrder.getOrderCode(), new MyGift(souvenirOrder));
        } catch (Exception e) {
            log.error("System error| isdn: {}, souvenirReviewId: {}, shopId: {}\nshop:{}\n\nCustomerInfo:{}, error: {}", isdn, souvenirId, shopId, shop, customerInfo, e.getMessage(), e);
        }
        return new Response(500, "System error");
    }

    @Override
    public Response exchangeVoucher(String language, String isdn, String vtAccId, long voucherGroupId, int topicId) {
        VoucherGroup voucherGroup = null;
        LoyaltyCustomerInfo customerInfo = null;

        try {
            voucherGroup = voucherGroupDao.getVoucherGroupById(voucherGroupId);
            if (voucherGroup == null) {
                log.error("Voucher group invalid| isdn: {}, vtAccId: {}, voucherGroupId: {}", isdn, vtAccId, voucherGroupId);
                return new Response(400, localeFactory.getMessage("voucher-invalid", language));
            }

            customerInfo = getCustomerInfo(isdn, vtAccId);
            if (customerInfo == null || customerInfo.getTotalUsePoint() == null || customerInfo.getTotalUsePoint() <= 0 || customerInfo.getTotalUsePoint() < voucherGroup.getMaxPoint()) {
                log.error("User's voucher loyalty point invalid| isdn: {}, vtAccId: {}, voucherGroupId: {},\nCustomerInfo: {}\nvoucherGroup: {}", isdn, vtAccId, voucherGroupId, customerInfo, voucherGroup);
                return new Response(400, localeFactory.getMessage("user-voucher-loyalty-invalid", language));
            }

            if(!redeemPoint(isdn, voucherGroup.getMaxPoint(), TransTypeId.VOUCHER)) {
                log.error("Change loyalty fail| isdn: {}, vtAccId: {}, voucherGroupId: {},\nCustomerInfo: {}\nvoucherGroup: {}", isdn, vtAccId, voucherGroupId, customerInfo, voucherGroup);
                return new Response(400, localeFactory.getMessage("change-loyalty-fail", language));
            }

            Voucher voucher = new Voucher(isdn, voucherGroup);
            if(!voucherDao.store(voucher)) {
                log.error("Fail to store exchange voucher| isdn: {}, vtAccId: {}, voucherGroupId: {},\nCustomerInfo: {}\nvoucherGroup: {}", isdn, vtAccId, voucherGroupId, customerInfo, voucherGroup);
                // Hoàn điểm
                if(!adjustAccountPoint(isdn, voucherGroup.getMaxPoint(), TransTypeId.VOUCHER))
                    log.error("Hoàn điểm is fail| isdn: {}, vtAccId: {}, voucherGroupId: {},\nCustomerInfo: {}\nvoucherGroup: {}", isdn, vtAccId, voucherGroupId, customerInfo, voucherGroup);
                else
                    log.info("Hoàn điểm success| isdn: {}, vtAccId: {}, voucherGroupId: {},\nCustomerInfo: {}\nvoucherGroup: {}", isdn, vtAccId, voucherGroupId, customerInfo, voucherGroup);
                return new Response(400, localeFactory.getMessage("fail-to-store-exchange-voucher", language));
            }

            if(!voucherGroupDao.updateQuantityExchangedById(voucherGroupId))
                log.error("Tăng lượt nhận voucher fail| id: {}", voucherGroupId);

            clearCacheMyGift(isdn);
            voucherGroupDao.clearCacheVoucherGroupById(voucherGroupId);
            log.info("Success: isdn: {}, vtAccId: {}, voucherGroupId: {}, topicId {}", isdn, vtAccId, voucherGroupId, topicId);
            return new Response(200, voucher.getCode(), new MyGift(voucher));
        } catch (Exception e) {
            log.error("System error| isdn: {}, vtAccId: {}, voucherGroupId: {}\nCustomerInfo:{}\nVoucherGroup: {}, error: {}", isdn, vtAccId, voucherGroupId, customerInfo, voucherGroup, e.getMessage(), e);
        }
        return new Response(500, "System error");
    }

    @Override
    public SelfcareResponse<List<StoreInfo>> findStoreHasGiftByAddr(String longitude, String latitude, String provinceId, String districtId) {
        return natcomSelfcareService.wsFindStoreByAddr(longitude, latitude, provinceId, districtId);
    }

    @Override
    public void clearCacheMyGift(String isdn) {
        Set<String> keys = redisTemplate.keys("rc10m::MySouvenir:" + isdn + "*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);

        Set<String> keys2 = redisTemplate.keys("rc10m::MyVoucher:" + isdn + "*");
        if (keys2 != null && !keys2.isEmpty())
            redisTemplate.delete(keys2);
    }

    private boolean adjustAccountPoint(String isdn, long pointAmount, TransTypeId transTypeId) {
        LoyaltyResponse<Object> response = null;
        try {
            response = natcomLoyaltyService.adjustAccountPoint(isdn, null, null, null, pointAmount, PointTypeId.CONSUMPTION, transTypeId, null, null);
            return response != null && response.getCode() != null && response.getCode().equals("000");
        } catch (Exception e) {
            log.error("isdn: {}, pointAmount: {}, transTypeId: {},\nreponse: {}, error: {}",  isdn, pointAmount, transTypeId, response, e.getMessage(), e);
        }

        return false;
    }

    private boolean redeemPoint(String isdn, long pointAmount, TransTypeId transTypeId) {
        LoyaltyResponse<Object> response = null;

        try {
            response = natcomLoyaltyService.redeemPoint(isdn, pointAmount, transTypeId);
            return response != null && response.getCode() != null && response.getCode().equals("000");
        } catch (Exception e) {
            log.error("isdn: {}, pointAmount: {}, transTypeId: {},\nreponse: {}, error: {}",  isdn, pointAmount, transTypeId, response, e.getMessage(), e);
        }

        return false;
    }
}

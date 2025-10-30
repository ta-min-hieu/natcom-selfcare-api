package com.ringme.service.natcom;

import com.ringme.dto.natcom.loyalty.*;
import com.ringme.enums.loyalty.IsSendSMS;
import com.ringme.enums.loyalty.LoyaltyStatus;
import com.ringme.enums.loyalty.PointTransactionType;
import com.ringme.enums.loyalty.PointTypeId;
import com.ringme.enums.loyalty.gift.TransTypeId;
import com.ringme.enums.loyalty.gift.*;

import java.time.LocalDate;
import java.util.List;

public sealed interface NatcomLoyaltyService permits NatcomLoyaltyServiceImpl {
    // cần 1 trong các param
    LoyaltyResponse<List<ViettelAccountDTO>> getCustomerInfo(String isdn, String subId, String idNo, String custId, String vtAccountId);
    // cần 1 trong các param
    LoyaltyResponse<List<PointTransferHistoryDTO>> getPointTransferHistory(String isdn, String subId, String idNo, String custId, LocalDate fromDate, LocalDate toDate, Long offset, Long limit, PointTransactionType type, PointTypeId pointId);
    //không bắt buộc
    LoyaltyResponse<List<RankDefineDTO>> getRankDefineInfo(String tenant);
    // cần 1 trong các param
    LoyaltyResponse<List<GiftDTO>> getGiftInfo(GiftId giftId, GiftCode giftCode, Long giftType, GiftName giftName, Long point, LoyaltyStatus status);
    // 1 trong 4 param đầu là bắt buộc
    LoyaltyResponse<List<AccountPointDto>> getAccountPointInfo(String isdn, String subId, String idNo, String custId, IsSendSMS isSendSms);
    // 1 trong 4 param đầu là bắt buộc
//    LoyaltyResponse<Object> getTotalAccountPoint(String isdn, String subId, String idNo, String custId, IsSendSMS isSendSms, Long numDay);

    LoyaltyResponse<Object> getTotalPoint(String vtAccId, PointTypeId pointTypeId);
    // cần 1 trong các param
    LoyaltyResponse<AccountRankDTO> getAccountRankInfo(String isdn, String subId, String idNo, String custId);
    // cần 1 trong 5 param đầu
    LoyaltyResponse<Object> redeemPoint(String isdn, long pointAmount, TransTypeId typeId);
    // cần 1 trong 4 param đầu
    LoyaltyResponse<Object> adjustAccountPoint(String isdn, String subId, String custId, Long vtAccId, Long pointAmount, PointTypeId pointId, TransTypeId transTypeId, Long userSystem, String description);

    LoyaltyResponse<List<AccountRankHistoryDTO>> accountRankHis(Long vtAccId, LocalDate fromDate, LocalDate toDate);
    // cần 1 trong 3 param đầu
    LoyaltyResponse<List<SumPointDTO>> getListPointRankByMonth(Long vtAccId, String productId, String productName, LocalDate fromDate, LocalDate toDate);
    // cần 1 trong 3 param đầu
    LoyaltyResponse<List<SumPointDTO>> getListPointProductByMonth(Long vtAccId, String productId, String productName, LocalDate fromDate, LocalDate toDate);

    LoyaltyResponse<Object> redeemPointLotteryCode(String productName);
}

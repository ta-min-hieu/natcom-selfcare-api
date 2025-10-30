package com.ringme.dto.ringme.loyalty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ringme.dto.natcom.loyalty.AccountPointDto;
import com.ringme.dto.natcom.loyalty.LoyaltyResponse;
import com.ringme.dto.natcom.loyalty.ViettelAccountDTO;
import com.ringme.enums.loyalty.LoyaltyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyCustomerInfo {
    private String accountName;
    private LoyaltyStatus status;
    private String idNo;
    private Long tenant;
    private Long accountRankId;
    private Long rankId;
    private String rankName;

    private Long vtAccId;
    private Double totalRankPoint = 0d;
    private Double totalUsePoint = 0d;
    private Double totalUsePointExp = 0d;

    private ZonedDateTime pointExpireDate;

    public LoyaltyCustomerInfo(LoyaltyResponse<List<ViettelAccountDTO>> viettelAccountR, LoyaltyResponse<Object> consumptionR, LoyaltyResponse<Object> rankingR, LoyaltyResponse<List<AccountPointDto>> accountPointsR) {
        try {
            if (viettelAccountR != null) {
                List<ViettelAccountDTO> list = viettelAccountR.getData(new TypeReference<List<ViettelAccountDTO>>() {});
                if (list != null && !list.isEmpty()) {

                    ViettelAccountDTO viettelAccountDTO = list.getFirst();
                    accountName = viettelAccountDTO.getAccountName();
                    status = viettelAccountDTO.getStatus();
                    idNo = viettelAccountDTO.getIdNo();
                    tenant = viettelAccountDTO.getTenant();
                    accountRankId = viettelAccountDTO.getAccountRankId();
                    rankId = viettelAccountDTO.getRankId();
                    rankName = viettelAccountDTO.getRankName();
                    vtAccId = viettelAccountDTO.getVtAccId();
                }
            }

            if (consumptionR != null) {
                Double consumption = consumptionR.getData(new TypeReference<Double>() {});
                if(consumption != null)
                    totalUsePoint = consumption;
            }

            if (rankingR != null) {
                Double ranking = rankingR.getData(new TypeReference<Double>() {});
                if(ranking != null)
                    totalRankPoint = ranking;
            }

            if(accountPointsR != null) {
                List<AccountPointDto> list = accountPointsR.getData(new TypeReference<List<AccountPointDto>>() {});
                List<AccountPointDto> filteredList = list.stream()
                        .filter(p -> Integer.valueOf(2).equals(p.getPointType()))
                        .toList();

                Optional<ZonedDateTime> minExpireDate = filteredList.stream()
                        .map(AccountPointDto::getPointExpireDate)
                        .min(ZonedDateTime::compareTo);

                pointExpireDate = minExpireDate.orElse(null);

                minExpireDate.ifPresent(minDate -> totalUsePointExp = filteredList.stream()
                        .filter(p -> minDate.equals(p.getPointExpireDate()))
                        .map(AccountPointDto::getPointValue)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum());
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }
}

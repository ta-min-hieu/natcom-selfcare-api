package com.ringme.dto.ringme.selfcare;

import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.natcom.selfcare.response.accounts.detail.AccountsDetail;
import com.ringme.dto.natcom.selfcare.response.accounts.detail.ValueItem;
import lombok.Data;

import java.util.List;

@Data
public class AccountInfoDetail {
    private String fullName;
    private String isdn;
    private String blockOneWayDate;
    private String blockTwoWayDate;
    private String status;
    private String deleteDate;

    List<ValueItem> accounts;

    public AccountInfoDetail(SelfcareResponse<List<AccountsDetail>> accountsDetails, String name) {
        fullName = name;

        if(accountsDetails == null)
            return;

        List<AccountsDetail> list = accountsDetails.getWsResponse();
        if(list == null || list.isEmpty())
            return;

        for(int i = 0; i < list.size(); i++) {
            AccountsDetail a = list.get(i);
            if(a == null)
                continue;

            if(i == 0) {
                List<ValueItem> valueItems = a.getValues();
                isdn = valueItems.getFirst().getValue();
                status = valueItems.get(1).getValue();
                blockOneWayDate = valueItems.get(2).getValue();
                blockTwoWayDate = valueItems.get(3).getValue();
                deleteDate = valueItems.get(4).getValue();
            } else if (i == 1) {
                accounts = a.getValues();

                if(accounts != null)
                    for (ValueItem item : accounts)
                        if (item.getExp() != null)
                            item.setExp(item.getExp().replace("expired date: ", ""));
            }
        }
    }
}

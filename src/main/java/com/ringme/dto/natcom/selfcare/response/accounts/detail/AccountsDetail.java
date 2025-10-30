package com.ringme.dto.natcom.selfcare.response.accounts.detail;

import lombok.Data;

import java.util.List;

@Data
public class AccountsDetail {
    private String title;
    private List<ValueItem> values;
}

package com.ringme.dto.ringme.ftth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ringme.dto.natcom.ftth.FtthResponse;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewAccount {
    private String ftthAccount;
    private String contractNo;
    private String infoName;
    private String infoDescriptionName;
    private String infoDescriptionValue;
    private Double infoAmount;
    private String packageName;
    private String paymentType;
    private String numberOfMonthsOfUse;
    private Double openingDebt;
    private Double monthlyFee;
    private Double paidAmount;
    private Double hotCharge;
    private Double prePaid;
    private Double totalPrice;
    private Double paymentAmount;
    private Double paymentAmountHotCharge;
    private Double rate;
    private String fullName;
    private Double priorDebit;
    private Double beforeDebit;
    private Double ariseDebit;
    private Double toPay;

    private List<String> currencies = List.of("USD", "HTG");

    // common
    public ViewAccount (String ftthAccount, FtthResponse v, double exchangeRate, PaymentFtthInfo p) {
        this.ftthAccount = ftthAccount;
        contractNo = v.getContractNo();
        packageName = v.getProductCode();
        openingDebt = v.getContractDebt();
        monthlyFee = v.getMonthlyFee();
        paidAmount = v.getPayment();
        prePaid = v.getRemainPayment();
        rate = exchangeRate;
        hotCharge = v.getHotcharge();
        fullName = p.getUserUsing();
        priorDebit = p.getPriorDebit();
        ariseDebit = p.getAriseDebit();
        beforeDebit = priorDebit - ariseDebit;
        toPay = p.getToPay();
    }

    public Double getPaymentAmount() {
        if(paymentAmount == null || paymentAmount < 0)
            return 0.0;

        return paymentAmount;
    }

    public Double getPaymentAmountHotCharge() {
        if(paymentAmountHotCharge == null || paymentAmountHotCharge < 0)
            return 0.0;

        return paymentAmountHotCharge;
    }
}

package com.ringme.dao.mysql.selfcare;

import com.ringme.dto.natcom.natcash.response.PaymentStatusData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class NatcashMerchantPaymentHistoryDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(PaymentStatusData obj, String requestId) {
        String SQL = """
                INSERT INTO natcash_merchant_payment_history (
                    amount, request_id, order_number, response_code, to_phone, trans_id
                ) VALUES (
                    :amount, :request_id, :orderNumber, :responseCode, :toPhone, :transId
                )
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("amount", obj.getAmount())
                    .addValue("request_id", requestId)
                    .addValue("orderNumber", obj.getOrderNumber())
                    .addValue("responseCode", obj.getResponseCode())
                    .addValue("toPhone", obj.getToPhone())
                    .addValue("transId", obj.getTransId()));
            if (rs > 0) {
                log.info("Store NatcashMerchantPaymentHistory successfully!: {}", obj);
                return true;
            }
        } catch (Exception e) {
            log.error("obj: {}, Error: {}", obj, e.getMessage(), e);
        }
        return false;
    }
}

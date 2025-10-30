package com.ringme.dao.mysql.selfcare;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Log4j2
@Repository
public class NatcashMerchantRefundHistoryDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(Integer refundStatus, String requestId, String orderNumber, String toPhone, String transaction, BigDecimal amount) {
        String SQL = """
                INSERT INTO natcash_merchant_refund_history (
                     status, request_id, order_number, to_phone, transaction, amount
                 ) VALUES (
                     :status, :request_id, :orderNumber, :toPhone, :transaction, :amount
                 )
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("status", refundStatus)
                    .addValue("request_id", requestId)
                    .addValue("orderNumber", orderNumber)
                    .addValue("toPhone", toPhone)
                    .addValue("transaction", transaction)
                    .addValue("amount", amount));
            if (rs > 0) {
                log.info("Store NatcashMerchantRefundHistory successfully!: refundStatus: {}, orderNumber: {}, toPhone: {}, transaction: {}, amount: {}",
                        refundStatus, orderNumber, toPhone, transaction, amount);
                return true;
            }
        } catch (Exception e) {
            log.error("refundStatus: {}, orderNumber: {}, toPhone: {}, transaction: {}, amount: {}, Error: {}", refundStatus, orderNumber, toPhone, transaction, amount, e.getMessage(), e);
        }
        return false;
    }
}

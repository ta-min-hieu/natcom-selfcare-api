package com.ringme.dao.mysql.selfcare;

import com.ringme.model.selfcare.TopupAirtimeHistory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class TopupAirtimeHistoryDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(TopupAirtimeHistory obj) {
        String SQL = """
                INSERT INTO topup_airtime_history (
                     request_id, amount, isdn, status,
                     transaction_id, error_code, content, description, order_number
                 ) VALUES (
                     :requestId, :amount, :isdn, :status,
                     :transactionId, :errorCode, :content, :description, :orderNumber
                 )
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("requestId", obj.getRequestId())
                    .addValue("amount", obj.getAmount())
                    .addValue("isdn", obj.getIsdn())
                    .addValue("status", obj.getStatus())
                    .addValue("transactionId", obj.getTransactionId())
                    .addValue("errorCode", obj.getErrorCode())
                    .addValue("content", obj.getContent())
                    .addValue("description", obj.getDescription())
                    .addValue("orderNumber", obj.getOrderNumber())
            );
            if (rs > 0) {
                log.info("Store SouvenirOrder successfully!: {}", obj);
                return true;
            }
        } catch (Exception e) {
            log.error("obj: {}, Error: {}", obj, e.getMessage(), e);
        }
        return false;
    }
}

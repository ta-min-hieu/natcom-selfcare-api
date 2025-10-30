package com.ringme.dao.mysql.selfcare;

import com.ringme.model.selfcare.FtthPaymentHistory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class FtthPaymentHistoryDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(FtthPaymentHistory obj) {
        try {
            String SQL = """
                    INSERT INTO ftth_payment_history (ftth_account, money_dolar, money_haiti, error_code, order_number, content)
                    VALUES (:ftthAccount, :money_dolar, :money_haiti, :errorCode, :orderNumber, :content)
                """;

            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("ftthAccount", obj.getFtthAccount())
                    .addValue("money_dolar", obj.getMoneyDolar())
                    .addValue("money_haiti", obj.getMoneyHaiti())
                    .addValue("errorCode", obj.getErrorCode())
                    .addValue("orderNumber", obj.getOrderNumber())
                    .addValue("content", obj.getContent())
            );
            if (rs > 0) {
                log.info("Store successfully!: {}", obj);
                return true;
            }
        } catch (Exception e) {
            log.error("obj: {}, Error: {}", obj, e.getMessage(), e);
        }
        return false;
    }
}

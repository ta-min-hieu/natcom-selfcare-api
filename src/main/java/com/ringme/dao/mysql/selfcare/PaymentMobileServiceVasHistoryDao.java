package com.ringme.dao.mysql.selfcare;

import com.ringme.model.selfcare.PaymentMobileServiceVasHistory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class PaymentMobileServiceVasHistoryDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(PaymentMobileServiceVasHistory obj) {
        try {
            String SQL = """
                    INSERT INTO natcash_payment_mobile_service_vas_history (
                         isdn, package_code, money, error_code, order_number, user_msg
                     ) VALUES (
                         :isdn, :package_code, :money, :errorCode, :orderNumber, :user_msg
                     )
                """;

            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("isdn", obj.getIsdn())
                    .addValue("package_code", obj.getPackageCode())
                    .addValue("money", obj.getMoney())
                    .addValue("errorCode", obj.getErrorCode())
                    .addValue("orderNumber", obj.getOrderNumber())
                    .addValue("user_msg", obj.getUserMsg())
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

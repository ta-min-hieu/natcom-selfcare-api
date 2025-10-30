package com.ringme.dao.mysql.selfcare;

import com.ringme.model.selfcare.TkgPaymentHistory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class TkgPaymentHistoryDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(TkgPaymentHistory obj) {
        try {
            String SQL = """
                    INSERT INTO tkg_payment_history
                    (type, isdnee, sharer_isdn, service_code, price, status)
                    VALUES
                    (:type, :isdnee, :sharer_isdn, :service_code, :price, :status);
                """;

            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("type", obj.getType())
                    .addValue("isdnee", obj.getIsdnee())
                    .addValue("sharer_isdn", obj.getSharerIsdn())
                    .addValue("service_code", obj.getServiceCode())
                    .addValue("price", obj.getPrice())
                    .addValue("status", obj.getStatus())
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

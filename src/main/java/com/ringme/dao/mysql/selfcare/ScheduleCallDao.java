package com.ringme.dao.mysql.selfcare;

import com.ringme.model.selfcare.ScheduleCall;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class ScheduleCallDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(ScheduleCall obj) {
        try {
            String SQL = """
                INSERT INTO schedule_call (
                     support_type, start_date, end_date, isdn, language
                 ) VALUES (
                     :supportType, :startDate, :endDate, :isdn, :language
                 )
                """;

            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("supportType", obj.getSupportType().getType())
                    .addValue("startDate", obj.getStartDate())
                    .addValue("endDate", obj.getEndDate())
                    .addValue("language", obj.getLanguage())
                    .addValue("isdn", obj.getIsdn()));
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

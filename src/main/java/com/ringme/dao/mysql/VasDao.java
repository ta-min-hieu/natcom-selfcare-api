package com.ringme.dao.mysql;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class VasDao {
    @Autowired
    @Qualifier("vasNameJdbcTemplate")
    NamedParameterJdbcTemplate vasNameJdbcTemplate;

    @Cacheable(cacheManager = "rc", cacheNames = "rc60m", key = "'checkPromotionPackage:msisdn:' + #msisdn + ':packageCode:' + #packageCode")
    public boolean checkPromotionPackage(String msisdn, String packageCode) {
        try {
            String sql = sqlHandle(packageCode);
            if(sql == null) {
                log.error("Not found packageCode: {}, msisdn: {}", packageCode, msisdn);
                return false;
            }

            List<Integer> list = vasNameJdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("msisdn", msisdn),
                    (rs, rowNum) -> {
                        return rs.getInt("count(*)");
                    });

            if(list == null || list.isEmpty() || list.getFirst() == 0)
                return false;

            return true;
        } catch (Exception e) {
            log.error("msisdn: {}, packageCode: {}, error: {}", msisdn, packageCode, e.getMessage(), e);
        }
        return false;
    }

    private String sqlHandle(String packageCode) {
        String sql = null;
        switch (packageCode) {
            case "New_DL9" -> sql = "select count(*) from promotion_newdl9 where msisdn = :msisdn and end_time_reg>trunc (sysdate)";
            case "DL3" -> sql = "select count(*) from promotion_dl3 where msisdn = :msisdn and end_time_reg>trunc (sysdate)";
            case "Xchange50_Discount" -> sql = "select count(*) from promotion_x50_d where msisdn = :msisdn and (end_time_reg>trunc (sysdate) or end_time_reg is null)";
            case "Xchange25_Discount" -> sql = "select count(*) from promotion_x25_d where msisdn = :msisdn and (end_time_reg>trunc (sysdate) or end_time_reg is null)";
            case "Xchange50_Plus" -> sql = "select count(*) from promotion_exchange50_plus where msisdn = :msisdn and (end_time_reg>trunc (sysdate) or end_time_reg is null)";
            case "Xchange25_Plus" -> sql = "select count(*) from promotion_x25_p where msisdn = :msisdn and (end_time_reg>trunc (sysdate) or end_time_reg is null)";
            case "New-Xchange6" -> sql = "select count(*) from v_promo_x6_new where msisdn = :msisdn and end_time_reg>trunc (sysdate)";
        }

        return sql;
    }
}

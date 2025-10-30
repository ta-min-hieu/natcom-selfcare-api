package com.ringme.dao.mysql;

import com.ringme.common.Helper;
import com.ringme.dto.natcom.selfcare.response.packages.SubPackage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Repository
public class SubServiceDao {
    @Autowired
    @Qualifier("mynatcomNameJdbcTemplate")
    NamedParameterJdbcTemplate jdbcTemplate;

    @Cacheable(cacheManager = "rc", cacheNames = "rc60m", key = "'getPackageAmount:code:' + #code")
    public BigDecimal getPackageAmountByLanguageAndCode(String code) {
        try {
            String sql = """
                    select price from sub_service
                    where code = :code
                    """;

            List<Double> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("code", code),
                    (rs, rowNum) -> {
                        return rs.getDouble("price") * 1.1;
                    });
            if(list == null || list.isEmpty())
                return null;

            return Helper.roundingMode(list.getFirst());
        } catch (Exception e) {
            log.error("code: {}, error: {}", code, e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc24h", key = "'getSubPackageByServiceId:serviceId:' + #serviceId")
    public List<SubPackage> getSubPackageByServiceId(String serviceId) {
        try {
            String sql = """
                    select * from sub_service
                    where service_id = :serviceId
                    and status = 1
                    """;

            List<SubPackage> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("serviceId", serviceId),
                    (rs, rowNum) -> {
                        SubPackage subPackage = new SubPackage();
                        subPackage.setCode(rs.getString("code"));
                        subPackage.setPrice(rs.getString("price"));
                        subPackage.setUnit(rs.getString("unit"));
                        subPackage.setTypeAndPaymentMINatcash(rs.getInt("is_auto_renew"), rs.getInt("is_gift"));
                        return subPackage;
                    });
            if(list == null || list.isEmpty())
                return null;

            list.sort(Comparator.comparing(SubPackage::getType));

            return list;
        } catch (Exception e) {
            log.error("serviceId: {}, error: {}", serviceId, e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc24h", key = "'getAllVasPackageCode'")
    public List<String> getAllVasPackageCode() {
        try {
            String sql = """
                        select ss.code from sub_service ss
                        join service s on ss.service_id = s.id
                        join service_group sg on s.service_group_id = sg.id
                        where ss.status = 1 and sg.code in ('VAS: Education', 'VAS: Entertainment', 'VAS: Games', 'VAS: Utilities')
                    """;

            List<String> list = jdbcTemplate.query(sql, new MapSqlParameterSource(),
                    (rs, rowNum) -> {
                        return rs.getString("code");
                    });
            if(list == null || list.isEmpty())
                return null;

            log.info("list: {}", list);
            return list;
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }
}

package com.ringme.dao.mysql.loyalty;

import com.ringme.enums.loyalty.LoyaltyStatus;
import com.ringme.model.loyalty.voucher.Merchant;
import com.ringme.model.loyalty.voucher.VoucherTopic;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class MerchantDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'Merchant:id:' + #merchantId")
    public Merchant getMerchantById(int merchantId) {
        try {
            String sql = """
                    select * from merchant where status = 1 and id = :merchantId;
                    """;

            List<Merchant> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                    .addValue("merchantId", merchantId),
                    (rs, rowNum) -> {
                        Merchant r = new Merchant();
                        r.setId(rs.getLong("id"));
                        r.setName(rs.getString("name"));
                        r.setOwnerName(rs.getString("owner_name"));
                        r.setOwnerPhoneNumber(rs.getString("owner_phonenumber"));
                        r.setStatus(LoyaltyStatus.fromCode(rs.getInt("status")));
                        r.setNote(rs.getString("note"));
                        return r;
                    });

            if(list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }
}

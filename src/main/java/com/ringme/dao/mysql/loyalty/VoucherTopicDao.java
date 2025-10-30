package com.ringme.dao.mysql.loyalty;

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
public class VoucherTopicDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'VoucherTopics:'")
    public List<VoucherTopic> getVoucherTopics() {
        try {
            String sql = """
                    select * from voucher_topic where status = 1
                    """;

            return jdbcTemplate.query(sql, new MapSqlParameterSource(),
                    (rs, rowNum) -> {
                        VoucherTopic r = new VoucherTopic();
                        r.setId(rs.getInt("id"));
                        r.setName(rs.getString("name"));
                        r.setIconUrl(rs.getString("icon_url"));
                        return r;
                    });
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'VoucherTopics:id:' + #topicId")
    public VoucherTopic getVoucherTopicById(int topicId) {
        try {
            String sql = """
                    select * from voucher_topic where status = 1 and id = :topicId;
                    """;

            List<VoucherTopic> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                    .addValue("topicId", topicId),
                    (rs, rowNum) -> {
                        VoucherTopic r = new VoucherTopic();
                        r.setId(rs.getInt("id"));
                        r.setName(rs.getString("name"));
                        r.setIconUrl(rs.getString("icon_url"));
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

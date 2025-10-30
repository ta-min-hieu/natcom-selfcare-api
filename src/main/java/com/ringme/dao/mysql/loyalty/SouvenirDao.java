package com.ringme.dao.mysql.loyalty;

import com.ringme.common.Common;
import com.ringme.enums.loyalty.souvenir.SouvenirStatus;
import com.ringme.model.loyalty.Souvenir;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Log4j2
@Repository
public class SouvenirDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    Common common;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'Souvenirs'")
    public List<Souvenir> getSouvenirs() {
        try {
            String sql = """
                    select distinct s.id, s.title, s.point, s.unit, s.start_date, s.date_expired, s.icon_url, s.description
                    from loyalty_souvenir s
                    where s.status = 1 and s.quantity_total > s.quantity_real and s.quantity_total > s.quantity_exchanged
                    and (s.start_date is null or s.start_date < now())
                    and (s.date_expired is null or s.date_expired > now())
                    """;

            return jdbcTemplate.query(sql, new MapSqlParameterSource(),
                    (rs, rowNum) -> {
                        Souvenir o = new Souvenir();
                        o.setId(rs.getLong("id"));
                        o.setTitle(rs.getString("title"));
                        o.setMaxPoint(rs.getInt("point"));
                        o.setPointUnit(rs.getString("unit"));
                        o.setStartDate(rs.getDate("start_date"));
                        o.setDateExpired(rs.getDate("date_expired"));
                        o.setImageUrl(common.urlHandler(rs.getString("icon_url")));
                        o.setDescription(rs.getString("description"));
                        return o;
                    });
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    @CacheEvict(cacheManager = "rc", cacheNames = "rc10m", key = "'Souvenir:id:' + #id")
    public void clearCacheSouvenirById(long id) {}

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'Souvenir:id:' + #id")
    public Souvenir getSouvenirById(long id) {
        try {
            String sql = """
                    select distinct s.id, s.title, s.point, s.unit, s.start_date, s.date_expired, s.icon_url, s.description
                    from loyalty_souvenir s
                    where s.status = 1 and s.quantity_total > s.quantity_real and s.quantity_total > s.quantity_exchanged
                    and (s.start_date is null or s.start_date < now())
                    and (s.date_expired is null or s.date_expired > now())
                    and id = :id
                    """;

            List<Souvenir> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("id", id),
                    (rs, rowNum) -> {
                        Souvenir o = new Souvenir();
                        o.setId(rs.getLong("id"));
                        o.setTitle(rs.getString("title"));
                        o.setMaxPoint(rs.getInt("point"));
                        o.setPointUnit(rs.getString("unit"));
                        o.setStartDate(rs.getDate("start_date"));
                        o.setDateExpired(rs.getDate("date_expired"));
                        o.setImageUrl(common.urlHandler(rs.getString("icon_url")));
                        o.setDescription(rs.getString("description"));
                        return o;
                    });

            if (list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    public void clearCacheSouvenirByIsdn(String isdn) {
        Set<String> keys = redisTemplate.keys("rc10m::MySouvenir:" + isdn + "*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
    }

    @CacheEvict(cacheManager = "rc", cacheNames = "rc10m", key = "'Souvenir:' + #id")
    public void clearCacheSouvenirByIdAndShowroomId(long id) {}

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'Souvenir:' + #id")
    public Souvenir getSouvenirByIdAndShowroomId(long id) {
        try {
            String sql = """
                    select distinct s.* from loyalty_souvenir s
                    where s.id = :id and s.status = 1
                      and (s.start_date is null or s.start_date < now())
                      and (s.date_expired is null or s.date_expired > now())
                      and s.quantity_total > s.quantity_real and s.quantity_total > s.quantity_exchanged
                    """;

            List<Souvenir> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("id", id),
                    (rs, rowNum) -> {
                        Souvenir r = new Souvenir();
                        r.setId(rs.getLong("id"));
                        r.setTitle(rs.getString("title"));
                        r.setStatus(SouvenirStatus.fromCode(rs.getInt("status")));
                        r.setImageUrl(common.urlHandler(rs.getString("icon_url")));
                        r.setMaxPoint(rs.getInt("point"));
                        r.setPointUnit(rs.getString("unit"));
                        r.setDescription(rs.getString("description"));
                        r.setStartDate(rs.getDate("start_date"));
                        r.setDateExpired(rs.getDate("date_expired"));
                        r.setCreatedAt(rs.getDate("created_at"));
                        r.setUpdatedAt(rs.getDate("updated_at"));
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

    public boolean updateQuantityExchangedById(long id) {
        String SQL = """
                UPDATE loyalty_souvenir
                  SET quantity_exchanged = quantity_exchanged + 1
                  WHERE id = :id;
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("id", id));
            if (rs > 0) {
                log.info("successfully!: id: {}", id);
                return true;
            }
        } catch (Exception e) {
            log.error("form: {}, Error: {}", e.getMessage(), e);
        }
        return false;
    }
}

package com.ringme.dao.mysql.loyalty;

import com.ringme.common.Common;
import com.ringme.dto.ringme.loyalty.VoucherDto;
import com.ringme.enums.loyalty.LoyaltyStatus;
import com.ringme.model.loyalty.voucher.VoucherGroup;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class VoucherGroupDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    Common common;

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'VoucherGroups:topicId:' + #topicId")
    public List<VoucherDto> getVouchersGroupByTopicId(int topicId) {
        try {
            String sql = """
                    select distinct vg.id, vg.name, vg.description, vg.image_url, vg.max_point, vg.point_unit, vg.end_date, vg.id_merchant, m.name `merchant_name`
                    from voucher_group vg
                     join merchant m on vg.id_merchant = m.id and m.status = 1
                     join voucher_group_sub_merchant vgsm on vgsm.id_voucher_group = vg.id
                     join sub_merchant sm on sm.id = vgsm.id_sub_merchant and sm.status = 1
                    where vg.status = 1 and vg.quantity_total > vg.quantity_exchanged
                    and vg.start_date < now() and vg.end_date > now()
                    and vg.topic_id = :topic_id
                    """;

            return jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("topic_id", topicId),
                    (rs, rowNum) -> {
                        VoucherDto r = new VoucherDto();
                        r.setId(rs.getInt("id"));
                        r.setTitle(rs.getString("name"));
                        r.setDescription(rs.getString("description"));
                        r.setImageUrl(common.urlHandler(rs.getString("image_url")));
                        r.setMaxPoint(rs.getInt("max_point"));
                        r.setPointUnit(rs.getString("point_unit"));
                        r.setDateExpired(rs.getDate("end_date"));
                        r.setMerchantId(rs.getInt("id_merchant"));
                        r.setMerchantName(rs.getString("merchant_name"));
                        return r;
                    });
        } catch (Exception e) {
            log.error("topicId: {}, error: {}", topicId, e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'VoucherGroup:id:' + #id")
    public VoucherDto getVouchersGroupById(long id) {
        try {
            String sql = """
                    select distinct vg.id, vg.name, vg.description, vg.image_url, vg.max_point, vg.point_unit, vg.end_date, vg.id_merchant, m.name `merchant_name`
                    from voucher_group vg
                     join merchant m on vg.id_merchant = m.id and m.status = 1
                     join voucher_group_sub_merchant vgsm on vgsm.id_voucher_group = vg.id
                     join sub_merchant sm on sm.id = vgsm.id_sub_merchant and sm.status = 1
                    where vg.status = 1 and vg.quantity_total > vg.quantity_exchanged
                    and vg.start_date < now() and vg.end_date > now()
                    and vg.id = :id
                    """;

            List<VoucherDto> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("id", id),
                    (rs, rowNum) -> {
                        VoucherDto r = new VoucherDto();
                        r.setId(rs.getInt("id"));
                        r.setTitle(rs.getString("name"));
                        r.setDescription(rs.getString("description"));
                        r.setImageUrl(common.urlHandler(rs.getString("image_url")));
                        r.setMaxPoint(rs.getInt("max_point"));
                        r.setPointUnit(rs.getString("point_unit"));
                        r.setDateExpired(rs.getDate("end_date"));
                        r.setMerchantId(rs.getInt("id_merchant"));
                        r.setMerchantName(rs.getString("merchant_name"));
                        return r;
                    });

            if(list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("id: {}, error: {}", id, e.getMessage(), e);
        }
        return null;
    }

    @CacheEvict(cacheManager = "rc", cacheNames = "rc10m", key = "'VoucherGroups:id:' + #voucherGroupId")
    public void clearCacheVoucherGroupById(long voucherGroupId) {}

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'VoucherGroups:id:' + #voucherGroupId")
    public VoucherGroup getVoucherGroupById(long voucherGroupId) {
        try {
            String sql = """
                    select distinct vg.*, m.name `merchant_name`, vt.name `topic_name`
                    from voucher_group vg
                     join voucher_topic vt on vt.id = vg.topic_id
                     join merchant m on vg.id_merchant = m.id and m.status = 1
                     join voucher_group_sub_merchant vgsm on vgsm.id_voucher_group = vg.id
                     join sub_merchant sm on sm.id = vgsm.id_sub_merchant and sm.status = 1
                    where vg.status = 1 and vg.quantity_total > vg.quantity_exchanged
                    and vg.start_date < now() and vg.end_date > now()
                    and vg.id = :id
                    """;

            List<VoucherGroup> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("id", voucherGroupId),
                    (rs, rowNum) -> {
                        VoucherGroup r = new VoucherGroup();
                        r.setId(rs.getLong("id"));
                        r.setTopicId(rs.getLong("topic_id"));
                        r.setTopicName(rs.getString("topic_name"));
                        r.setIdMerchant(rs.getLong("id_merchant"));
                        r.setMerchantName(rs.getString("merchant_name"));
                        r.setStatus(LoyaltyStatus.fromCode(rs.getInt("status")));
                        r.setName(rs.getString("name"));
                        r.setQuantityTotal(rs.getInt("quantity_total"));
                        r.setQuantityExchanged(rs.getInt("quantity_exchanged"));
                        r.setMaxPoint(rs.getInt("max_point"));
                        r.setStartDate(rs.getDate("start_date"));
                        r.setEndDate(rs.getDate("end_date"));
                        r.setDescription(rs.getString("description"));
                        r.setImageUrl(rs.getString("image_url"));
                        r.setDiscountAmount(rs.getDouble("discount_amount"));
                        r.setPointUnit(rs.getString("point_unit"));
                        r.setDiscountUnit(rs.getString("discount_unit"));
                        return r;
                    });

            if(list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("id: {}, error: {}", voucherGroupId, e.getMessage(), e);
        }
        return null;
    }

    public boolean updateQuantityExchangedById(long id) {
        String SQL = """
                UPDATE voucher_group
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

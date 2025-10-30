package com.ringme.dao.mysql.loyalty;

import com.ringme.common.Common;
import com.ringme.dto.ringme.loyalty.MyGift;
import com.ringme.enums.loyalty.VoucherStatus;
import com.ringme.enums.loyalty.gift.MyGiftStatus;
import com.ringme.enums.loyalty.gift.MyGiftType;
import com.ringme.enums.loyalty.souvenir.SouvenirOrderStatus;
import com.ringme.model.loyalty.voucher.Voucher;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class VoucherDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    Common common;

    public boolean store(Voucher voucher) {
        String SQL = """
                INSERT INTO voucher (
                     code, id_voucher_group, voucher_group_name, status, isdn, used_date, topic_id, topic_name, id_merchant, merchant_name,
                     max_point, discount_unit, point_unit, discount_amount, description, image_url, start_date, end_date
                 ) VALUES (
                     :code, :idVoucherGroup, :voucherGroupName, :status, :isdn, :usedDate, :topicId, :topicName, :idMerchant, :merchantName,
                     :maxPoint, :discountUnit, :pointUnit, :discountAmount, :description, :imageUrl, :startDate, :endDate
                 )
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("code", voucher.getCode())
                    .addValue("idVoucherGroup", voucher.getIdVoucherGroup())
                    .addValue("voucherGroupName", voucher.getVoucherGroupName())
                    .addValue("status", voucher.getStatus().getType())
                    .addValue("isdn", voucher.getIsdn())
                    .addValue("usedDate", voucher.getUsedDate())
                    .addValue("startDate", voucher.getStartDate())
                    .addValue("endDate", voucher.getEndDate())
                    .addValue("topicId", voucher.getTopicId())
                    .addValue("topicName", voucher.getTopicName())
                    .addValue("idMerchant", voucher.getIdMerchant())
                    .addValue("merchantName", voucher.getMerchantName())
                    .addValue("maxPoint", voucher.getMaxPoint())
                    .addValue("discountUnit", voucher.getDiscountUnit())
                    .addValue("pointUnit", voucher.getPointUnit())
                    .addValue("discountAmount", voucher.getDiscountAmount())
                    .addValue("description", voucher.getDescription())
                    .addValue("imageUrl", voucher.getImageUrl()));
            if (rs > 0) {
                log.info("Store Voucher successfully!: {}", voucher);
                return true;
            }
        } catch (Exception e) {
            log.error("form: {}, Error: {}", e.getMessage(), e);
        }
        return false;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'MyVoucher:' + #isdn")
    public List<Voucher> getMyVoucherByIsdn(String isdn) {
        try {
            String sql = """
                    SELECT * FROM voucher WHERE isdn = :isdn
                    """;

            return jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("isdn", isdn),
                    (rs, rowNum) -> {
                        Voucher o = new Voucher();
                        o.setId(rs.getLong("id"));
                        o.setVoucherGroupName(rs.getString("voucher_group_name"));
                        o.setCode(rs.getString("code"));
                        o.setStatus(VoucherStatus.fromCode(rs.getInt("status")));
                        o.setMaxPoint(rs.getInt("max_point"));
                        o.setPointUnit(rs.getString("point_unit"));
                        o.setEndDate(rs.getDate("end_date"));
                        o.setDescription(rs.getString("description"));
                        o.setImageUrl(common.urlHandler(rs.getString("image_url")));
                        o.setIdMerchant(rs.getLong("id_merchant"));
                        o.setMerchantName(rs.getString("merchant_name"));
                        o.setUsedDate(rs.getDate("used_date"));
                        o.setStartDate(rs.getDate("start_date"));
                        o.setEndDate(rs.getDate("end_date"));
                        o.setDiscountAmount(rs.getDouble("discount_amount"));
                        o.setDiscountUnit(rs.getString("discount_unit"));
                        return o;
                    });
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'MyVoucher:' + #isdn + ':status:' + #status")
    public List<MyGift> getMyVoucherByIsdnAndStatus(String isdn, String status) {
        String sql = """
                    SELECT * FROM voucher WHERE isdn = :isdn
                    """;

        try {
            if(status.equals(MyGiftStatus.ACTIVE.getType()))
                sql += " and status = 0 and end_date > now()";
            else if(status.equals(MyGiftStatus.USED.getType()))
                sql += " and status = 1";
            else
                sql += " and status = 0 and end_date < now()";

            List<MyGift> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("isdn", isdn),
                    (rs, rowNum) -> {
                        MyGift o = new MyGift();
                        o.setId(rs.getLong("id"));
                        o.setTitle(rs.getString("voucher_group_name"));
                        o.setOrderCode(rs.getString("code"));
                        o.setMaxPoint(rs.getInt("max_point"));
                        o.setPointUnit(rs.getString("point_unit"));
                        o.setStartDate(rs.getDate("start_date"));
                        o.setDescription(rs.getString("description"));
                        o.setImageUrl(common.urlHandler(rs.getString("image_url")));
                        o.setIdMerchant(rs.getLong("id_merchant"));
                        o.setMerchantName(rs.getString("merchant_name"));
                        o.setUsedDate(rs.getDate("used_date"));
                        o.setStartDate(rs.getDate("start_date"));
                        o.setDateExpired(rs.getDate("end_date"));
                        o.setDiscountAmount(rs.getDouble("discount_amount"));
                        o.setDiscountUnit(rs.getString("discount_unit"));
                        o.setStatus(SouvenirOrderStatus.VALID);
                        o.setMyGiftType(MyGiftType.VOUCHER.getType());
                        o.setUpdateAt(rs.getTimestamp("updated_at"));
                        return o;
                    });

            log.info("success isdn: {}, status: {}, sql: {}, list: {}", isdn, status, sql, list);
            return list;
        } catch (Exception e) {
            log.error("isdn: {}, status: {}, sql: {}, error: {}", isdn, status, sql, e.getMessage(), e);
        }
        return null;
    }
}

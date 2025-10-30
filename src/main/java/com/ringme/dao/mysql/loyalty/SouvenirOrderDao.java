package com.ringme.dao.mysql.loyalty;

import com.ringme.common.Common;
import com.ringme.dto.ringme.loyalty.MyGift;
import com.ringme.enums.loyalty.gift.MyGiftStatus;
import com.ringme.enums.loyalty.gift.MyGiftType;
import com.ringme.enums.loyalty.souvenir.SouvenirOrderStatus;
import com.ringme.model.loyalty.SouvenirOrder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class SouvenirOrderDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    Common common;

    public boolean store(SouvenirOrder obj) {
        String SQL = """
                INSERT INTO loyalty_souvenir_order (souvenir_id, showroom_id, showroom_name, order_code, isdn, status, start_date, date_expired, icon_url, description, title, point, unit, province_id, province_name, district_id, district_name, address, open_time, longitude, latitude)
                VALUES (:souvenir_id, :showroomId, :showroomName, :orderCode, :isdn, :status, :startDate, :dateExpired, :iconUrl, :description, :title, :point, :unit, :province_id, :province_name, :district_id, :district_name, :address, :open_time, :longitude, :latitude);
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("souvenir_id", obj.getSouvenirId())
                    .addValue("showroomId", obj.getShowroomId())
                    .addValue("showroomName", obj.getShowroomName())
                    .addValue("orderCode", obj.getOrderCode())
                    .addValue("isdn", obj.getIsdn())
                    .addValue("status", obj.getStatus().getType())
                    .addValue("startDate", obj.getStartDate())
                    .addValue("dateExpired", obj.getDateExpired())
                    .addValue("iconUrl", common.urlHandler(obj.getImageUrl()))
                    .addValue("description", obj.getDescription())
                    .addValue("title", obj.getTitle())
                    .addValue("point", obj.getPoint())
                    .addValue("unit", obj.getUnit())
                    .addValue("province_id", obj.getProvinceId())
                    .addValue("province_name", obj.getProvinceName())
                    .addValue("district_id", obj.getDistrictId())
                    .addValue("district_name", obj.getDistrictName())
                    .addValue("address", obj.getAddress())
                    .addValue("open_time", obj.getOpenTime())
                    .addValue("longitude", obj.getLongitude())
                    .addValue("latitude", obj.getLatitude())
            );
            if (rs > 0) {
                log.info("Store SouvenirOrder successfully!: {}", obj);
                return true;
            }
        } catch (Exception e) {
            log.error("form: {}, Error: {}", e.getMessage(), e);
        }
        return false;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'MySouvenir:' + #isdn + ':status:' + #status")
    public List<MyGift> getMySouvenirByIsdnAndStatus(String isdn, String status) {
        String sql = """
                    SELECT * FROM loyalty_souvenir_order WHERE isdn = :isdn
                    """;

        try {
            if(status.equals(MyGiftStatus.ACTIVE.getType()))
                sql += " and status in (1, 2, 3) and date_expired > now()";
            else if(status.equals(MyGiftStatus.USED.getType()))
                sql += " and status = 4";
            else
                sql += " and status in (1, 2, 3) and date_expired < now()";

            List<MyGift> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("isdn", isdn),
                    (rs, rowNum) -> {
                        MyGift o = new MyGift();
                        o.setOrderCode(rs.getString("order_code"));
                        o.setIsdn(rs.getString("isdn"));
                        o.setTitle(rs.getString("title"));
                        o.setMaxPoint(rs.getInt("point"));
                        o.setPointUnit(rs.getString("unit"));
                        o.setDateExpired(rs.getDate("date_expired"));
                        o.setImageUrl(common.urlHandler(rs.getString("icon_url")));
                        o.setDescription(rs.getString("description"));
                        o.setShowroomId(rs.getString("showroom_id"));
                        o.setShowroomName(rs.getString("showroom_name"));
                        o.setMyGiftType(MyGiftType.SOUVENIR.getType());
                        o.setStatus(SouvenirOrderStatus.fromCode(rs.getInt("status")));
                        o.setProvinceId(rs.getString("province_id"));
                        o.setProvinceName(rs.getString("province_name"));
                        o.setDistrictId(rs.getString("district_id"));
                        o.setDistrictName(rs.getString("district_name"));
                        o.setAddress(rs.getString("address"));
                        o.setOpenTime(rs.getString("open_time"));
                        o.setLongitude(rs.getDouble("longitude"));
                        o.setLatitude(rs.getDouble("latitude"));
                        o.setUpdateAt(rs.getTimestamp("updated_at"));
                        return o;
                    });

            log.info("success isdn: {}, status: {}, sql: {}, list: {}", isdn, status, sql, list.size());
            return list;
        } catch (Exception e) {
            log.error("isdn: {}, status: {}, sql: {}, error: {}", isdn, status, sql, e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'MySouvenir:' + #isdn")
    public List<SouvenirOrder> getMySouvenirByIsdn(String isdn) {
        try {
            String sql = """
                    SELECT * FROM loyalty_souvenir_order WHERE isdn = :isdn
                    """;

            return jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("isdn", isdn),
                    (rs, rowNum) -> {
                        SouvenirOrder o = new SouvenirOrder();
                        o.setOrderCode(rs.getString("order_code"));
                        o.setIsdn(rs.getString("isdn"));
                        o.setTitle(rs.getString("title"));
                        o.setStatus(SouvenirOrderStatus.fromCode(rs.getInt("status")));
                        o.setPoint(rs.getInt("point"));
                        o.setUnit(rs.getString("unit"));
                        o.setDateExpired(rs.getDate("date_expired"));
                        o.setImageUrl(common.urlHandler(rs.getString("icon_url")));
                        o.setDescription(rs.getString("description"));
                        o.setShowroomId(rs.getString("showroom_id"));
                        o.setShowroomName(rs.getString("showroom_name"));
                        return o;
                    });
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }
}

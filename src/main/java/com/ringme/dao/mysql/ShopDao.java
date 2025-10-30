package com.ringme.dao.mysql;

import com.ringme.model.Shop;
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
public class ShopDao {
    @Autowired
    @Qualifier("mynatcomNameJdbcTemplate")
    NamedParameterJdbcTemplate jdbcTemplate;

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'Shop:id:' + #id")
    public Shop getShopById(String id) {
        try {
            String sql = """
                    select s.*, p.name as "province_name", d.name as "district_name"
                    from shop s
                    JOIN area p ON s.province_id = p.province AND p.district IS NULL AND p.precinct IS NULL
                    JOIN area d ON s.province_id = d.province AND s.district_id = d.district AND d.precinct IS NULL
                    where s.id = :id
                    and rownum <= 1
                    """;

            List<Shop> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("id", id),
                    (rs, rowNum) -> {
                        Shop shop = new Shop();
                        shop.setId(rs.getString("ID"));
                        shop.setName(rs.getString("NAME"));
                        shop.setAddr(rs.getString("ADDR"));
                        shop.setOpenTime(rs.getString("OPEN_TIME"));
                        shop.setLongitude(rs.getBigDecimal("LONGITUDE"));
                        shop.setLatitude(rs.getBigDecimal("LATITUDE"));
                        shop.setProvinceId(rs.getObject("PROVINCE_ID", Integer.class));
                        shop.setProvinceName(rs.getString("province_name"));
                        shop.setDistrictId(rs.getObject("DISTRICT_ID", Integer.class));
                        shop.setDistrictName(rs.getString("district_name"));
                        shop.setIsdn(rs.getString("ISDN"));
                        shop.setType(rs.getObject("TYPE", Integer.class));
                        shop.setStatus(rs.getObject("STATUS", Integer.class));
                        shop.setCreatedTime(rs.getDate("CREATED_TIME"));
                        shop.setCreatedBy(rs.getString("CREATED_BY"));
                        shop.setLastUpdatedTime(rs.getDate("LAST_UPDATED_TIME"));
                        shop.setLastUpdatedBy(rs.getString("LAST_UPDATED_BY"));
                        shop.setLa(rs.getObject("LA", Integer.class));
                        shop.setLo(rs.getObject("LO", Integer.class));
                        shop.setShopOrder(rs.getObject("SHOP_ORDER", Integer.class));
                        return shop;
                    });
            if(list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("id: {}, error: {}", id, e.getMessage(), e);
        }
        return null;
    }
}

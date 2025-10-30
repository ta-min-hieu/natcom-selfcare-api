package com.ringme.dao.mysql;

import com.ringme.common.Helper;
import com.ringme.dto.ringme.ftth.FtthRegister;
import com.ringme.dto.ringme.ftth.PaymentFtthInfo;
import com.ringme.enums.selfcare.FtthRegisterStatus;
import com.ringme.model.selfcare.FtthPackage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Log4j2
@Repository
public class FtthDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("paymentNameJdbcTemplate")
    NamedParameterJdbcTemplate paymentNameJdbcTemplate;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    public boolean store(FtthRegister form) {
        String SQL = """
                INSERT INTO ftth_register(package_code, package_name, name, isdner, isdnee, emailee, provice_id, provice_name, district_id, district_name, commune, note, status, order_code, branch_id)
                VALUES(:packageCode, :packageName, :name, :isdner, :isdnee, :emailee, :proviceId, :proviceName, :districtId, :districtName, :commune, :note, :status, :orderCode, :branch_id)
                """;
        try {
            Integer branchId = getBranchIdByProvinceId(Integer.parseInt(form.getProviceId().trim()));

            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("packageCode", form.getPackageCode())
                    .addValue("packageName", form.getPackageName())
                    .addValue("name", form.getName())
                    .addValue("isdner", form.getIsdner())
                    .addValue("isdnee", form.getIsdnee())
                    .addValue("emailee", form.getEmailee())
                    .addValue("proviceId", form.getProviceId())
                    .addValue("proviceName", form.getProviceName())
                    .addValue("districtId", form.getDistrictId())
                    .addValue("districtName", form.getDistrictName())
                    .addValue("commune", form.getCommune())
                    .addValue("note", form.getNote())
                    .addValue("status", branchId == null ? 0 : 1)
                    .addValue("branch_id", branchId)
                    .addValue("orderCode", form.getOrderCode()));
            if (rs > 0) {
                log.info("Ftth register successfully!| form: {}", form);
                return true;
            }
        } catch (Exception e) {
            log.error("form: {}, Error: {}", form, e.getMessage(), e);
        }
        return false;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'HistoryFtthRegister:isdner:' + #isdner + ':status:' + #status + ':startDate:' + #startDate + ':endDate:' + #endDate")
    public List<FtthRegister> getHistoryFtthRegisters(String isdner, List<Integer> status, String startDate, String endDate) {
        try {
            String sql = """
                    select id, package_code, package_name, name, isdner, isdnee, emailee, provice_id, provice_name, district_id,
                    district_name, commune, note, order_code, created_at, updated_at,
                    CASE
                        WHEN status IN (0, 1) THEN 1
                        ELSE status
                    END AS status
                    from ftth_register
                     where isdner = :isdner
                     and status in (:status)
                     and (:startDate is null or created_at >= :startDate)
                     and (:endDate is null or created_at <= :endDate)
                    order by updated_at desc
                    """;

            return jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("isdner", isdner)
                            .addValue("status", status)
                            .addValue("startDate", startDate)
                            .addValue("endDate", endDate),
                    (rs, rowNum) -> {
                        FtthRegister form = new FtthRegister();
                        form.setId(rs.getLong("id"));
                        form.setPackageCode(rs.getString("package_code"));
                        form.setPackageName(rs.getString("package_name"));
                        form.setName(rs.getString("name"));
                        form.setIsdner(rs.getString("isdner"));
                        form.setIsdnee(rs.getString("isdnee"));
                        form.setEmailee(rs.getString("emailee"));
                        form.setProviceId(rs.getString("provice_id"));
                        form.setProviceName(rs.getString("provice_name"));
                        form.setDistrictId(rs.getString("district_id"));
                        form.setDistrictName(rs.getString("district_name"));
                        form.setCommune(rs.getString("commune"));
                        form.setNote(rs.getString("note"));
                        form.setOrderCode(rs.getString("order_code"));
                        form.setStatus(statusHandle(rs.getInt("status")));
                        form.setCreatedAt(rs.getTimestamp("created_at"));
                        form.setUpdatedAt(rs.getTimestamp("updated_at"));

                        return form;
                    });
        } catch (Exception e) {
            log.error("isdner: {}, status: {}, startDate: {}, endDate: {}, error: {}", isdner, status, startDate, endDate, e.getMessage(), e);
        }
        return null;
    }

    private FtthRegisterStatus statusHandle(int status) {
        if(status == 0 || status == 1 || status == 2 || status == 3 || status == 4 || status == 5)
            return FtthRegisterStatus.IN_PROCESS;
        else if(status == 6)
            return FtthRegisterStatus.SUCCESS;
        else
            return FtthRegisterStatus.CANCEL;
    }

    public PaymentFtthInfo getPaymentFtthAccountInfo(String ftthAccount) {
        try {
            String sql = """
                    select a.user_using, a.isdn ,b.sta_of_cycle prior_debit,c.usage_charge_tax arise_charge,b.usage_charge_tax hot_charge,b.payment paid_amount, d.prepaid , b.sta_of_cycle + (b.usage_charge_tax*1.1)  - b.payment +b.adjustment_negative + b.discount to_pay
                       from subscriber a
                                left join debit_contract b
                                          on a.contract_id =b.contract_id
                                left join debit_contract c
                                          on a.contract_id =c.contract_id
                                left join contract_prepaid d
                                          on a.contract_id =d.contract_id
                       where a.status =2
                         and b.bill_cycle = add_months ( trunc(sysdate,'MM'),0)
                         and c.bill_cycle = add_months ( trunc(sysdate,'MM'),-1)
                         and   a.isdn = :ftthAccount
                    """;

            List<PaymentFtthInfo> list = paymentNameJdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("ftthAccount", ftthAccount),
                    (rs, rowNum) -> {
                        PaymentFtthInfo p = new PaymentFtthInfo();
                        p.setUserUsing(rs.getString("user_using"));
                        p.setFtthAccount(rs.getString("isdn"));
                        p.setPriorDebit(rs.getDouble("prior_debit"));
                        p.setAriseDebit(rs.getDouble("arise_charge"));
                        p.setPrepaid(rs.getDouble("prepaid"));
                        p.setHotCharge(rs.getDouble("hot_charge"));
                        p.setPaidAmount(rs.getDouble("paid_amount"));
                        p.setToPay(rs.getDouble("to_pay"));

                        return p;
                    });

            if(list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("ftthAccount: {}, error: {}", ftthAccount, e.getMessage(), e);
        }
        return null;
    }

    public void clearCacheHistoryFtthRegisterByIsdner(String isdner) {
        Set<String> keys = redisTemplate.keys("rc10m::HistoryFtthRegister:isdner:" + isdner + "*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc10m", key = "'getBranchIdByProvinceId:provinceId:' + #provinceId")
    public Integer getBranchIdByProvinceId(int provinceId) {
        try {
            String sql = """
                    select ftth_branch_id
                    from ftth_branch_province
                     where province_id = :province_id
                    """;

            List<Integer> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("province_id", provinceId),
                    (rs, rowNum) -> {
                        return rs.getInt("ftth_branch_id");
                    });

            if(list == null || list.isEmpty() || list.size() > 1)
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("provinceId: {}, error: {}", provinceId, e.getMessage(), e);
        }
        return null;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc24h", key = "'getFtthPackages:language:' + #language")
    public List<FtthPackage> getFtthPackages(String language) {

        String sql = """
                select * from ftth_package
                where status = 1
                """;

        return jdbcTemplate.query(sql, new MapSqlParameterSource(), (rs, rowNum) -> {
            return handleMap(rs, language, "");
        });
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc24h", key = "'getFtthPackage:id:' + #id + ':language:' + #language")
    public FtthPackage getFtthPackageById(String language, long id) {

        String sql = """
                select * from ftth_package
                where status = 1
                and id = :id
                """;

        List<FtthPackage> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                        .addValue("id", id),
                (rs, rowNum) -> {
                    return handleMap(rs, language);
                });

        if(list == null || list.isEmpty())
            return null;

        return list.getFirst();
    }

    private FtthPackage handleMap(ResultSet rs, String language) throws SQLException {
        FtthPackage p = new FtthPackage();
        p.setId(rs.getString("id"));
        p.setProductCode(rs.getString("code"));
        p.setProductName(rs.getString("name"));
        p.setGroupProduct(rs.getString("group"));

        if(language != null && language.equals("en"))
            p.setDescription(rs.getString("description_en"));
        else
            p.setDescription(rs.getString("description_ht"));

        return p;
    }

    private FtthPackage handleMap(ResultSet rs, String language, String all) throws SQLException {
        FtthPackage p = new FtthPackage();
        p.setId(rs.getString("id"));
        p.setProductCode(rs.getString("code"));
        p.setProductName(rs.getString("name"));
        p.setGroupProduct(rs.getString("group"));

        if(language != null && language.equals("en"))
            p.setDescription(rs.getString("short_description_en"));
        else
            p.setDescription(rs.getString("short_description_ht"));

        return p;
    }
}

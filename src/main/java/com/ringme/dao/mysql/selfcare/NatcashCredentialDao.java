package com.ringme.dao.mysql.selfcare;

import com.ringme.dto.natcom.natcash.request.CredentialRequest;
import com.ringme.enums.natcash.NatcashCallbackType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class NatcashCredentialDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean store(CredentialRequest obj, NatcashCallbackType type) {
        String SQL = """
                INSERT INTO natcash_credential (
                    request_id, partner_code,
                    device_id, device_model, os_name, os_version,
                    callback_url, timestamp, order_number,
                    amount, msisdn, type
                ) VALUES (
                    :requestId, :partnerCode,
                    :deviceId, :deviceModel, :osName, :osVersion,
                    :callbackUrl, :timestamp, :orderNumber,
                    :amount, :msisdn, :type
                )
                """;
        try {
            int rs = jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("requestId", obj.getRequestId())
                    .addValue("partnerCode", obj.getPartnerCode())
                    .addValue("deviceId", obj.getDeviceId())
                    .addValue("deviceModel", obj.getDeviceModel())
                    .addValue("osName", obj.getOsName())
                    .addValue("osVersion", obj.getOsVersion())
                    .addValue("callbackUrl", obj.getCallbackUrl())
                    .addValue("timestamp", obj.getTimestamp())
                    .addValue("orderNumber", obj.getOrderNumber())
                    .addValue("amount", obj.getAmount())
                    .addValue("msisdn", obj.getMsisdn())
                    .addValue("type", type.getType())
            );
            if (rs > 0) {
                log.info("Store NatcashCredential successfully!: {}", obj);
                return true;
            }
        } catch (Exception e) {
            log.error("obj: {}, Error: {}", obj, e.getMessage(), e);
        }
        return false;
    }

    public CredentialRequest selectByOrderNumber(String orderNumber) {
        try {
            String sql = """
                    SELECT
                        request_id, partner_code,
                        device_id, device_model, os_name, os_version,
                        callback_url, timestamp, order_number,
                        amount, msisdn
                    FROM natcash_credential
                    WHERE order_number = :orderNumber
                    """;

            List<CredentialRequest> list = jdbcTemplate.query(sql, new MapSqlParameterSource()
                            .addValue("orderNumber", orderNumber),
                    (rs, rowNum) -> {
                        CredentialRequest r = new CredentialRequest();
                        r.setRequestId(rs.getString("request_id"));
                        r.setPartnerCode(rs.getString("partner_code"));
                        r.setDeviceId(rs.getString("device_id"));
                        r.setDeviceModel(rs.getString("device_model"));
                        r.setOsName(rs.getString("os_name"));
                        r.setOsVersion(rs.getString("os_version"));
                        r.setCallbackUrl(rs.getString("callback_url"));
                        r.setTimestamp(rs.getLong("timestamp"));
                        r.setOrderNumber(rs.getString("order_number"));
                        r.setAmount(rs.getBigDecimal("amount"));
                        r.setMsisdn(rs.getString("msisdn"));
                        r.setRequestId(rs.getString("request_id"));
                        return r;
                    });
            if(list == null || list.isEmpty())
                return null;

            return list.getFirst();
        } catch (Exception e) {
            log.error("orderNumber: {}, error: {}", orderNumber, e.getMessage(), e);
        }
        return null;
    }
}

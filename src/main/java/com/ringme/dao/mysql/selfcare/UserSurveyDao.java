package com.ringme.dao.mysql.selfcare;

import com.ringme.model.selfcare.UserSurvey;
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
public class UserSurveyDao {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @CacheEvict(cacheManager = "rc", cacheNames = "rc24h", key = "'UserSurvey:isdn:' + #obj.isdn + ':surveyFormId:' + #obj.surveyFormId")
    public boolean store(UserSurvey obj) {
        try {
            String sql = """
                INSERT INTO user_survey (isdn, survey_id, survey_form_id)
                VALUES (:isdn, :surveyId, :surveyFormId)
            """;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("isdn", obj.getIsdn())
                    .addValue("surveyId", obj.getSurveyId())
                    .addValue("surveyFormId", obj.getSurveyFormId());

            int rs = jdbcTemplate.update(sql, params);

            if (rs > 0) {
                log.info("Store successfully!: {}", obj);
                return true;
            }
        } catch (Exception e) {
            log.error("obj: {}, Error: {}", obj, e.getMessage(), e);
        }
        return false;
    }

    @Cacheable(cacheManager = "rc", cacheNames = "rc24h", key = "'UserSurvey:isdn:' + #isdn + ':surveyFormId:' + #surveyFormId")
    public UserSurvey findByIsdnAndSurveyFormOnWeek(String isdn, String surveyFormId) {
        String sql = """
            SELECT *
            FROM user_survey
            WHERE isdn = :isdn AND survey_form_id = :surveyFormId
            AND created_at >= NOW() - INTERVAL 7 DAY
            limit 1
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("isdn", isdn)
                .addValue("surveyFormId", surveyFormId);

        List<UserSurvey> list = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            UserSurvey s = new UserSurvey();
            s.setId(rs.getLong("id"));
            s.setIsdn(rs.getString("isdn"));
            s.setSurveyId(rs.getString("survey_id"));
            s.setSurveyFormId(rs.getString("survey_form_id"));
            s.setCreatedAt(rs.getTimestamp("created_at"));
            return s;
        });

        if(list == null || list.isEmpty())
            return null;

        return list.getFirst();
    }
}

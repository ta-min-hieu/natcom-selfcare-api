package com.ringme.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class MariaDbConfig {

    @Primary
    @Bean(name = "selfcareDataSourceProperties")
    @ConfigurationProperties("maria.datasource.selfcare")
    public DataSourceProperties selfcareDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "selfcareDataSource")
    @ConfigurationProperties("maria.datasource.selfcare.configuration")
    public DataSource selfcareDataSource(@Qualifier("selfcareDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "selfcareTransactionManager")
    public PlatformTransactionManager selfcareTransactionManager(
            @Qualifier("selfcareDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = "selfcareNameJdbcTemplate")
    public NamedParameterJdbcTemplate selfcareNameJdbcTemplate(
            @Qualifier("selfcareDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
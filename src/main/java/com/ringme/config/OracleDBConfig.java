package com.ringme.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class OracleDBConfig {

    // ============ my-natcom ============
    @Bean(name = "mynatcomDataSourceProperties")
    @ConfigurationProperties("oracle.datasource.mynatcom")
    public DataSourceProperties mynatcomDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mynatcomDataSource")
    @ConfigurationProperties("oracle.datasource.mynatcom.configuration")
    public DataSource mynatcomDataSource(@Qualifier("mynatcomDataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setConnectionInitSql("ALTER SESSION SET CURRENT_SCHEMA = MY_NATCOM");
        return dataSource;
    }

    @Bean(name = "mynatcomTransactionManager")
    public PlatformTransactionManager mynatcomTransactionManager(
            @Qualifier("mynatcomDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mynatcomNameJdbcTemplate")
    public NamedParameterJdbcTemplate mynatcomNameJdbcTemplate(
            @Qualifier("mynatcomDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    // ============ payment ============
    @Bean(name = "paymentDataSourceProperties")
    @ConfigurationProperties("oracle.datasource.payment")
    public DataSourceProperties paymentDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "paymentDataSource")
    @ConfigurationProperties("oracle.datasource.payment.configuration")
    public DataSource paymentDataSource(@Qualifier("paymentDataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setConnectionInitSql("ALTER SESSION SET CURRENT_SCHEMA = payment_haiti");
        return dataSource;
    }

    @Bean(name = "paymentTransactionManager")
    public PlatformTransactionManager paymentTransactionManager(
            @Qualifier("paymentDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "paymentNameJdbcTemplate")
    public NamedParameterJdbcTemplate paymentNameJdbcTemplate(
            @Qualifier("paymentDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    // ============ vas ============
    @Bean(name = "vasDataSourceProperties")
    @ConfigurationProperties("oracle.datasource.vas")
    public DataSourceProperties vasDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "vasDataSource")
    @ConfigurationProperties("oracle.datasource.vas.configuration")
    public DataSource vasDataSource(@Qualifier("vasDataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setConnectionInitSql("ALTER SESSION SET CURRENT_SCHEMA = ncfree_smart_center");
        return dataSource;
    }

    @Bean(name = "vasTransactionManager")
    public PlatformTransactionManager vasTransactionManager(
            @Qualifier("vasDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "vasNameJdbcTemplate")
    public NamedParameterJdbcTemplate vasNameJdbcTemplate(
            @Qualifier("vasDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
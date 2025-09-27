package com.boozer.nexus.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.boozer.nexus.persistence")
@ConditionalOnProperty(prefix = "nexus.db", name = "enabled", havingValue = "true")
public class CliDatabaseConfiguration {

    @Bean
    public DataSource dataSource(Environment environment) {
        String url = environment.getProperty("spring.datasource.url");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("spring.datasource.url must be provided when nexus.db.enabled=true");
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(environment.getProperty("spring.datasource.username"));
        config.setPassword(environment.getProperty("spring.datasource.password"));
        config.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver"));
        config.setMaximumPoolSize(Integer.parseInt(environment.getProperty("spring.datasource.hikari.maximum-pool-size", "8")));
        config.setConnectionTimeout(Long.parseLong(environment.getProperty("spring.datasource.hikari.connection-timeout", "30000")));
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment environment) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        vendorAdapter.setShowSql(Boolean.parseBoolean(environment.getProperty("spring.jpa.show-sql", "false")));

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.boozer.nexus.persistence");

        Map<String, Object> jpaProps = new HashMap<>();
        jpaProps.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto", "update"));
        jpaProps.put("hibernate.dialect", environment.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect"));
        jpaProps.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        factory.setJpaPropertyMap(jpaProps);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

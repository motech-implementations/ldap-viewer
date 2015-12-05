package org.motechproject.nms.ldapbrowser.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.flywaydb.core.Flyway;
import org.motechproject.nms.ldapbrowser.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Application.class)
class JpaConfig implements TransactionManagementConfigurer {

    @Value("${dataSource.driverClassName}")
    private String driver;
    @Value("${dataSource.url}")
    private String url;
    @Value("${dataSource.username}")
    private String username;
    @Value("${dataSource.password}")
    private String password;
    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;
    @Value("${c3p0.maxPoolSize}")
    private int maxPoolSize;
    @Value("${c3p0.minPoolSize}")
    private int minPoolSize;
    @Value("${c3p0.maxStatements}")
    private int maxStatements;
    @Value("${c3p0.testConnectionOnCheckout}")
    private boolean testConnOnCheckout;

    @Bean
    public DataSource configureDataSource() throws PropertyVetoException, NamingException {
        ComboPooledDataSource ds = new ComboPooledDataSource();

        // jdbc
        ds.setDriverClass(driver);
        ds.setJdbcUrl(url);
        ds.setUser(username);
        ds.setPassword(password);
        // c3p0 pool
        ds.setMaxPoolSize(maxPoolSize);
        ds.setMinPoolSize(minPoolSize);
        ds.setMaxStatements(maxStatements);
        ds.setTestConnectionOnCheckout(testConnOnCheckout);

        return ds;
    }

    @Bean(initMethod = "migrate", name = "flyway")
    public Flyway flyway() throws PropertyVetoException, NamingException {
        Flyway flyway = new Flyway();
        flyway.setDataSource(configureDataSource());
        return flyway;
    }

    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean configureEntityManagerFactory() throws PropertyVetoException, NamingException {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(configureDataSource());
        entityManagerFactoryBean.setPackagesToScan("org.motechproject.nms");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, dialect);
        jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, hbm2ddlAuto);
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new JpaTransactionManager();
    }
}

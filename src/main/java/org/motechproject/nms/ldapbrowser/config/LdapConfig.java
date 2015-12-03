package org.motechproject.nms.ldapbrowser.config;

import org.apache.directory.ldap.client.api.AbstractPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LdapConfig {

    @Value("${ldap.host}")
    private String ldapHost;

    @Value("${ldap.port}")
    private int ldapPort;

    @Value("${ldap.useSsl}")
    private boolean useSsl;

    @Value("${ldap.admin.username}")
    private String adminUsername;

    @Value("${ldap.admin.password}")
    private String adminPassword;

    @Bean
    public LdapConnectionConfig ldapConnectionConfig() {
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost(ldapHost);
        config.setLdapPort(ldapPort);
        config.setUseSsl(useSsl);
        config.setName(String.format("uid=%s,ou=system", adminUsername));
        config.setCredentials(adminPassword);
        return config;
    }

    @Bean
    public AbstractPoolableLdapConnectionFactory ldapConnectionFactory() {
        return new DefaultPoolableLdapConnectionFactory(ldapConnectionConfig());
    }

    @Bean
    public LdapConnectionPool ldapConnectionPool() {
        LdapConnectionPool pool = new LdapConnectionPool(ldapConnectionFactory());
        pool.setTestOnBorrow(true);
        return pool;
    }
}

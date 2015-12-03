package org.motechproject.nms.ldapbrowser.config;

import org.motechproject.nms.ldapbrowser.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;

import static org.springframework.context.annotation.ComponentScan.Filter;

@Configuration
@PropertySources({
		@PropertySource("classpath:persistence.properties"),
		@PropertySource(value = "file:/etc/nms-ldap.persistence", ignoreResourceNotFound = true)
})
@ComponentScan(basePackageClasses = Application.class, excludeFilters = @Filter({Controller.class, Configuration.class}))
class ApplicationConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
}
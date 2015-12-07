package org.motechproject.nms.ldapbrowser.config;

import org.motechproject.nms.ldapbrowser.ldap.LdapAuthenticationProvider;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import org.motechproject.nms.ldapbrowser.ldap.UserService;

import javax.inject.Inject;

@Configuration
@EnableWebMvcSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices("remember-me-key", userService());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
	}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .eraseCredentials(false)
            .authenticationProvider(ldapAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/", "/favicon.ico", "/resources/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/signin")
                .permitAll()
                .failureUrl("/signin?error=1")
                .loginProcessingUrl("/authenticate")
                .and()
            .logout()
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessUrl("/signin?logout")
                .and()
            .rememberMe()
                .rememberMeServices(rememberMeServices())
                .key("remember-me-key");
    }
}
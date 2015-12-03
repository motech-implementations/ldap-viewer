package org.motechproject.nms.ldapbrowser.ldap;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;

/**
 * Created by GES0_000 on 03.12.2015.
 */
@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

    @Inject
    private LdapUserService ldapUserService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LdapUser user = ldapUserService.authenticate(authentication.getName(), (String) authentication.getCredentials());
        if (user == null) {
            throw new BadCredentialsException("Unable to authenticate user " + authentication.getName());
        }
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
                Collections.singleton(createAuthority()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private GrantedAuthority createAuthority() {
        return new SimpleGrantedAuthority("ADMIN_ROLE");
    }
}

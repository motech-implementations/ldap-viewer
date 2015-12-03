package org.motechproject.nms.ldapbrowser.ldap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.inject.Inject;
import java.util.Collections;

public class UserService implements UserDetailsService {
	
	@Inject
	private LdapUserService ldapUserService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LdapUser ldapUser = ldapUserService.getUser(username);
		if (ldapUser == null || !ldapUser.isAdmin()) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(ldapUser);
	}

	public void signin(LdapUser user) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(user));
	}

	private Authentication authenticate(LdapUser user) {
		return new UsernamePasswordAuthenticationToken(createUser(user), null, Collections.singleton(createAuthority()));
	}

	private User createUser(LdapUser user) {
		return new User(user.getUsername(), user.getPassword(), Collections.singleton(createAuthority()));
	}

	private GrantedAuthority createAuthority() {
		return new SimpleGrantedAuthority("ADMIN_ROLE");
	}

}

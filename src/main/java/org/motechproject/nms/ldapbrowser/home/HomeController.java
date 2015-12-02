package org.motechproject.nms.ldapbrowser.home;

import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.security.Principal;

@Controller
public class HomeController {

	@Inject
	private RegionService regionService;

	@Inject
	private LdapUserService ldapUserService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal) {
		return principal == null ? "signin/signin" : "home/homeSignedIn";
	}
}

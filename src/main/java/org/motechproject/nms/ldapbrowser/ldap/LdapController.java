package org.motechproject.nms.ldapbrowser.ldap;

import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.motechproject.nms.ldapbrowser.support.web.DtData;
import org.motechproject.nms.ldapbrowser.support.web.DtRequest;
import org.motechproject.nms.ldapbrowser.support.web.MessageHelper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@Controller
class LdapController {

    @Inject
    private LdapUserService ldapUserService;

    @Inject
    private RegionService regionService;

    @RequestMapping(value = "ldap/users", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public DtData<LdapUser> getUsers(@RequestBody DtRequest dtRequest) {
        UsersQuery query = new UsersQuery(dtRequest.getStart(), dtRequest.getLength());

        List<LdapUser> users = ldapUserService.getUsers(query);
        long totalUsers = ldapUserService.countUsers(query);

        return new DtData<>(users, totalUsers);
    }

    @RequestMapping(value = "ldap/user", method = RequestMethod.GET)
    public ModelAndView createUserPage() {
        ModelAndView mav = new ModelAndView("ldap/user");
        mav.getModelMap().put("user", new LdapUser());
        addRegionDate(mav);

        return mav;
    }

    @RequestMapping(value = "ldap/user/{username}", method = RequestMethod.GET)
    public ModelAndView updateUsePage(@PathVariable String username) {
        LdapUser user = ldapUserService.getUser(username);

        ModelAndView mav = new ModelAndView("ldap/user");
        mav.getModelMap().put("user", user);
        addRegionDate(mav);

        return mav;
    }

    @RequestMapping(value = "ldap/user", method = RequestMethod.POST)
    public ModelAndView saveUser(@Valid @ModelAttribute LdapUser user, Errors errors, RedirectAttributes ra) {
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("/ldap/user");
            MessageHelper.addErrorAttribute(mav, "user.save.error");
            return mav;
        } else {
            ldapUserService.saveUser(user);
            MessageHelper.addSuccessAttribute(ra, "user.saved");
            return new ModelAndView("redirect:/");
        }
    }

    private void addRegionDate(ModelAndView mav) {
        mav.getModelMap().put("states", regionService.availableStateNames());
        mav.getModelMap().put("districts", regionService.availableDistrictNames());
    }
}

package org.motechproject.nms.ldapbrowser.ldap;

import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.motechproject.nms.ldapbrowser.support.web.DtData;
import org.motechproject.nms.ldapbrowser.support.web.DtRequest;
import org.motechproject.nms.ldapbrowser.support.web.MessageHelper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
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
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
class LdapController {

    private static final String STATES = "states";
    private static final String DISTRICTS = "districts";

    @Inject
    private LdapUserService ldapUserService;

    @Inject
    private RegionService regionService;

    @Inject
    private LdapUserValidator validator;

    // AJAX

    @RequestMapping(value = "ldap/users", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public DtData<LdapUser> getUsers(@RequestBody DtRequest dtRequest, Principal principal) {
        UsersQuery query = new UsersQuery(dtRequest.getStart(), dtRequest.getLength());

        List<LdapUser> users = ldapUserService.getUsers(query, principal.getName());

        return new DtData<>(users, users.size());
    }

    @RequestMapping(value = "ldap/user/districts/{stateName}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<String> getDistrictNamesForExistingUser(@PathVariable String stateName) {
        return regionService.availableDistrictNames(stateName);
    }

    @RequestMapping(value = "ldap/districts/{stateName}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<String> getDistrictNamesForNewUser(@PathVariable String stateName) {
        return regionService.availableDistrictNames(stateName);
    }

    // PAGES

    @RequestMapping(value = "ldap/user", method = RequestMethod.GET)
    public ModelAndView createUserPage(Principal principal) {
        LdapUser editedUser = new LdapUser();
        LdapUser currentUser = getCurrentUser(principal);

        ModelAndView mav = new ModelAndView("ldap/user");
        editedUser.setUiEdit(false);
        mav.getModelMap().put("user", editedUser);
        addRegionalData(mav, currentUser, editedUser);

        return mav;
    }

    @RequestMapping(value = "ldap/user/{username}", method = RequestMethod.GET)
    public ModelAndView updateUserPage(@PathVariable String username, Principal principal) {
        LdapUser editedUser = ldapUserService.getUser(username);
        LdapUser currentUser = getCurrentUser(principal);

        ModelAndView mav = new ModelAndView("ldap/user");
        editedUser.setUiEdit(true);
        mav.getModelMap().put("user", editedUser);
        addRegionalData(mav, currentUser, editedUser);

        return mav;
    }

    @RequestMapping(value = "ldap/user", method = RequestMethod.POST)
    public ModelAndView saveUser(@ModelAttribute LdapUser user, Errors errors, RedirectAttributes ra,
                                 Principal principal) {
        validator.validate(user, errors);

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("/ldap/user");
            LdapUser currentUser = getCurrentUser(principal);

            mav.getModelMap().put("user", user);
            addRegionalData(mav, currentUser, user);
            addErrorAlert(errors, mav);

            return mav;
        } else {
            ldapUserService.saveUser(user);
            MessageHelper.addSuccessAttribute(ra, "user.saved");
            return new ModelAndView("redirect:/");
        }
    }

    @RequestMapping(value = "ldap/user/{username}/delete", method = RequestMethod.GET)
    public String deleteUser(@PathVariable String username, RedirectAttributes ra) {
        ldapUserService.deleteUser(username);
        MessageHelper.addSuccessAttribute(ra, "user.deleted");
        return "redirect:/";
    }

    private LdapUser getCurrentUser(Principal principal) {
        return ldapUserService.getUser(principal.getName());
    }

    private void addRegionalData(ModelAndView mav, LdapUser currentUser, LdapUser editedUser) {
        if (currentUser.isNationalLevel()) {
            mav.getModelMap().put(STATES, regionService.availableStateNames());
            // load districts if a state is selected
            if (!StringUtils.isEmpty(editedUser.getState())) {
                mav.getModelMap().put(DISTRICTS, regionService.availableDistrictNames(editedUser.getState()));
            }
        } else if (currentUser.isStateLevel()) {
            // one state and a list of districts in this case
            mav.getModelMap().put(STATES, Collections.singletonList(currentUser.getState()));
            mav.getModelMap().put(DISTRICTS, regionService.availableDistrictNames(currentUser.getState()));
        } else if (currentUser.isDistrictLevel()) {
           // one state and one district
            mav.getModelMap().put(STATES, Collections.singletonList(currentUser.getState()));
            mav.getModelMap().put(DISTRICTS, Collections.singletonList(currentUser.getDistrict()));
        } else {
            throw new IllegalStateException("User " + currentUser.getName() + " has wrong admin state");
        }
    }

    private void addErrorAlert(Errors errors, ModelAndView mav) {
        for (FieldError fieldError : errors.getFieldErrors()) {
            MessageHelper.addErrorAttribute(mav, fieldError.getCode());
            return;
        }
    }
}

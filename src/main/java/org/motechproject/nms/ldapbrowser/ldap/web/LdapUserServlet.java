package org.motechproject.nms.ldapbrowser.ldap.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserValidator;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.CreateUserPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.EditUserPageAction;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.pentaho.platform.util.beans.ActionHarness;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public class LdapUserServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(LdapUserServlet.class);

    private LdapUserService ldapUserService;
    private RegionService regionService;
    private LdapUserValidator validator;
    private TemplateEngine templateEngine;
    private UrlMatcher urlMatcher;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String ctxPath = req.getContextPath();
            if (urlMatcher.isNewUserReq(ctxPath)) {
                executePageAction(new CreateUserPageAction(), req, resp);
            } else if (urlMatcher.isEditUserReq(ctxPath)) {
                executePageAction(new EditUserPageAction(), req, resp);
            } else if (urlMatcher.isDeleteUserReq(ctxPath)) {

            } else {
                LOG.debug("Unrecognized context path: " + ctxPath);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ServletException("Error while executing page action", e);
        }
    }

    @RequestMapping(value = "ldap/user", method = RequestMethod.POST)
    public ModelAndView saveUser(@ModelAttribute LdapUser user, Errors errors,
                                 Principal principal) {
        validator.validate(user, errors);

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("/ldap/user");
            LdapUser currentUser = getCurrentUser(principal);

            mav.getModelMap().put("user", user);
            //addRegionalData(mav, currentUser, user);
            addErrorAlert(errors, mav);

            return mav;
        } else {
            ldapUserService.saveUser(user);
            //MessageHelper.addSuccessAttribute(ra, "user.saved");
            return new ModelAndView("redirect:/");
        }
    }

    @RequestMapping(value = "ldap/user/{username}/delete", method = RequestMethod.GET)
    public String deleteUser(@PathVariable String username) {
        ldapUserService.deleteUser(username);
        //MessageHelper.addSuccessAttribute(ra, "user.deleted");
        return "redirect:/";
    }

    private LdapUser getCurrentUser(Principal principal) {
        return ldapUserService.getUser(principal.getName());
    }

    private void addErrorAlert(Errors errors, ModelAndView mav) {
    /*    for (FieldError fieldError : errors.getFieldErrors()) {
            MessageHelper.addErrorAttribute(mav, fieldError.getCode());
            return;
        }*/
    }

    public void setLdapUserService(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setValidator(LdapUserValidator validator) {
        this.validator = validator;
    }

    private void executePageAction(AbstractPageAction action, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ActionHarness actionHarness = new ActionHarness(action);
        prepareAction(actionHarness, req, resp);
        action.execute();
    }

    private void prepareAction(ActionHarness action, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        action.setValues(req.getParameterMap());

        action.setValue("outputStream", resp.getOutputStream());
        // TODO:
        //action.setCurrentUsername("TODO");
        action.setValue("ldapUserService", ldapUserService);
        action.setValue("regionService", regionService);
        action.setValue("templateEngine", templateEngine);

        WebContext thCtx = new WebContext(req, resp, getServletContext());
        action.setValue("thymeleafContext", thCtx);
    }
}

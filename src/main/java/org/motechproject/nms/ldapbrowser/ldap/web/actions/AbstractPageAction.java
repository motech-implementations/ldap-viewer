package org.motechproject.nms.ldapbrowser.ldap.web.actions;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.pentaho.platform.api.action.IStreamingAction;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractPageAction implements IStreamingAction {

    private static final String STATES = "states";
    private static final String DISTRICTS = "districts";

    private OutputStream outputStream;
    private TemplateEngine templateEngine;
    private IContext thymeleafContext;
    private LdapUserService ldapUserService;
    private RegionService regionService;
    private String currentUsername;

    @Override
    public String getMimeType(String s) {
        return "text/html";
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void setThymeleafContext(IContext thymeleafContext) {
        this.thymeleafContext = thymeleafContext;
    }

    public void setLdapUserService(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }

    protected TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    protected IContext getThymeleafContext() {
        return thymeleafContext;
    }

    public LdapUserService getLdapUserService() {
        return ldapUserService;
    }

    protected void addRegionalDataToModel(Map<String, Object> modelMap, LdapUser currentUser, LdapUser editedUser) {
        if (currentUser.isNationalLevel()) {
            modelMap.put(STATES, regionService.availableStateNames());
            // load districts if a state is selected
            if (!StringUtils.isEmpty(editedUser.getState())) {
                modelMap.put(DISTRICTS, regionService.availableDistrictNames(editedUser.getState()));
            }
        } else if (currentUser.isStateLevel()) {
            // one state and a list of districts in this case
            modelMap.put(STATES, Collections.singletonList(currentUser.getState()));
            modelMap.put(DISTRICTS, regionService.availableDistrictNames(currentUser.getState()));
        } else if (currentUser.isDistrictLevel()) {
            // one state and one district
            modelMap.put(STATES, Collections.singletonList(currentUser.getState()));
            modelMap.put(DISTRICTS, Collections.singletonList(currentUser.getDistrict()));
        } else {
            throw new IllegalStateException("User " + currentUser.getName() + " has wrong admin state");
        }
    }

    protected LdapUser getCurrentUser() {
        return ldapUserService.getUser(currentUsername);
    }
}

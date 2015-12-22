package org.motechproject.nms.ldapbrowser.ldap.web.actions;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.pentaho.platform.api.action.IStreamingAction;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractPageAction implements IStreamingAction {

    private static final String STATES = "states";
    private static final String DISTRICTS = "districts";
    private static final String STATE_DISTRICTS = "stateDistricts";

    protected static final String USER_ADMIN_MODE = "userAdminMode";

    private OutputStream outputStream;
    private TemplateEngine templateEngine;
    private WebContext thymeleafContext;
    private LdapUserService ldapUserService;
    private RegionService regionService;
    private String currentUsername;
    private Map<String, String[]> parametersMap;

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

    public void setThymeleafContext(WebContext thymeleafContext) {
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

    protected TemplateEngine getTemplateEnginue() {
        return templateEngine;
    }

    protected WebContext getThymeleafContext() {
        return thymeleafContext;
    }

    public LdapUserService getLdapUserService() {
        return ldapUserService;
    }

    public Map<String, String[]> getParametersMap() {
        return parametersMap;
    }

    public void setParametersMap(Map<String, String[]> parametersMap) {
        this.parametersMap = parametersMap;
    }

    protected void addRegionalDataToModel(LdapUser currentUser, LdapUser editedUser) {
        thymeleafContext.setVariable(STATES, regionService.availableStateNames());
        thymeleafContext.setVariable(DISTRICTS, regionService.allAvailableDistrictInfo());
        thymeleafContext.setVariable(STATE_DISTRICTS, regionService.availableDistrictNames(getSelectedState(editedUser)));
    }

    protected LdapUser getCurrentUser() {
        return ldapUserService.getUser(currentUsername);
    }

    protected void setModelVariable(String varName, Object value) {
        thymeleafContext.setVariable(varName, value);
    }

    protected void printView(String viewName) throws IOException {
        Writer writer = new PrintWriter(outputStream);
        templateEngine.process(viewName, thymeleafContext, writer);
        writer.flush();
    }

    private String getSelectedState(LdapUser editedUser) {
        if (StringUtils.isNotBlank(editedUser.getState())) {
            return editedUser.getState();
        }
        List<String> states = regionService.availableStateNames();
        if (!states.isEmpty()) {
            return states.get(0);
        }

        return null;
    }
}

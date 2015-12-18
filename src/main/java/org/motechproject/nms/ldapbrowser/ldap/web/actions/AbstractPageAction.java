package org.motechproject.nms.ldapbrowser.ldap.web.actions;

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
import java.util.Map;

public abstract class AbstractPageAction implements IStreamingAction {

    private static final String STATES = "states";
    private static final String DISTRICTS = "districts";

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
//        if (currentUser.getRoles().isNationalLevel()) {
//            thymeleafContext.setVariable(STATES, regionService.availableStateNames());
//            // load districts if a state is selected
//            if (!StringUtils.isEmpty(editedUser.getState())) {
//                thymeleafContext.setVariable(DISTRICTS, regionService.availableDistrictNames(editedUser.getState()));
//            }
//        } else if (currentUser.isStateLevel()) {
//            // one state and a list of districts in this case
//            thymeleafContext.setVariable(STATES, Collections.singletonList(currentUser.getState()));
//            thymeleafContext.setVariable(DISTRICTS, regionService.availableDistrictNames(currentUser.getState()));
//        } else if (currentUser.isDistrictLevel()) {
//            // one state and one district
//            thymeleafContext.setVariable(STATES, Collections.singletonList(currentUser.getState()));
//            thymeleafContext.setVariable(DISTRICTS, Collections.singletonList(currentUser.getDistrict()));
//        } else {
//            throw new IllegalStateException("User " + currentUser.getName() + " has wrong admin state");
//        }
        thymeleafContext.setVariable(STATES, regionService.availableStateNames());
        thymeleafContext.setVariable(DISTRICTS, regionService.allAvailableDistrictInfo());
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
}

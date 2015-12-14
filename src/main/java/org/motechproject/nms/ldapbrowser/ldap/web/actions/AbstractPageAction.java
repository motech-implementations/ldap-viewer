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

public abstract class AbstractPageAction implements IStreamingAction {

    private static final String STATES = "states";
    private static final String DISTRICTS = "districts";

    private OutputStream outputStream;
    private InputStream inputStream;
    private TemplateEngine templateEngine;
    private WebContext thymeleafContext;
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

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }

    protected InputStream getInputStream() {
        return inputStream;
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

    protected void addRegionalDataToModel(LdapUser currentUser, LdapUser editedUser) {
//        if (currentUser.isNationalLevel()) {
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

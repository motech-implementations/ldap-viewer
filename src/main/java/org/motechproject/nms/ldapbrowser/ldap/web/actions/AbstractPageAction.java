package org.motechproject.nms.ldapbrowser.ldap.web.actions;

import org.apache.commons.lang.StringUtils;
import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;
import org.motechproject.nms.ldapbrowser.ldap.LdapAdminRights;
import org.motechproject.nms.ldapbrowser.ldap.LdapRole;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.pentaho.platform.api.action.IStreamingAction;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractPageAction implements IStreamingAction {

    private static final String STATES = "states";
    private static final String DISTRICTS = "districts";
    private static final String STATE_DISTRICTS = "stateDistricts";

    protected static final String ADMIN_RIGHTS = "adminRights";

    private OutputStream outputStream;
    private TemplateEngine templateEngine;
    private WebContext thymeleafContext;
    private LdapUserService ldapUserService;
    private RegionService regionService;
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
        thymeleafContext.setVariable(STATES, filerStatesForUser(editedUser, regionService.availableStateNames()));
        thymeleafContext.setVariable(DISTRICTS, filterDistrictsForUser(editedUser, regionService.allAvailableDistrictInfo()));
        thymeleafContext.setVariable(STATE_DISTRICTS, regionService.availableDistrictNames(getSelectedState(editedUser)));
    }

    private List<String> filerStatesForUser(LdapUser user, List<String> availableStates) {
        if (StringUtils.isNotBlank(user.getDistrict())) {
            // District-level user cannot be assigned state roles
            return new ArrayList<>();
        } else if (StringUtils.isNotBlank(user.getState())) {
            // Just one state for state-level users
            List<String> states = new ArrayList<>();
            states.add(user.getState());
            return states;
        } else {
            return availableStates;
        }
    }

    private List<DistrictInfo> filterDistrictsForUser(LdapUser user, List<DistrictInfo> availableDistricts) {
        if (StringUtils.isNotBlank(user.getDistrict())) {
            // Just one district for district-level users
            List<DistrictInfo> districts = new ArrayList<>();
            districts.add(new DistrictInfo(user.getState(), user.getDistrict()));

            return districts;
        } else if (StringUtils.isNotBlank(user.getState())) {
            // All districts from the state for state-level users
            List<DistrictInfo> districts = new ArrayList<>();
            for (DistrictInfo districtInfo : availableDistricts) {
                if (districtInfo.getState().equals(user.getState())) {
                    districts.add(districtInfo);
                }
            }

            return districts;
        } else {
            return availableDistricts;
        }
    }

    protected void addCurrentUserAdminRightsToModel(LdapUser userInEdition) {
        List<String> states = new ArrayList<>();

        LdapUser currentUser = getCurrentUser();
        boolean nationalAdmin = false;
        boolean masterAdmin = false;
        boolean editNationalRoles = false;

        for (LdapRole role : currentUser.getRoles()) {
            if (role.isMasterAdmin()) {
                masterAdmin = true;
                nationalAdmin = true;
                editNationalRoles = StringUtils.isBlank(userInEdition.getState()) && StringUtils.isBlank(userInEdition.getDistrict());
            } else if (role.isAdmin() && StringUtils.isBlank(role.getState()) && StringUtils.isBlank(role.getDistrict())) {
                nationalAdmin = true;
                editNationalRoles = StringUtils.isBlank(userInEdition.getState()) && StringUtils.isBlank(userInEdition.getDistrict());
            } else if (role.isAdmin() && StringUtils.isNotBlank(role.getState()) && StringUtils.isBlank(role.getDistrict())) {
                states.add(role.getState());
            }
        }

        thymeleafContext.setVariable(ADMIN_RIGHTS, new LdapAdminRights(editNationalRoles, masterAdmin, nationalAdmin, states));
    }

    protected LdapUser getCurrentUser() {
        return ldapUserService.getLoggedUser();
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

package org.motechproject.nms.ldapbrowser.ldap.web;

import org.apache.commons.io.IOUtils;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.AbstractPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.get.CreateUserPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.get.EditUserPageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.get.UserTablePageAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.post.DeleteUserAction;
import org.motechproject.nms.ldapbrowser.ldap.web.actions.post.SaveUserAction;
import org.motechproject.nms.ldapbrowser.ldap.web.validator.LdapUserValidator;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.pentaho.platform.util.beans.ActionHarness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LdapUserServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LdapUserServlet.class);

    private static final String USERNAME = "username";
    private static final String VALIDATOR = "validator";

    private LdapUserService ldapUserService;
    private RegionService regionService;
    private LdapUserValidator validator;
    private TemplateEngine templateEngine;
    private UrlMatcher urlMatcher;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String path = req.getPathInfo();

            if (urlMatcher.isUserListReq(path)) {
                userTablePage(req, resp, out);
            } else if (urlMatcher.isNewUserReq(path)) {
                newUserPage(req, resp, out);
            } else if (urlMatcher.isEditUserReq(path)) {
                editUserPage(req, resp, out);
            } else {
                LOG.debug("Unrecognized context path: " + path);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            writeResposne(resp, out);
        } catch (Exception e) {
            throw new ServletException("Error while executing GET request: " + req.getContextPath(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String path = req.getPathInfo();

            if (urlMatcher.isSaveUserReq(path)) {
                saveUser(req, resp, out);
            } else if (urlMatcher.isDeleteUserReq(path)) {
                deleteUser(req, resp, out);
            } else {
                LOG.debug("Unrecognized context path: " + path);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            writeResposne(resp, out);
        } catch (Exception e) {
            throw new ServletException("Error while executing POST request: " + req.getContextPath(), e);
        }
    }

    private void userTablePage(HttpServletRequest req, HttpServletResponse resp, OutputStream out) throws Exception {
        UserTablePageAction action = new UserTablePageAction();
        prepareAction(action, req, resp, out);
        action.execute();
    }

    private void newUserPage(HttpServletRequest req, HttpServletResponse resp, OutputStream out) throws Exception {
        CreateUserPageAction action = new CreateUserPageAction();
        prepareAction(action, req, resp, out);
        action.execute();
    }

    private void editUserPage(HttpServletRequest req, HttpServletResponse resp, OutputStream out) throws Exception {
        EditUserPageAction action = new EditUserPageAction();

        ActionHarness actionHarness = prepareAction(action, req, resp, out);
        String username = urlMatcher.extractUsernameForEdit(req.getPathInfo());
        actionHarness.setValue(USERNAME, username);

        action.execute();
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp, OutputStream out) throws Exception {
        DeleteUserAction action = new DeleteUserAction();

        ActionHarness actionHarness = prepareAction(action, req, resp, out);
        String username = urlMatcher.extractUsernameForDelete(req.getPathInfo());
        actionHarness.setValue(USERNAME, username);

        action.execute();
    }

    private void saveUser(HttpServletRequest req, HttpServletResponse resp, OutputStream out) throws Exception {
        SaveUserAction action = new SaveUserAction();

        ActionHarness actionHarness = prepareAction(action, req, resp, out);
        actionHarness.setValue(VALIDATOR, validator);

        action.execute();
    }

    private ActionHarness prepareAction(AbstractPageAction action, HttpServletRequest req,
                                        HttpServletResponse resp, OutputStream out) throws Exception {
        ActionHarness actionHarness = new ActionHarness(action);

        actionHarness.setValue("parametersMap", req.getParameterMap());
        actionHarness.setValue("outputStream", out);

        actionHarness.setValue("currentUsername", req.getUserPrincipal().getName());
        actionHarness.setValue("ldapUserService", ldapUserService);
        actionHarness.setValue("regionService", regionService);
        actionHarness.setValue("templateEngine", templateEngine);

        WebContext thCtx = new WebContext(req, resp, getServletContext());
        actionHarness.setValue("thymeleafContext", thCtx);

        return actionHarness;
    }

    private void writeResposne(HttpServletResponse resp, ByteArrayOutputStream out) throws IOException {
        resp.setContentLength(out.size());
        resp.setContentType(MediaType.TEXT_HTML);

        IOUtils.write(out.toByteArray(), resp.getOutputStream());
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

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void setUrlMatcher(UrlMatcher urlMatcher) {
        this.urlMatcher = urlMatcher;
    }
}

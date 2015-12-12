package org.motechproject.nms.ldapbrowser.ldap.web;

import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.ldap.UsersQuery;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.motechproject.nms.ldapbrowser.support.web.DtData;
import org.motechproject.nms.ldapbrowser.support.web.DtRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/nms-users/api")
public class LdapAjaxService {

    private LdapUserService ldapUserService;
    private RegionService regionService;

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DtData<LdapUser> getUsers(DtRequest dtRequest) {
        UsersQuery query = new UsersQuery(dtRequest.getStart(), dtRequest.getLength());

        List<LdapUser> users = ldapUserService.getUsers(query);

        return new DtData<>(users, users.size());
    }

    @GET
    @Path("/districts/{stateName}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistrictNamesForNewUser(@PathParam("stateName") String stateName) {
        return regionService.availableDistrictNames(stateName);
    }

    public void setLdapUserService(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }
}

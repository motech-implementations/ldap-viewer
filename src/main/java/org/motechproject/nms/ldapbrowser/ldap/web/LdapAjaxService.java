package org.motechproject.nms.ldapbrowser.ldap.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.nms.ldapbrowser.ldap.DistrictInfo;
import org.motechproject.nms.ldapbrowser.ldap.LdapUser;
import org.motechproject.nms.ldapbrowser.ldap.LdapUserService;
import org.motechproject.nms.ldapbrowser.ldap.UsersQuery;
import org.motechproject.nms.ldapbrowser.region.RegionService;
import org.motechproject.nms.ldapbrowser.support.web.DtData;
import org.motechproject.nms.ldapbrowser.support.web.DtRequest;
import org.pentaho.platform.web.http.api.resources.JaxbList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/nms-users/api")
public class LdapAjaxService {

    private static final Logger LOG = LoggerFactory.getLogger(LdapAjaxService.class);

    private LdapUserService ldapUserService;
    private RegionService regionService;

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsers(DtRequest dtRequest) {
        UsersQuery query = new UsersQuery(dtRequest.getStart(), dtRequest.getLength());

        List<LdapUser> users = ldapUserService.getUsers(query);
        DtData<LdapUser> dtData = new DtData<>(users, users.size());
        ObjectMapper mapper = new ObjectMapper();
        String usersResponse = null;
        try {
            usersResponse = mapper.writeValueAsString(dtData);
        } catch (IOException e) {
            LOG.error("The serialization of the users object to JSON failed.", e);
        }

        return usersResponse;
    }

    @GET
    @Path("/districts/{stateName}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public JaxbList<String> getDistrictNamesForNewUser(@PathParam("stateName") String stateName) {
        return new JaxbList<>(regionService.availableDistrictNames(stateName));
    }

    @GET
    @Path("/districts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDistrictNames() {
        List<DistrictInfo> districts = regionService.allAvailableDistrictInfo();
        DtData<DistrictInfo> dtData = new DtData<>(districts, districts.size());

        return Response.ok(dtData).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/states")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public JaxbList<String> getStateNamesForNewUser() {
        return new JaxbList<>(regionService.availableStateNames());
    }

    public void setLdapUserService(LdapUserService ldapUserService) {
        this.ldapUserService = ldapUserService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }
}

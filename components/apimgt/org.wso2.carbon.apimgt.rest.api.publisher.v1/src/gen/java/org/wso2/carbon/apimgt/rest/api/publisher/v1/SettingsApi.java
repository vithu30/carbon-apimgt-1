package org.wso2.carbon.apimgt.rest.api.publisher.v1;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.EnvironmentListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.SettingsDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.SettingsApiService;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.impl.SettingsApiServiceImpl;
import org.wso2.carbon.apimgt.api.APIManagementException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;


@Path("/settings")
@Api(description = "the settings API")



public class SettingsApi  {
    @Context MessageContext securityContext;
    SettingsApiService delegate = new SettingsApiServiceImpl();


    @GET
    @Path("/gateway-environments")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Get All Gateway Environments", notes = "This operation can be used to retrieve the list of gateway environments available. ", response = EnvironmentListDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "", description = "")        })    }, tags={ "Settings" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200,
                    message = "OK. Environment list is returned. ",
                    response = EnvironmentListDTO.class),
        @ApiResponse(code = 304,
                    message = "Not Modified. Empty body because the client has already the latest version of the requested resource (Will be supported in future). ",
                    response = Void.class),
        @ApiResponse(code = 404,
                    message = "Not Found. The specified resource does not exist.",
                    response = ErrorDTO.class) })
    public Response settingsGatewayEnvironmentsGet( @NotNull 
        @ApiParam(value = "**API ID** consisting of the **UUID** of the API. The combination of the provider of the API, name of the API and the version is also accepted as a valid API I. Should be formatted as **provider-name-version**. ",required=true) 
            @QueryParam("apiId") String apiId
) throws APIManagementException{
        return delegate.settingsGatewayEnvironmentsGet(apiId, securityContext);
    }

    @GET
    
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retreive Publisher Settings", notes = "Retreive publisher settings ", response = SettingsDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "", description = "")        })    }, tags={ "Settings" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200,
                    message = "OK. Settings returned ",
                    response = SettingsDTO.class),
        @ApiResponse(code = 404,
                    message = "Not Found. The specified resource does not exist.",
                    response = ErrorDTO.class) })
    public Response settingsGet() throws APIManagementException{
        return delegate.settingsGet(securityContext);
    }
}

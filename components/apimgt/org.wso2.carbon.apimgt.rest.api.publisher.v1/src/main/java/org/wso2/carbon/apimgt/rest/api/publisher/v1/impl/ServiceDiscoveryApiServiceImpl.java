package org.wso2.carbon.apimgt.rest.api.publisher.v1.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.*;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.ServiceDiscoveriesInfoDTO;

import java.util.List;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public class ServiceDiscoveryApiServiceImpl implements ServiceDiscoveryApiService {
      public Response serviceDiscoveryEndpointsTypeGet(String type, MessageContext messageContext) {
          ServiceDiscoveriesInfoDTO serviceListDTO = new ServiceDiscoveriesInfoDTO();
          JSONArray serviceDiscovery;
          APIManagerConfiguration configuration = ServiceReferenceHolder.getInstance().getAPIManagerConfigurationService()
                  .getAPIManagerConfiguration();
          serviceDiscovery = configuration.getServiceDiscoveryConf();
          Map<String, String> serviceDiscoveryConfig = new ObjectMapper().convertValue(serviceDiscovery.get(0), Map.class);
          System.out.println(serviceDiscoveryConfig.size() + "------");

      // do some magic!
      return Response.ok().entity(APIUtil.getServices(serviceDiscoveryConfig)).build();
  }
}

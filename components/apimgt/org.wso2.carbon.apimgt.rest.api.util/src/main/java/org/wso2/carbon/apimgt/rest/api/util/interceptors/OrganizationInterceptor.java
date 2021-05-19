/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.apimgt.rest.api.util.interceptors;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.common.RestApiCommonUtil;
import org.wso2.carbon.apimgt.rest.api.common.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.exception.BadRequestException;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;
import org.wso2.carbon.user.api.UserStoreException;

public class OrganizationInterceptor extends AbstractPhaseInterceptor {

    private static final Log logger = LogFactory.getLog(OrganizationInterceptor.class);

    public OrganizationInterceptor() {
        // We will use PRE_INVOKE phase as we need to process message before hit actual
        // service
        super(Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        String queryString = (String) message.get(Message.QUERY_STRING);
        String organizationId = null;
        if (queryString != null) {
            String[] queries = queryString.split("&");
            if (queries != null) {
                for (int i = 0; i < queries.length; i++) {
                    if (queries[i].startsWith("organizationId")) {
                        organizationId = queries[i].split("=")[1];
                        message.put(RestApiConstants.ORGANIZATION, organizationId);
                        break;
                    }
                }
            }
        }
        if (organizationId == null) {
            ArrayList requestedTenantDomain = (ArrayList) ((TreeMap) (message.get(Message.PROTOCOL_HEADERS)))
                    .get(RestApiConstants.HEADER_X_WSO2_TENANT);
            String tenantDomain = null;
            if (requestedTenantDomain != null) {
                tenantDomain = RestApiUtil.getRequestedTenantDomain(requestedTenantDomain.get(0).toString());
                try {
                    if (!APIUtil.isTenantAvailable(tenantDomain)) {
                        RestApiUtil.handleBadRequest("Provided tenant domain '" + tenantDomain + "' is invalid",
                                901300L, logger);

                    }
                } catch (UserStoreException | BadRequestException e) {
                    String errorMessage = "Error while checking availability of tenant " + tenantDomain;
                    RestApiUtil.handleInternalServerError(errorMessage, e, logger);
                }
            }
            if (StringUtils.isEmpty(tenantDomain)) {
                tenantDomain = RestApiCommonUtil.getLoggedInUserTenantDomain();
            }

            message.put(RestApiConstants.ORGANIZATION, tenantDomain);
        }

        logger.debug("Organization :" + message.get(RestApiConstants.ORGANIZATION));
    }
}

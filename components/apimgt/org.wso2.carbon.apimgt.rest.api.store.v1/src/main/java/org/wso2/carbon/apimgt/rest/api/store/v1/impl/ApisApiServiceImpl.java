/*
 * Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.rest.api.store.v1.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.api.APIConsumer;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIRating;
import org.wso2.carbon.apimgt.api.model.Documentation;
import org.wso2.carbon.apimgt.api.model.ResourceFile;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.impl.APIClientGenerationException;
import org.wso2.carbon.apimgt.impl.APIClientGenerationManager;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.store.v1.ApisApiService;

import org.wso2.carbon.apimgt.rest.api.store.v1.dto.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wso2.carbon.apimgt.rest.api.store.v1.mappings.APIMappingUtil;
import org.wso2.carbon.apimgt.rest.api.store.v1.mappings.DocumentationMappingUtil;
import org.wso2.carbon.apimgt.rest.api.util.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestAPIStoreUtils;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ApisApiServiceImpl implements ApisApiService {

    private static final Log log = LogFactory.getLog(ApisApiServiceImpl.class);

    @Override
    public Response apisGet(Integer limit, Integer offset, String xWSO2Tenant, String query, String ifNoneMatch,
            MessageContext messageContext) {
        limit = limit != null ? limit : RestApiConstants.PAGINATION_LIMIT_DEFAULT;
        offset = offset != null ? offset : RestApiConstants.PAGINATION_OFFSET_DEFAULT;
        query = query == null ? "" : query;
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        APIListDTO apiListDTO = new APIListDTO();
        try {
            String username = RestApiUtil.getLoggedInUsername();
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);

            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            String newSearchQuery = APIUtil.constructNewSearchQuery(query);

            //revert content search back to normal search by name to avoid doc result complexity and to comply with REST api practices
            if (newSearchQuery.startsWith(APIConstants.CONTENT_SEARCH_TYPE_PREFIX + "=")) {
                newSearchQuery = newSearchQuery
                        .replace(APIConstants.CONTENT_SEARCH_TYPE_PREFIX + "=", APIConstants.NAME_TYPE_PREFIX + "=");
            }
            // Append LC state query criteria if the search is not doc or subcontext
            // based
            if (!APIConstants.DOCUMENTATION_SEARCH_TYPE_PREFIX_WITH_EQUALS.startsWith(newSearchQuery) &&
                    !APIConstants.SUBCONTEXT_SEARCH_TYPE_PREFIX.startsWith(newSearchQuery)) {
                boolean displayAPIsWithMultipleStatus = APIUtil.isAllowDisplayAPIsWithMultipleStatus();

                String[] statusList = { APIConstants.PUBLISHED, APIConstants.PROTOTYPED };
                if (displayAPIsWithMultipleStatus) {
                    statusList = new String[] { APIConstants.PUBLISHED, APIConstants.PROTOTYPED,
                            APIConstants.DEPRECATED };
                }

                String lcCriteria = APIConstants.LCSTATE_SEARCH_TYPE_KEY;
                lcCriteria = lcCriteria + APIUtil.getORBasedSearchCriteria(statusList);

                newSearchQuery = newSearchQuery + APIConstants.SEARCH_AND_TAG + lcCriteria;
            }

            Map allMatchedApisMap = apiConsumer
                    .searchPaginatedAPIs(newSearchQuery, requestedTenantDomain, offset, limit, false);
            Set<API> sortedSet = (Set<API>) allMatchedApisMap.get("apis"); // This is a SortedSet
            ArrayList<API> allMatchedApis = new ArrayList<>(sortedSet);

            apiListDTO = APIMappingUtil.fromAPIListToDTO(allMatchedApis);
            //Add pagination section in the response
            Object totalLength = allMatchedApisMap.get("length");
            Integer totalAvailableAPis = 0;
            if (totalLength != null) {
                totalAvailableAPis = (Integer) totalLength;
            }

            APIMappingUtil
                    .setPaginationParams(apiListDTO, query, offset, limit, totalAvailableAPis);

            return Response.ok().entity(apiListDTO).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.rootCauseMessageMatches(e, "start index seems to be greater than the limit count")) {
                //this is not an error of the user as he does not know the total number of apis available. Thus sends 
                //  an empty response
                apiListDTO.setCount(0);
                apiListDTO.setPagination(new PaginationDTO());
                return Response.ok().entity(apiListDTO).build();
            } else {
                String errorMessage = "Error while retrieving APIs";
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdGet(String apiId, String xWSO2Tenant, String ifNoneMatch, MessageContext messageContext) {
        return Response.ok().entity(getAPIByAPIId(apiId, xWSO2Tenant)).build();
    }

    @Override
    public Response apisApiIdGraphqlSchemaGet(String apiId, String ifNoneMatch, String xWSO2Tenant, MessageContext messageContext) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();
            APIIdentifier apiIdentifier = APIMappingUtil.getAPIIdentifierFromUUID(apiId,
                    requestedTenantDomain);
            String graphQLSchema = apiConsumer.getGraphqlSchema(apiIdentifier);
            return Response.ok().entity(graphQLSchema).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                String errorMessage = "Error while retrieving API : " + apiId;
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }
        }
        return null;
    }

    @Override
    public Response apisApiIdCommentsCommentIdDelete(String commentId, String apiId, String ifMatch, MessageContext messageContext) {
        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response apisApiIdCommentsCommentIdGet(String commentId, String apiId, String ifNoneMatch, MessageContext messageContext) {
        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response apisApiIdCommentsCommentIdPut(String commentId, String apiId, CommentDTO body, String ifMatch, MessageContext messageContext) {
        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response apisApiIdCommentsGet(String apiId, Integer limit, Integer offset, MessageContext messageContext) {
        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response apisApiIdCommentsPost(String apiId, CommentDTO body, MessageContext messageContext) {
        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response apisApiIdDocumentsDocumentIdContentGet(String apiId, String documentId, String xWSO2Tenant,
            String ifNoneMatch, MessageContext messageContext) {
        Documentation documentation;
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            String username = RestApiUtil.getLoggedInUsername();
            APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();

            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }

            //this will fail if user does not have access to the API or the API does not exist
            APIIdentifier apiIdentifier = APIMappingUtil.getAPIIdentifierFromUUID(apiId, requestedTenantDomain);

            documentation = apiConsumer.getDocumentation(documentId, requestedTenantDomain);
            if (documentation == null) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_DOCUMENTATION, documentId, log);
                return null;
            }

            if (documentation.getSourceType().equals(Documentation.DocumentSourceType.FILE)) {
                String resource = documentation.getFilePath();
                Map<String, Object> docResourceMap = APIUtil.getDocument(username, resource, requestedTenantDomain);
                Object fileDataStream = docResourceMap.get(APIConstants.DOCUMENTATION_RESOURCE_MAP_DATA);
                Object contentType = docResourceMap.get(APIConstants.DOCUMENTATION_RESOURCE_MAP_CONTENT_TYPE);
                contentType = contentType == null ? RestApiConstants.APPLICATION_OCTET_STREAM : contentType;
                String name = docResourceMap.get(APIConstants.DOCUMENTATION_RESOURCE_MAP_NAME).toString();
                return Response.ok(fileDataStream)
                        .header(RestApiConstants.HEADER_CONTENT_TYPE, contentType)
                        .header(RestApiConstants.HEADER_CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                        .build();
            } else if (documentation.getSourceType().equals(Documentation.DocumentSourceType.INLINE) || documentation.getSourceType().equals(Documentation.DocumentSourceType.MARKDOWN)) {
                String content = apiConsumer.getDocumentationContent(apiIdentifier, documentation.getName());
                return Response.ok(content)
                        .header(RestApiConstants.HEADER_CONTENT_TYPE, APIConstants.DOCUMENTATION_INLINE_CONTENT_TYPE)
                        .build();
            } else if (documentation.getSourceType().equals(Documentation.DocumentSourceType.URL)) {
                String sourceUrl = documentation.getSourceUrl();
                return Response.seeOther(new URI(sourceUrl)).build();
            }
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                String errorMessage = "Error while retrieving document " + documentId + " of the API " + apiId;
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }
        } catch (URISyntaxException e) {
            String errorMessage = "Error while retrieving source URI location of " + documentId;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdDocumentsDocumentIdGet(String apiId, String documentId, String xWSO2Tenant,
            String ifModifiedSince, MessageContext messageContext) {
        Documentation documentation;
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            String username = RestApiUtil.getLoggedInUsername();
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);

            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }

            if (!RestAPIStoreUtils.isUserAccessAllowedForAPIByUUID(apiId, requestedTenantDomain)) {
                RestApiUtil.handleAuthorizationFailure(RestApiConstants.RESOURCE_API, apiId, log);
            }

            documentation = apiConsumer.getDocumentation(documentId, requestedTenantDomain);
            if (null != documentation) {
                DocumentDTO documentDTO = DocumentationMappingUtil.fromDocumentationToDTO(documentation);
                return Response.ok().entity(documentDTO).build();
            } else {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_DOCUMENTATION, documentId, log);
            }
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                RestApiUtil.handleInternalServerError("Error while getting API " + apiId, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdDocumentsGet(String apiId, Integer limit, Integer offset, String xWSO2Tenant,
            String ifNoneMatch, MessageContext messageContext) {
        //pre-processing
        //setting default limit and offset values if they are not set
        limit = limit != null ? limit : RestApiConstants.PAGINATION_LIMIT_DEFAULT;
        offset = offset != null ? offset : RestApiConstants.PAGINATION_OFFSET_DEFAULT;

        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            String username = RestApiUtil.getLoggedInUsername();
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);

            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }

            //this will fail if user doesn't have access to the API or the API does not exist
            APIIdentifier apiIdentifier = APIMappingUtil.getAPIIdentifierFromUUID(apiId, requestedTenantDomain);

            List<Documentation> documentationList = apiConsumer.getAllDocumentation(apiIdentifier, username);
            DocumentListDTO documentListDTO = DocumentationMappingUtil
                    .fromDocumentationListToDTO(documentationList, offset, limit);

            //todo : set total count properly
            DocumentationMappingUtil
                    .setPaginationParams(documentListDTO, apiId, offset, limit, documentationList.size());
            return Response.ok().entity(documentListDTO).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                RestApiUtil.handleInternalServerError("Error while getting API " + apiId, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        } /*catch (UnsupportedEncodingException e) {
            String errorMessage = "Error while Decoding apiId" + apiId;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }*/
        return null;
    }

    @Override
    public Response apisApiIdRatingsGet(String apiId, Integer limit, Integer offset, String xWSO2Tenant,
            MessageContext messageContext) {
        //pre-processing
        //setting default limit and offset values if they are not set
        limit = limit != null ? limit : RestApiConstants.PAGINATION_LIMIT_DEFAULT;
        offset = offset != null ? offset : RestApiConstants.PAGINATION_OFFSET_DEFAULT;
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            String username = RestApiUtil.getLoggedInUsername();
            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);
            API api = apiConsumer.getLightweightAPIByUUID(apiId, requestedTenantDomain);
            APIIdentifier apiIdentifier = api.getId();
            float avgRating = apiConsumer.getAverageAPIRating(apiIdentifier);
            int userRating = 0;
            if (!APIConstants.WSO2_ANONYMOUS_USER.equals(username)) {
                userRating = apiConsumer.getUserRating(apiIdentifier, username);
            }
            List<RatingDTO> ratingDTOList = new ArrayList<>();
            JSONArray array = apiConsumer.getAPIRatings(apiIdentifier);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                RatingDTO ratingDTO = APIMappingUtil.fromJsonToRatingDTO(obj);
                ratingDTO.setApiId(apiId);
                ratingDTOList.add(ratingDTO);
            }
            RatingListDTO ratingListDTO = APIMappingUtil.fromRatingListToDTO(ratingDTOList, offset, limit);
            ratingListDTO.setUserRating(userRating);
            ratingListDTO.setAvgRating(String.valueOf(avgRating));
            APIMappingUtil.setRatingPaginationParams(ratingListDTO, apiId, offset, limit, ratingDTOList.size());
            return Response.ok().entity(ratingListDTO).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                RestApiUtil.handleInternalServerError("Error while retrieving ratings for API " + apiId, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    /**
     * Rest api implementation to downloading the client sdk for given api in given sdk language.
     *
     * @param apiId : The id of the api.
     * @param language : Preferred sdk language.
     * @param messageContext : messageContext
     * @return : The sdk as a zip archive.
     */
    @Override
    public Response apisApiIdSdksLanguageGet(String apiId, String language, MessageContext messageContext) {

        if (StringUtils.isEmpty(apiId) || StringUtils.isEmpty(language)) {
            String message = "Error generating the SDK. API id or language should not be empty";
            RestApiUtil.handleBadRequest(message, log);
        }
        String tenant = RestApiUtil.getLoggedInUserTenantDomain();
        APIDTO api = getAPIByAPIId(apiId, tenant);
        APIClientGenerationManager apiClientGenerationManager = new APIClientGenerationManager();
        Map<String, String> sdkArtifacts;
        if (api != null) {
            String apiProvider = api.getProvider();
            try {
                sdkArtifacts = apiClientGenerationManager.generateSDK(language, api.getName(),
                        api.getVersion(), apiProvider);
                //Create the sdk response.
                File sdkFile = new File(sdkArtifacts.get("zipFilePath"));
                return Response.ok(sdkFile, MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition",
                        "attachment; filename=\"" + sdkArtifacts.get("zipFileName") + "\"" ).build();
            } catch (APIClientGenerationException e) {
                String message = "Error generating client sdk for api: " + api.getName() + " for language: " + language;
                RestApiUtil.handleInternalServerError(message, e, log);
            }
        } 
        String message = "Could not find an API for ID " + apiId;
        RestApiUtil.handleResourceNotFoundError(message, log);
        return null;
    }

    /**
     * Retrieves the swagger document of an API
     * 
     * @param apiId API identifier
     * @param labelName name of the gateway label
     * @param environmentName name of the gateway environment
     * @param ifNoneMatch If-None-Match header value
     * @param xWSO2Tenant requested tenant domain for cross tenant invocations
     * @param messageContext CXF message context
     * @return Swagger document of the API for the given label or gateway environment
     */
    @Override
    public Response apisApiIdSwaggerGet(String apiId, String labelName, String environmentName,
            String ifNoneMatch, String xWSO2Tenant, MessageContext messageContext) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();

            if (StringUtils.isNotEmpty(labelName) && StringUtils.isNotEmpty(environmentName)) {
                RestApiUtil.handleBadRequest("Only one of 'labelName' or 'environmentName' can be provided", log);
            }

            API api = apiConsumer.getLightweightAPIByUUID(apiId, requestedTenantDomain);

            //gets the first available environment if neither label nor environment is not provided
            if (StringUtils.isEmpty(labelName) && StringUtils.isEmpty(environmentName)) {
                environmentName = api.getEnvironments().iterator().next();
            }

            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            
            String apiSwagger = null;
            if (StringUtils.isNotEmpty(environmentName)) {
                apiSwagger = apiConsumer.getOpenAPIDefinitionForEnvironment(api.getId(), environmentName);
            } else if (StringUtils.isNotEmpty(labelName)) {
                apiSwagger = apiConsumer.getOpenAPIDefinitionForLabel(api.getId(), labelName);
            }

            return Response.ok().entity(apiSwagger).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                String errorMessage = "Error while retrieving API : " + apiId;
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdThumbnailGet(String apiId, String xWSO2Tenant, String ifNoneMatch, MessageContext messageContext) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();
            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            //this will fail if user does not have access to the API or the API does not exist
            APIIdentifier apiIdentifier = APIMappingUtil.getAPIIdentifierFromUUID(apiId, requestedTenantDomain);
            ResourceFile thumbnailResource = apiConsumer.getIcon(apiIdentifier);

            if (thumbnailResource != null) {
                return Response
                        .ok(thumbnailResource.getContent(), MediaType.valueOf(thumbnailResource.getContentType()))
                        .build();
            } else {
                return Response.noContent().build();
            }
        } catch (APIManagementException e) {
            //Auth failure occurs when cross tenant accessing APIs. Sends 404, since we don't need to expose the
            // existence of the resource
            if (RestApiUtil.isDueToResourceNotFound(e) || RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                String errorMessage = "Error while retrieving thumbnail of API : " + apiId;
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdUserRatingPut(String apiId, RatingDTO body, String xWSO2Tenant,
            MessageContext messageContext) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            int rating = 0;
            String username = RestApiUtil.getLoggedInUsername();
            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);
            //this will fail if user doesn't have access to the API or the API does not exist
            API api = apiConsumer.getLightweightAPIByUUID(apiId, requestedTenantDomain);
            APIIdentifier apiIdentifier = api.getId();
            if (body != null) {
                rating = body.getRating();
            }
            switch (rating) {
                //Below case 0[Rate 0] - is to remove ratings from a user
                case 0: {
                    apiConsumer.rateAPI(apiIdentifier, APIRating.RATING_ZERO, username);
                    break;
                }
                case 1: {
                    apiConsumer.rateAPI(apiIdentifier, APIRating.RATING_ONE, username);
                    break;
                }
                case 2: {
                    apiConsumer.rateAPI(apiIdentifier, APIRating.RATING_TWO, username);
                    break;
                }
                case 3: {
                    apiConsumer.rateAPI(apiIdentifier, APIRating.RATING_THREE, username);
                    break;
                }
                case 4: {
                    apiConsumer.rateAPI(apiIdentifier, APIRating.RATING_FOUR, username);
                    break;
                }
                case 5: {
                    apiConsumer.rateAPI(apiIdentifier, APIRating.RATING_FIVE, username);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Can't handle " + rating);
                }
            }
            JSONObject obj = apiConsumer.getUserRatingInfo(apiIdentifier, username);
            RatingDTO ratingDTO = new RatingDTO();
            if (obj != null && !obj.isEmpty()) {
                ratingDTO = APIMappingUtil.fromJsonToRatingDTO(obj);
                ratingDTO.setApiId(apiId);
            }
            return Response.ok().entity(ratingDTO).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                RestApiUtil
                        .handleInternalServerError("Error while adding/updating user rating for API " + apiId, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdUserRatingGet(String apiId, String xWSO2Tenant, String ifNoneMatch,
            MessageContext messageContext) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            String username = RestApiUtil.getLoggedInUsername();
            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);
            //this will fail if user doesn't have access to the API or the API does not exist
            API api = apiConsumer.getLightweightAPIByUUID(apiId, requestedTenantDomain);
            APIIdentifier apiIdentifier = api.getId();
            JSONObject obj = apiConsumer.getUserRatingInfo(apiIdentifier, username);
            RatingDTO ratingDTO = new RatingDTO();
            if (obj != null && !obj.isEmpty()) {
                ratingDTO = APIMappingUtil.fromJsonToRatingDTO(obj);
                ratingDTO.setApiId(apiId);
            }
            return Response.ok().entity(ratingDTO).build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                RestApiUtil.handleInternalServerError("Error while retrieving user rating for API " + apiId, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdUserRatingDelete(String apiId, String xWSO2Tenant, String ifMatch,
            MessageContext messageContext) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            String username = RestApiUtil.getLoggedInUsername();
            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }
            APIConsumer apiConsumer = RestApiUtil.getConsumer(username);
            //this will fail if user doesn't have access to the API or the API does not exist
            API api = apiConsumer.getLightweightAPIByUUID(apiId, requestedTenantDomain);
            APIIdentifier apiIdentifier = api.getId();
            apiConsumer.removeAPIRating(apiIdentifier, username);
            return Response.ok().build();
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(
                        RestApiConstants.RESOURCE_RATING + " for " + RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                RestApiUtil.handleInternalServerError("Error while deleting user rating for API " + apiId, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }

    @Override
    public Response apisApiIdWsdlGet(String apiId, String ifNoneMatch, String xWSO2Tenant, MessageContext messageContext) {
        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response apisApiIdSubscriptionPoliciesGet(String apiId, String ifNoneMatch, String xWSO2Tenant,
                                                     MessageContext messageContext) {
        APIDTO apiInfo = getAPIByAPIId(apiId, xWSO2Tenant);
        List<Tier> availableThrottlingPolicyList = new ThrottlingPoliciesApiServiceImpl()
                .getThrottlingPolicyList(ThrottlingPolicyDTO.PolicyLevelEnum.SUBSCRIPTION.toString(), xWSO2Tenant);

        if (apiInfo != null ) {
            List<String> apiTiers = apiInfo.getTiers();
            if (apiTiers != null && !apiTiers.isEmpty()) {
                List<Tier> apiThrottlingPolicies = new ArrayList<>();
                for (Tier policy : availableThrottlingPolicyList) {
                    if (apiTiers.contains(policy.getName())) {
                        apiThrottlingPolicies.add(policy);
                    }
                }
                return Response.ok().entity(apiThrottlingPolicies).build();
            }
        }
        return null;
    }

    private APIDTO getAPIByAPIId(String apiId, String xWSO2Tenant) {
        String requestedTenantDomain = RestApiUtil.getRequestedTenantDomain(xWSO2Tenant);
        try {
            APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();

            if (!RestApiUtil.isTenantAvailable(requestedTenantDomain)) {
                RestApiUtil.handleBadRequest("Provided tenant domain '" + xWSO2Tenant + "' is invalid", log);
            }

            API api = apiConsumer.getAPIbyUUID(apiId, requestedTenantDomain);
            return APIMappingUtil.fromAPItoDTO(api,requestedTenantDomain);
        } catch (APIManagementException e) {
            if (RestApiUtil.isDueToAuthorizationFailure(e)) {
                RestApiUtil.handleAuthorizationFailure(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else if (RestApiUtil.isDueToResourceNotFound(e)) {
                RestApiUtil.handleResourceNotFoundError(RestApiConstants.RESOURCE_API, apiId, e, log);
            } else {
                String errorMessage = "Error while retrieving API : " + apiId;
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }
        } catch (UserStoreException e) {
            String errorMessage = "Error while checking availability of tenant " + requestedTenantDomain;
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }
}

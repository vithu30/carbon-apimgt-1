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

package org.wso2.carbon.apimgt.rest.api.store.v1.mappings;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.api.APIConsumer;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIProduct;
import org.wso2.carbon.apimgt.api.model.Label;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIOperationsDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIProductBusinessInformationDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIProductDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIProductInfoDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIProductListDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIBusinessInformationDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIDefaultVersionURLsDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIEndpointURLsDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIInfoDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIListDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.APIURLsDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.LabelDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.PaginationDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.RatingDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.RatingListDTO;
import org.wso2.carbon.apimgt.rest.api.util.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class APIMappingUtil {

    public static APIDTO fromAPItoDTO(API model, String tenantDomain) throws APIManagementException {

        APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();
        APIDTO dto = new APIDTO();
        dto.setName(model.getId().getApiName());
        dto.setVersion(model.getId().getVersion());
        String providerName = model.getId().getProviderName();
        dto.setProvider(APIUtil.replaceEmailDomainBack(providerName));
        dto.setId(model.getUUID());
        dto.setContext(model.getContext());
        dto.setDescription(model.getDescription());
        dto.setIsDefaultVersion(model.isDefaultVersion());
        dto.setLifeCycleStatus(model.getStatus());
        dto.setType(model.getType());
        dto.setAvgRating(String.valueOf(model.getRating()));

        /* todo: created and last updated times
        if (null != model.getLastUpdated()) {
            Date lastUpdateDate = model.getLastUpdated();
            Timestamp timeStamp = new Timestamp(lastUpdateDate.getTime());
            dto.setLastUpdatedTime(String.valueOf(timeStamp));
        }

        String createdTimeStamp = model.getCreatedTime();
        if (null != createdTimeStamp) {
            Date date = new Date(Long.valueOf(createdTimeStamp));
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dateFormatted = formatter.format(date);
            dto.setCreatedTime(dateFormatted);
        } */

        //Get Swagger definition which has URL templates, scopes and resource details
        String apiSwaggerDefinition = null;

        if (!APIConstants.APITransportType.WS.toString().equals(model.getType())) {
            apiSwaggerDefinition = apiConsumer.getOpenAPIDefinition(model.getId());
        }
        dto.setApiDefinition(apiSwaggerDefinition);

        if (APIConstants.APITransportType.GRAPHQL.toString().equals(model.getType())) {
            List<APIOperationsDTO> operationList = new ArrayList<>();
            for (URITemplate template : model.getUriTemplates()) {
                APIOperationsDTO operation = new APIOperationsDTO();
                operation.setTarget(template.getUriTemplate());
                operation.setVerb(template.getHTTPVerb());
                operationList.add(operation);
            }
            dto.setOperations(operationList);
        }

        Set<String> apiTags = model.getTags();
        List<String> tagsToReturn = new ArrayList<>();
        tagsToReturn.addAll(apiTags);
        dto.setTags(tagsToReturn);

        Set<org.wso2.carbon.apimgt.api.model.Tier> apiTiers = model.getAvailableTiers();
        List<String> tiersToReturn = new ArrayList<>();
        for (org.wso2.carbon.apimgt.api.model.Tier tier : apiTiers) {
            tiersToReturn.add(tier.getName());
        }
        dto.setTiers(tiersToReturn);

        dto.setTransport(Arrays.asList(model.getTransports().split(",")));

        dto.setEndpointURLs(extractEnpointURLs(model, tenantDomain));

        APIBusinessInformationDTO apiBusinessInformationDTO = new APIBusinessInformationDTO();
        apiBusinessInformationDTO.setBusinessOwner(model.getBusinessOwner());
        apiBusinessInformationDTO.setBusinessOwnerEmail(model.getBusinessOwnerEmail());
        apiBusinessInformationDTO.setTechnicalOwner(model.getTechnicalOwner());
        apiBusinessInformationDTO.setTechnicalOwnerEmail(model.getTechnicalOwnerEmail());
        dto.setBusinessInformation(apiBusinessInformationDTO);

        if (!StringUtils.isBlank(model.getThumbnailUrl())) {
            dto.setHasThumbnail(true);
        }

        if (model.getAdditionalProperties() != null) {
            JSONObject additionalProperties = model.getAdditionalProperties();
            Map<String, String> additionalPropertiesMap = new HashMap<>();
            for (Object propertyKey : additionalProperties.keySet()) {
                String key = (String) propertyKey;
                additionalPropertiesMap.put(key, (String) additionalProperties.get(key));
            }
            dto.setAdditionalProperties(additionalPropertiesMap);
        }

        dto.setWsdlUri(model.getWsdlUrl());


        if (model.getGatewayLabels() != null) {
            dto.setLabels(getLabelDetails(model.getGatewayLabels(), model.getContext()));
        }

        if (model.getEnvironmentList() != null) {
            List<String> environmentListToReturn = new ArrayList<>();
            environmentListToReturn.addAll(model.getEnvironmentList());
            dto.setEnvironmentList(environmentListToReturn);
        }

        dto.setAuthorizationHeader(model.getAuthorizationHeader());
        if (model.getApiSecurity() != null) {
            dto.setSecurityScheme(Arrays.asList(model.getApiSecurity().split(",")));
        }
        return dto;
    }

    /**
     * Returns an API with minimal info given the uuid.
     *
     * @param apiUUID                 API uuid
     * @param requestedTenantDomain tenant domain of the API
     * @return API which represents the given id
     * @throws APIManagementException
     */
    public static API getAPIInfoFromUUID(String apiUUID, String requestedTenantDomain)
            throws APIManagementException {
        API api;
        APIProvider apiProvider = RestApiUtil.getLoggedInUserProvider();
        api = apiProvider.getLightweightAPIByUUID(apiUUID, requestedTenantDomain);
        return api;
    }

    /**
     * Returns the APIIdentifier given the uuid
     *
     * @param apiId                 API uuid
     * @param requestedTenantDomain tenant domain of the API
     * @return APIIdentifier which represents the given id
     * @throws APIManagementException
     */
    public static APIIdentifier getAPIIdentifierFromUUID(String apiId, String requestedTenantDomain)
            throws APIManagementException {
        return getAPIInfoFromUUID(apiId, requestedTenantDomain).getId();
    }

    /**
     * Sets pagination urls for a APIListDTO object given pagination parameters and url parameters
     *
     * @param apiListDTO APIListDTO object to which pagination urls need to be set
     * @param query      query parameter
     * @param offset     starting index
     * @param limit      max number of returned objects
     * @param size       max offset
     */
    public static void setPaginationParams(APIListDTO apiListDTO, String query, int offset, int limit, int size) {
        Map<String, Integer> paginatedParams = RestApiUtil.getPaginationParams(offset, limit, size);

        String paginatedPrevious = "";
        String paginatedNext = "";

        if (paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) != null) {
            paginatedPrevious = RestApiUtil
                    .getAPIPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT), query);
        }

        if (paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET) != null) {
            paginatedNext = RestApiUtil
                    .getAPIPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_NEXT_LIMIT), query);
        }

        PaginationDTO paginationDTO = CommonMappingUtil
                .getPaginationDTO(limit, offset, size, paginatedNext, paginatedPrevious);
        apiListDTO.setPagination(paginationDTO);
    }

    /**
     * Converts an API Set object into corresponding REST API DTO
     *
     * @param apiSet Set of API objects
     * @return APIListDTO object
     */
    public static APIListDTO fromAPISetToDTO(Set<API> apiSet) {
        APIListDTO apiListDTO = new APIListDTO();
        List<APIInfoDTO> apiInfoDTOs = apiListDTO.getList();
        if (apiInfoDTOs == null) {
            apiInfoDTOs = new ArrayList<>();
            apiListDTO.setList(apiInfoDTOs);
        }
        for (API api : apiSet) {
            apiInfoDTOs.add(fromAPIToInfoDTO(api));
        }
        apiListDTO.setCount(apiSet.size());

        return apiListDTO;
    }

    /**
     * Converts a JSONObject to corresponding RatingDTO
     *
     * @param  obj JSON Object to be converted
     * @return RatingDTO object
     */
    public static RatingDTO fromJsonToRatingDTO(JSONObject obj) {
        RatingDTO ratingDTO = new RatingDTO();
        if (obj != null) {
            ratingDTO.setApiId(String.valueOf(obj.get(APIConstants.API_ID)));
            ratingDTO.setRatingId(String.valueOf(obj.get(APIConstants.RATING_ID)));
            ratingDTO.setUsername((String) obj.get(APIConstants.USERNAME));
            ratingDTO.setRating((Integer) obj.get(APIConstants.RATING));
        }
        return ratingDTO;
    }

    /**
     * Converts a List object of Ratings into a DTO
     *
     * @param ratings        List of Ratings
     * @param limit          maximum number of ratings to be returned
     * @param offset         starting index
     * @return RatingListDTO object containing Rating DTOs
     */
    public static RatingListDTO fromRatingListToDTO(List<RatingDTO> ratings, int offset, int limit) {
        RatingListDTO ratingListDTO = new RatingListDTO();
        List<RatingDTO> ratingDTOs = ratingListDTO.getList();
        if (ratingDTOs == null) {
            ratingDTOs = new ArrayList<>();
            ratingListDTO.setList(ratingDTOs);
        }

        //add the required range of objects to be returned
        int start = offset < ratings.size() && offset >= 0 ? offset : Integer.MAX_VALUE;
        int end = offset + limit - 1 <= ratings.size() - 1 ? offset + limit - 1 : ratings.size() - 1;
        for (int i = start; i <= end; i++) {
            ratingDTOs.add(ratings.get(i));
        }
        ratingListDTO.setCount(ratingDTOs.size());
        return ratingListDTO;
    }

    /**
     * Sets pagination urls for a RatingListDTO object given pagination parameters and url parameters
     *
     * @param ratingListDTO   a RatingListDTO object
     * @param limit           max number of objects returned
     * @param offset          starting index
     * @param size            max offset
     */
    public static void setRatingPaginationParams(RatingListDTO ratingListDTO, String apiId, int offset, int limit,
            int size) {
        //acquiring pagination parameters and setting pagination urls
        Map<String, Integer> paginatedParams = RestApiUtil.getPaginationParams(offset, limit, size);
        String paginatedPrevious = "";
        String paginatedNext = "";

        if (paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) != null) {
            paginatedPrevious = RestApiUtil
                    .getRatingPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT), apiId);
        }

        if (paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET) != null) {
            paginatedNext = RestApiUtil
                    .getRatingPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_NEXT_LIMIT), apiId);
        }

        PaginationDTO paginationDTO = CommonMappingUtil
                .getPaginationDTO(limit, offset, size, paginatedNext, paginatedPrevious);
        ratingListDTO.setPagination(paginationDTO);
    }

    /**
     * Converts a List object of APIs into a DTO
     *
     * @param apiList List of APIs
     * @param limit   maximum number of APIs returns
     * @param offset  starting index
     * @return APIListDTO object containing APIDTOs
     */
    public static APIListDTO fromAPIListToDTO(List<API> apiList, int offset, int limit) {
        APIListDTO apiListDTO = new APIListDTO();
        List<APIInfoDTO> apiInfoDTOs = apiListDTO.getList();
        if (apiInfoDTOs == null) {
            apiInfoDTOs = new ArrayList<>();
            apiListDTO.setList(apiInfoDTOs);
        }

        //add the required range of objects to be returned
        int start = offset < apiList.size() && offset >= 0 ? offset : Integer.MAX_VALUE;
        int end = offset + limit - 1 <= apiList.size() - 1 ? offset + limit - 1 : apiList.size() - 1;
        for (int i = start; i <= end; i++) {
            apiInfoDTOs.add(fromAPIToInfoDTO(apiList.get(i)));
        }
        apiListDTO.setCount(apiInfoDTOs.size());
        return apiListDTO;
    }

    /**
     * Converts a List object of APIs into a DTO
     *
     * @param apiList List of APIs
     * @return APIListDTO object containing APIDTOs
     */
    public static APIListDTO fromAPIListToDTO(List<API> apiList) {
        APIListDTO apiListDTO = new APIListDTO();
        List<APIInfoDTO> apiInfoDTOs = apiListDTO.getList();
        if (apiList != null) {
            for (API api : apiList) {
                apiInfoDTOs.add(fromAPIToInfoDTO(api));
            }
        }
        apiListDTO.setCount(apiInfoDTOs.size());
        return apiListDTO;
    }

    /**
     * Creates a minimal DTO representation of an API object
     *
     * @param api API object
     * @return a minimal representation DTO
     */
    public static APIInfoDTO fromAPIToInfoDTO(API api) {
        APIInfoDTO apiInfoDTO = new APIInfoDTO();
        apiInfoDTO.setDescription(api.getDescription());
        apiInfoDTO.setContext(api.getContext());
        apiInfoDTO.setId(api.getUUID());
        APIIdentifier apiId = api.getId();
        apiInfoDTO.setName(apiId.getApiName());
        apiInfoDTO.setVersion(apiId.getVersion());
        apiInfoDTO.setProvider(apiId.getProviderName());
        apiInfoDTO.setLifeCycleStatus(api.getStatus());
        apiInfoDTO.setType(api.getType());
        apiInfoDTO.setAvgRating(String.valueOf(api.getRating()));
        String providerName = api.getId().getProviderName();
        apiInfoDTO.setProvider(APIUtil.replaceEmailDomainBack(providerName));
        Set<Tier> throttlingPolicies = api.getAvailableTiers();
        List<String> throttlingPolicyNames = new ArrayList<>();
        for (Tier tier : throttlingPolicies) {
            throttlingPolicyNames.add(tier.getName());
        }
        apiInfoDTO.setThrottlingPolicies(throttlingPolicyNames);
        //        if (api.getScopes() != null) {
        //            apiInfoDTO.setScopes(getScopeInfoDTO(api.getScopes()));
        //        }
        //        if (!StringUtils.isBlank(api.getThumbnailUrl())) {
        //            apiInfoDTO.setThumbnailUri(getThumbnailUri(api.getUUID()));
        //        }
        return apiInfoDTO;
    }

    /**
     * Extracts the API environment details with access url for each endpoint
     * 
     * @param api API object
     * @param tenantDomain Tenant domain of the API
     * @return the API environment details
     * @throws APIManagementException error while extracting the information
     */
    private static List<APIEndpointURLsDTO> extractEnpointURLs(API api, String tenantDomain)
            throws APIManagementException {
        List<APIEndpointURLsDTO> apiEndpointsList = new ArrayList<>();

        APIManagerConfiguration config = ServiceReferenceHolder.getInstance().getAPIManagerConfigurationService()
                .getAPIManagerConfiguration();
        Map<String, Environment> environments = config.getApiGatewayEnvironments();

        Set<String> environmentsPublishedByAPI = new HashSet<>(api.getEnvironments());
        environmentsPublishedByAPI.remove("none");

        Set<String> apiTransports = new HashSet<>(Arrays.asList(api.getTransports().split(",")));
        APIConsumer apiConsumer = RestApiUtil.getLoggedInUserConsumer();

        for (String environmentName : environmentsPublishedByAPI) {
            Environment environment = environments.get(environmentName);
            if (environment != null) {
                APIURLsDTO apiURLsDTO = new APIURLsDTO();
                APIDefaultVersionURLsDTO apiDefaultVersionURLsDTO = new APIDefaultVersionURLsDTO();
                String[] gwEndpoints = null;
                if ("WS".equalsIgnoreCase(api.getType())) {
                    gwEndpoints = environment.getWebsocketGatewayEndpoint().split(",");
                } else {
                    gwEndpoints = environment.getApiGatewayEndpoint().split(",");
                }
                Map<String, String> domains = new HashMap<>();
                if (tenantDomain != null) {
                    domains = apiConsumer.getTenantDomainMappings(tenantDomain,
                            APIConstants.API_DOMAIN_MAPPINGS_GATEWAY);
                }

                String customGatewayUrl = null;
                if (domains != null) {
                    customGatewayUrl = domains.get(APIConstants.CUSTOM_URL);
                }

                for (String gwEndpoint : gwEndpoints) {
                    StringBuilder endpointBuilder = new StringBuilder(gwEndpoint);

                    if (customGatewayUrl != null) {
                        int index = endpointBuilder.indexOf("//");
                        endpointBuilder.replace(index + 2, endpointBuilder.length(), customGatewayUrl);
                        endpointBuilder.append(api.getContext().replace("/t/" + tenantDomain, ""));
                    } else {
                        endpointBuilder.append(api.getContext());
                    }

                    if (gwEndpoint.contains("http:") && apiTransports.contains("http")) {
                        apiURLsDTO.setHttp(endpointBuilder.toString());
                    } else if (gwEndpoint.contains("https:") && apiTransports.contains("https")) {
                        apiURLsDTO.setHttps(endpointBuilder.toString());
                    } else if (gwEndpoint.contains("ws:")) {
                        apiURLsDTO.setWs(endpointBuilder.toString());
                    } else if (gwEndpoint.contains("wss:")) {
                        apiURLsDTO.setWss(endpointBuilder.toString());
                    }

                    if (api.isDefaultVersion()) {
                        int index = endpointBuilder.indexOf(api.getId().getVersion());
                        endpointBuilder.replace(index, endpointBuilder.length(), "");
                        if (gwEndpoint.contains("http:") && apiTransports.contains("http")) {
                            apiDefaultVersionURLsDTO.setHttp(endpointBuilder.toString());
                        } else if (gwEndpoint.contains("https:") && apiTransports.contains("https")) {
                            apiDefaultVersionURLsDTO.setHttps(endpointBuilder.toString());
                        } else if (gwEndpoint.contains("ws:")) {
                            apiDefaultVersionURLsDTO.setWs(endpointBuilder.toString());
                        } else if (gwEndpoint.contains("wss:")) {
                            apiDefaultVersionURLsDTO.setWss(endpointBuilder.toString());
                        }
                    }
                }

                APIEndpointURLsDTO apiEndpointURLsDTO = new APIEndpointURLsDTO();
                apiEndpointURLsDTO.setDefaultVersionURLs(apiDefaultVersionURLsDTO);
                apiEndpointURLsDTO.setUrLs(apiURLsDTO);

                apiEndpointURLsDTO.setEnvironmentName(environment.getName());
                apiEndpointURLsDTO.setEnvironmentType(environment.getType());

                apiEndpointsList.add(apiEndpointURLsDTO);
            }
        }

        return apiEndpointsList;
    }

    /**
     * Returns label details of the API in REST API DTO format.
     *
     * @param gatewayLabels Gateway label details from the API model object 
     * @param apiContext API context
     * @return label details of the API in REST API DTO format
     */
    private static List<LabelDTO> getLabelDetails(List<Label> gatewayLabels, String apiContext) {
        List<LabelDTO> labels = new ArrayList<>();
        for (Label label : gatewayLabels) {
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setName(label.getName());
            labelDTO.setDescription(label.getDescription());
            for (String url : label.getAccessUrls()) {
                labelDTO.getAccessUrls().add(url + apiContext);
            }
            labels.add(labelDTO);
        }
        return labels;
    }

    //    /**
    //     * Creates a minimal scope DTO which will be a part of API Object
    //     *
    //     * @param scopes set
    //     * @return Scope DTO
    //     */
    //    public static List<ScopeInfoDTO> getScopeInfoDTO(Set<Scope> scopes) {
    //
    //        List<ScopeInfoDTO> scopeDto = new ArrayList<ScopeInfoDTO>();
    //        for (Scope scope : scopes) {
    //            ScopeInfoDTO scopeInfoDTO = new ScopeInfoDTO();
    //            scopeInfoDTO.setKey(scope.getKey());
    //            scopeInfoDTO.setName(scope.getName());
    //            if (scope.getRoles() != null) {
    //                scopeInfoDTO.setRoles(Arrays.asList(scope.getRoles().split(",")));
    //            }
    //            scopeDto.add(scopeInfoDTO);
    //        }
    //        return scopeDto;
    //    }

    /**
     * Converts a List object of API Products into a DTO
     *
     * @param productList List of APIs
     * @return APIListDTO object containing APIDTOs
     */
    public static APIProductListDTO fromAPIProductListtoDTO(List<APIProduct> productList) {
        APIProductListDTO listDto = new APIProductListDTO();
        List<APIProductInfoDTO> list = new ArrayList<APIProductInfoDTO>();
        for (APIProduct apiProduct : productList) {
            APIProductInfoDTO productDto = new APIProductInfoDTO();
            productDto.setName(apiProduct.getId().getName());
            productDto.setProvider(apiProduct.getId().getProviderName());
            productDto.setDescription(apiProduct.getDescription());
            productDto.setId(apiProduct.getUuid());
            productDto.setThumbnailUri(RestApiConstants.RESOURCE_PATH_THUMBNAIL_API_PRODUCT
                    .replace(RestApiConstants.APIPRODUCTID_PARAM, apiProduct.getUuid()));
            list.add(productDto);
        }

        listDto.setList(list);
        listDto.setCount(list.size());
        return listDto;
    }
    
    public static APIProductDTO fromAPIProductToDTO(APIProduct product) throws APIManagementException {
        APIProductDTO dto = new APIProductDTO();
        dto.setId(product.getUuid());
        dto.setName(product.getId().getName());
        dto.setDescription(product.getDescription());
        dto.setProvider(product.getId().getProviderName());
        dto.setApiDefinition(product.getDefinition());
        dto.setThumbnailUrl(RestApiConstants.RESOURCE_PATH_THUMBNAIL_API_PRODUCT
                .replace(RestApiConstants.APIPRODUCTID_PARAM, product.getUuid()));
        APIProductBusinessInformationDTO businessInformation = new APIProductBusinessInformationDTO();
        businessInformation.setBusinessOwner(product.getBusinessOwner());
        businessInformation.setBusinessOwnerEmail(product.getBusinessOwnerEmail());
        dto.setBusinessInformation(businessInformation);
        Set<org.wso2.carbon.apimgt.api.model.Tier> apiTiers = product.getAvailableTiers();
        List<String> tiersToReturn = new ArrayList<>();
        for (org.wso2.carbon.apimgt.api.model.Tier tier : apiTiers) {
            tiersToReturn.add(tier.getName());
        }
        dto.setTiers(tiersToReturn);

        return dto;
    }
    
    /**
     * Creates a minimal DTO representation of an API Product object
     *
     * @param apiProduct API product object
     * @return a minimal representation DTO
     */
    public static APIProductInfoDTO fromAPIProductToInfoDTO(APIProduct apiProduct) {
        APIProductInfoDTO apiProductInfoDTO = new APIProductInfoDTO();
        apiProductInfoDTO.setDescription(apiProduct.getDescription());
        apiProductInfoDTO.setId(apiProduct.getUuid());
        apiProductInfoDTO.setName(apiProduct.getId().getName());
        String providerName = apiProduct.getId().getProviderName();
        apiProductInfoDTO.setProvider(APIUtil.replaceEmailDomainBack(providerName));
        apiProductInfoDTO.setThumbnailUri(RestApiConstants.RESOURCE_PATH_THUMBNAIL_API_PRODUCT
                .replace(RestApiConstants.APIPRODUCTID_PARAM, apiProduct.getUuid()));

        return apiProductInfoDTO;
    }

    /**
     * Sets pagination urls for a APIProductListDTO object given pagination parameters and url parameters
     *
     * @param apiProductListDTO a APIProductListDTO object
     * @param query      search condition
     * @param limit      max number of objects returned
     * @param offset     starting index
     * @param size       max offset
     */
    public static void setPaginationParams(APIProductListDTO apiProductListDTO, String query, int offset, int limit, int size) {

        //acquiring pagination parameters and setting pagination urls
        Map<String, Integer> paginatedParams = RestApiUtil.getPaginationParams(offset, limit, size);
        String paginatedPrevious = "";
        String paginatedNext = "";

        if (paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) != null) {
            paginatedPrevious = RestApiUtil
                    .getAPIProductPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT), query);
        }

        if (paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET) != null) {
            paginatedNext = RestApiUtil
                    .getAPIProductPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_NEXT_LIMIT), query);
        }

        PaginationDTO paginationDTO = CommonMappingUtil
                .getPaginationDTO(limit, offset, size, paginatedNext, paginatedPrevious);
        apiProductListDTO.setPagination(paginationDTO);
    }
}

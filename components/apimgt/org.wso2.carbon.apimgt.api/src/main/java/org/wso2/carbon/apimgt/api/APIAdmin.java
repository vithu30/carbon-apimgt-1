/*
*  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.apimgt.api;

import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.api.model.Label;
import org.wso2.carbon.apimgt.api.model.Monetization;
import org.wso2.carbon.apimgt.api.model.MonetizationUsagePublishInfo;

import java.util.List;

/**
 * APIAdmin responsible for providing helper functionality
 */
public interface APIAdmin  {
    /**
     * Returns labels of a given tenant
     *
     * @param tenantDomain    tenant domain
     * @return A List of labels related to the given tenant
     */
    List<Label> getAllLabels(String tenantDomain) throws APIManagementException;

    /**
     * Creates a new label for the tenant
     *
     * @param tenantDomain    tenant domain
     * @param label           content to add
     * @throws APIManagementException if failed add Label
     */
    Label addLabel(String tenantDomain, Label label) throws APIManagementException;

    /**
     * Delete existing label
     *
     * @param labelID  Label identifier
     * @throws APIManagementException If failed to delete label
     */
    void deleteLabel(String labelID) throws APIManagementException;

    /**
     * Updates the details of the given Label.
     * @param tenantDomain    tenant domain
     * @param label             content to update
     * @throws APIManagementException if failed to update label
     */
    Label updateLabel(String tenantDomain, Label label) throws APIManagementException;

    Application[] getAllApplicationsOfTenantForMigration(String appTenantDomain) throws APIManagementException;

    /**
     * Get the applications of the given tenantId with pagination.
     *
     * @param tenantId             tenant Id
     * @param start                content to start
     * @param offset               content to limit number of pages
     * @param searchOwner          content to search applications based on owners
     * @param searchApplication    content to search applications based on application
     * @param sortColumn           content to sort column
     * @param sortOrder            content to sort in a order
     * @throws APIManagementException if failed to get applications
     */
    List<Application> getApplicationsByTenantIdWithPagination(int tenantId, int start , int offset, String searchOwner,
                                                              String searchApplication, String sortColumn,
                                                              String sortOrder)
            throws APIManagementException;

    /**
     * Get count of the applications for the tenantId.
     *
     * @param tenantId             content to get application count based on tenant_id
     * @param searchOwner          content to search applications based on owners
     * @param searchApplication    content to search applications based on application
     * @throws APIManagementException if failed to get application
     */

    public int getApplicationsCount(int tenantId, String searchOwner, String searchApplication)
            throws APIManagementException;

    /**
     * This methods loads the monetization implementation class
     *
     * @return monetization implementation class
     * @throws APIManagementException if failed to load monetization implementation class
     */
    Monetization getMonetizationImplClass() throws APIManagementException;

    /**
     * Get the info about the monetization usage publish job
     *
     * @throws APIManagementException if failed to get monetization usage publish info
     */
    MonetizationUsagePublishInfo getMonetizationUsagePublishInfo() throws APIManagementException;

    /**
     * Add the info about the monetization usage publish job
     *
     * @throws APIManagementException if failed to update monetization usage publish info
     */
    void addMonetizationUsagePublishInfo(MonetizationUsagePublishInfo monetizationUsagePublishInfo)
            throws APIManagementException;

    /**
     * Update the info about the monetization usage publish job
     *
     * @throws APIManagementException if failed to update monetization usage publish info
     */
    void updateMonetizationUsagePublishInfo(MonetizationUsagePublishInfo monetizationUsagePublishInfo)
            throws APIManagementException;

    /**
     * The method converts the date into timestamp
     *
     * @param date
     * @return Timestamp in long format
     */
    long getTimestamp(String date);
}

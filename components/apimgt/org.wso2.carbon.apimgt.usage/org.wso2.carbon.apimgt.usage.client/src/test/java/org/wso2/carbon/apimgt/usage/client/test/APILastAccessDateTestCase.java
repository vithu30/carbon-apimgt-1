/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.apimgt.usage.client.test;

import org.junit.Assert;
import org.junit.Test;
import org.wso2.carbon.apimgt.usage.client.util.APIUsageClientUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Testing API Last AccessDate.
 */
public class APILastAccessDateTestCase {

    @Test
    public void testAPILastAccessDateFormat(){

        Calendar calendar = new GregorianCalendar(2016, 2, 31, 10, 30, 15); //2016-03-31::10:30:15
        String formattedDate = APIUsageClientUtil.getFormattedAPILastAccessDate(calendar.getTimeInMillis());
        Assert.assertEquals("03/31/2016  10:30", formattedDate);
    }
}

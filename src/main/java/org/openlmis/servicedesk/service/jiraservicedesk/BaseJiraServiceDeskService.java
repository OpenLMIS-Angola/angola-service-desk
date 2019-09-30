/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.servicedesk.service.jiraservicedesk;

import org.openlmis.servicedesk.service.BaseCommunicationService;
import org.openlmis.servicedesk.service.RequestHeaders;
import org.openlmis.servicedesk.util.RequestHelper;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseJiraServiceDeskService<T> extends BaseCommunicationService<T> {

  @Value("${serviceDeskApi.url}")
  private String serviceDeskUrl;

  @Value("${serviceDeskApi.userEmail}")
  private String userEmail;

  @Value("${serviceDeskApi.token}")
  private String token;

  protected String getServiceUrl() {
    return serviceDeskUrl;
  }

  protected String getJiraServiceDeskToken() {
    return RequestHelper.encodeToken(String.format("%s:%s", userEmail, token));
  }

  protected RequestHeaders addAtlassianTokenHeader(RequestHeaders headers) {
    return headers.set("X-Atlassian-Token", "no-check");
  }

  protected RequestHeaders addExpectHeader(RequestHeaders headers) {
    return headers.set("Expect", "100-continue");
  }

  protected RequestHeaders addExperimentalApiHeader(RequestHeaders headers) {
    return headers.set("X-ExperimentalApi", "opt-in");
  }
}

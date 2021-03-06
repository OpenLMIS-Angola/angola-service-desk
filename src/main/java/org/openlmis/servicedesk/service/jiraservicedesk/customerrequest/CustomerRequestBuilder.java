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

package org.openlmis.servicedesk.service.jiraservicedesk.customerrequest;

import org.openlmis.servicedesk.web.issue.IssueDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomerRequestBuilder {

  @Value("${serviceDeskApi.serviceDeskId}")
  private int serviceDeskId;

  @Value("${publicUrl}")
  private String publicUrl;

  /**
   * Builds valid Service Desk customer Request object from issue send from UI.
   *
   * @param  issue             issue send from UI
   * @param  customerAccountId customer id to be used as request author
   * @return                   customer request ready to send to Service Desk API
   */
  public CustomerRequest build(
      IssueDto issue, String customerAccountId, String email, String username) {

    return new CustomerRequest(
        serviceDeskId,
        issue.getType().getValue(),
        new RequestFieldValues(
            issue.getSummary(),
            getRequestDescriptionWithMetadata(issue, username, email),
            new CustomField(issue.getImpact().getValue().toString()),
            new CustomField(issue.getPriority().getValue().toString())),
        customerAccountId);
  }

  private String getRequestDescriptionWithMetadata(
      IssueDto issueDto, String username, String email) {

    return
        String.format("%s %n%n[METADATA]%nUsername: %s%nEmail: %s%nURL: %s",
            issueDto.getDescription(), username, email, publicUrl + "/#!/service-desk");
  }
}

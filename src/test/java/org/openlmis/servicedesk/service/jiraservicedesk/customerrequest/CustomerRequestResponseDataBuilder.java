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

import java.util.ArrayList;
import java.util.List;

public class CustomerRequestResponseDataBuilder {

  private static int instanceNumber = 0;

  private List<String> expands;
  private Integer issueId;
  private String issueKey;
  private Integer requestTypeId;
  private Integer serviceDeskId;
  private JiraDate createdDate;
  private Reporter reporter;
  private List<RequestField> requestFieldValues;
  private Status currentStatus;
  private Links links;

  /**
   * Constructor for {@link CustomerRequestResponseDataBuilder}.
   * Sets default values for new instance of {@link CustomerRequestResponse} class.
   */
  public CustomerRequestResponseDataBuilder() {
    instanceNumber++;

    expands = new ArrayList<>();
    issueId = instanceNumber;
    issueKey = "ServiceDeskAngola-" + instanceNumber;
    requestTypeId = instanceNumber;
    serviceDeskId = instanceNumber;
    createdDate = new JiraDateDataBuilder().build();
    reporter = new ReporterDataBuilder().build();
    requestFieldValues = new ArrayList<>();
    currentStatus = new StatusDataBuilder().build();
    links = new LinksDataBuilder().build();
  }

  public CustomerRequestResponse build() {
    return new CustomerRequestResponse(expands, issueId, issueKey, requestTypeId, serviceDeskId,
        createdDate, reporter, requestFieldValues, currentStatus, links);
  }
}

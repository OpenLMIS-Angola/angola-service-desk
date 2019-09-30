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

public class ReporterDataBuilder {

  private static int instanceNumber;

  private String accountId;
  private String name;
  private String key;
  private String emailAddress;
  private String displayName;
  private Boolean active;
  private String timeZone;
  private Links links;

  /**
   * Constructor for {@link ReporterDataBuilder}.
   * Sets default values for new instance of {@link Reporter} class.
   */
  public ReporterDataBuilder() {
    instanceNumber++;

    accountId = "user" + instanceNumber;
    name = "User" + instanceNumber;
    key = "kye" + instanceNumber;
    emailAddress = String.format("user%s@siglofa.org", instanceNumber);
    displayName = "John Doe";
    active = true;
    timeZone = "UTC";
    links = new LinksDataBuilder().build();
  }

  public Reporter build() {
    return new Reporter(accountId, name, key, emailAddress, displayName, active, timeZone, links);
  }
}

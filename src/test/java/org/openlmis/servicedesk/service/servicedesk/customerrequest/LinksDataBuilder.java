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

package org.openlmis.servicedesk.service.servicedesk.customerrequest;

import java.util.HashMap;
import java.util.Map;

public class LinksDataBuilder {

  private static int instanceNumber = 0;

  private String jiraRest;
  private String web;
  private String self;
  private Map<String, String> avatarUrls;

  /**
   * Constructor for {@link LinksDataBuilder}.
   * Sets default values for new instance of {@link Links} class.
   */
  public LinksDataBuilder() {
    instanceNumber++;

    jiraRest = "www.jira.com/" + instanceNumber;
    web = "www.web.com/" + instanceNumber;
    self = "www.self.com/" + instanceNumber;
    avatarUrls = new HashMap<>();
  }

  public Links build() {
    return new Links(jiraRest, web, self, avatarUrls);
  }
}

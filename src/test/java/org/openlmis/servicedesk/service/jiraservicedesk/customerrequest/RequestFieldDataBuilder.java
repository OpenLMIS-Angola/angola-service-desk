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

import java.util.HashMap;
import java.util.Map;

public class RequestFieldDataBuilder {

  private static int instanceNumber = 0;

  private String fieldId;
  private String label;
  // this could be either List or String, we are not using this value so we can simply use Object
  private Object value;
  private Map<String, String> renderedValue;

  /**
   * Constructor for {@link RequestFieldDataBuilder}.
   * Sets default values for new instance of {@link RequestField} class.
   */
  public RequestFieldDataBuilder() {
    instanceNumber++;

    fieldId = String.valueOf(instanceNumber);
    label = "field" + instanceNumber;
    value = instanceNumber;
    renderedValue = new HashMap<>();
  }

  public RequestField build() {
    return new RequestField(fieldId, label, value, renderedValue);
  }
}

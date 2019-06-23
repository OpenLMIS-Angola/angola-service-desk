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

public class RequestFieldValuesDataBuilder {

  private String summary;
  private String description;
  private CustomField impact;
  private CustomField priority;

  /**
   * Constructor for {@link RequestFieldValuesDataBuilder}.
   * Sets default values for new instance of {@link RequestFieldValues} class.
   */
  public RequestFieldValuesDataBuilder() {
    summary = "summary";
    description = "description";
    impact = new CustomField("1");
    priority = new CustomField("2");
  }

  public RequestFieldValuesDataBuilder withSummary(String summary) {
    this.summary = summary;
    return this;
  }

  public RequestFieldValuesDataBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public RequestFieldValuesDataBuilder withImpact(CustomField impact) {
    this.impact = impact;
    return this;
  }

  public RequestFieldValuesDataBuilder withPriority(CustomField priority) {
    this.priority = priority;
    return this;
  }

  public RequestFieldValues build() {
    return new RequestFieldValues(summary, description, impact, priority);
  }
}

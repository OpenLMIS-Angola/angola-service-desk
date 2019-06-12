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

package org.openlmis.servicedesk.service.customerrequest;

public class CustomerRequestDataBuilder {

  private Integer serviceDeskId;
  private Integer requestTypeId;
  private RequestFieldValues requestFieldValues;

  /**
   * Constructor for {@link CustomerRequestDataBuilder}.
   * Sets default values for new instance of {@link CustomerRequest} class.
   */
  public CustomerRequestDataBuilder() {
    serviceDeskId = 1;
    requestTypeId = 2;
    requestFieldValues = new RequestFieldValuesDataBuilder().build();
  }

  public CustomerRequestDataBuilder withServiceDeskId(Integer serviceDeskId) {
    this.serviceDeskId = serviceDeskId;
    return this;
  }

  public CustomerRequestDataBuilder withRequestTypeId(Integer requestTypeId) {
    this.requestTypeId = requestTypeId;
    return this;
  }

  public CustomerRequestDataBuilder withRequestFieldValues(RequestFieldValues requestFieldValues) {
    this.requestFieldValues = requestFieldValues;
    return this;
  }

  public CustomerRequest build() {
    return new CustomerRequest(serviceDeskId, requestTypeId, requestFieldValues);
  }
}

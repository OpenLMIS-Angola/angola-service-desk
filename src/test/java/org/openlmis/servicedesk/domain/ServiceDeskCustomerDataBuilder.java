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

package org.openlmis.servicedesk.domain;

import java.util.UUID;

public class ServiceDeskCustomerDataBuilder {

  private static int instanceNumber = 0;

  private UUID id;
  private String email;
  private String customerId;

  /**
   * Constructor for {@link ServiceDeskCustomerDataBuilder}.
   * Sets default values for new instance of {@link ServiceDeskCustomer} class.
   */
  public ServiceDeskCustomerDataBuilder() {
    instanceNumber++;

    id = UUID.randomUUID();
    email = "test" + instanceNumber + "@siglofa.org";
    customerId = "customer" + instanceNumber;
  }

  public ServiceDeskCustomerDataBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Builds {@link ServiceDeskCustomer} object from builder parameters.
   *
   * @return new instance of {@link ServiceDeskCustomer}
   */
  public ServiceDeskCustomer build() {
    ServiceDeskCustomer serviceDeskCustomer = new ServiceDeskCustomer(email, customerId);
    serviceDeskCustomer.setId(id);
    return serviceDeskCustomer;
  }
}

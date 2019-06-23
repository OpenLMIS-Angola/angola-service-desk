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

package org.openlmis.servicedesk.service.servicedesk.customer;

public class CustomerDataBuilder {

  private static int instanceNumber = 0;

  private String displayName;
  private String email;

  /**
   * Constructor for {@link CustomerDataBuilder}.
   * Sets default values for new instance of {@link Customer} class.
   */
  public CustomerDataBuilder() {
    instanceNumber++;

    displayName = "User " + instanceNumber;
    email = String.format("user%s@siglofa.org", instanceNumber);
  }

  public Customer build() {
    return new Customer(displayName, email);
  }
}

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

package org.openlmis.servicedesk.service.jiraservicedesk.customer;

public class CreatedCustomerDataBuilder {

  private static int instanceNumber = 0;

  private String accountId;
  private String name;
  private String key;
  private String displayName;
  private String emailAddress;

  /**
   * Constructor for {@link CreatedCustomerDataBuilder}.
   * Sets default values for new instance of {@link CreatedCustomer} class.
   */
  public CreatedCustomerDataBuilder() {
    instanceNumber++;

    accountId = "account" + instanceNumber;
    name = "name" + instanceNumber;
    key = "key" + instanceNumber;
    displayName = "user" + instanceNumber;
    emailAddress = String.format("user%s@siglofa.com", instanceNumber);
  }

  public CreatedCustomer build() {
    return new CreatedCustomer(accountId, name, key, displayName, emailAddress);
  }

  public CreatedCustomerDataBuilder withEmailAddress(String email) {
    this.emailAddress = email;
    return this;
  }
}

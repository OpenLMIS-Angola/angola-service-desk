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

import java.util.ArrayList;
import java.util.List;

public class CustomersResponseDataBuilder {

  private List<CreatedCustomer> values;

  /**
   * Constructor for {@link CustomersResponseDataBuilder}.
   * Sets default values for new instance of {@link CreatedCustomer} class.
   */
  public CustomersResponseDataBuilder() {
    values = new ArrayList<>();
    values.add(new CreatedCustomerDataBuilder().build());
  }

  public CustomersResponse build() {
    return new CustomersResponse(values);
  }

  public CustomersResponseDataBuilder withCreatedCustomer(CreatedCustomer createdCustomer) {
    this.values.add(createdCustomer);
    return this;
  }

  public CustomersResponseDataBuilder withEmptyCustomers() {
    this.values = new ArrayList<>();
    return this;
  }
}

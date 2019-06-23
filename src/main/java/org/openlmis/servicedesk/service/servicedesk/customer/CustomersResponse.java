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

import static org.openlmis.servicedesk.i18n.MessageKeys.CUSTOMER_NOT_FOUND;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.servicedesk.exception.NotFoundException;

@AllArgsConstructor
@NoArgsConstructor
@Data
public final class CustomersResponse {

  private List<CreatedCustomer> values;

  /**
   * Finds specific customer in list of all customers by email.
   * Throws {@link NotFoundException} if no customer was found.
   *
   * @param  email email address value to be used in filter
   * @return       customer with given email address
   */
  public CreatedCustomer findByEmail(String email) {
    return values.stream()
        .filter(customer -> customer.getEmailAddress().equals(email))
        .findFirst()
        .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND));
  }
}

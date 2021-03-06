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

package org.openlmis.servicedesk.i18n;

import java.util.Arrays;

public abstract class MessageKeys {

  private static final String DELIMITER = ".";

  private static final String NOT_FOUND = "notFound";
  private static final String AUTHENTICATION = "authentication";
  private static final String SERVICE_PREFIX = "serviceDesk";
  private static final String ATTACHMENT = "attachment";
  private static final String CUSTOMER = "customer";

  private static final String ERROR_PREFIX = join(SERVICE_PREFIX, "error");

  public static final String USER_NOT_FOUND =
      join(ERROR_PREFIX, AUTHENTICATION, "user", NOT_FOUND);

  public static final String USER_CONTACT_DETAILS_NOT_FOUND =
      join(ERROR_PREFIX, AUTHENTICATION, "userContactDetails", NOT_FOUND);
  public static final String CURRENT_USER_HAS_NO_EMAIL =
      join(ERROR_PREFIX, "currentUser", "hasNoEmail");

  public static final String ATTACHMENT_FAILED_TO_READ =
      join(ERROR_PREFIX, ATTACHMENT, "failedToRead");
  public static final String ATTACHMENT_NOT_FOUND = join(ERROR_PREFIX, ATTACHMENT, NOT_FOUND);

  public static final String CANNOT_FIND_AND_CREATE_CUSTOMER_WITH_EMAIL =
      join(ERROR_PREFIX, CUSTOMER, "byEmail", "cannotFindAndCreate");
  public static final String CANNOT_ADD_CUSTOMER_TO_SERVICE_DESK = join(ERROR_PREFIX, CUSTOMER,
      "cannotAddToServiceDesk");

  private MessageKeys() {
    throw new UnsupportedOperationException();
  }

  private static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }
}

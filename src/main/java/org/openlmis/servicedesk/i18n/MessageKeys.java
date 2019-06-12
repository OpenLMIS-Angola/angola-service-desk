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

  private static final String SERVICE_PREFIX = "serviceDesk";
  private static final String ERROR_PREFIX = join(SERVICE_PREFIX, "error");

  private static final String ISSUE = "issue";

  private static final String SUCCESSFULLY_CREATED = "createdSuccessfully";

  public static final String ISSUE_SUCCESSFULLY_CREATED =
      join(SERVICE_PREFIX, ISSUE, SUCCESSFULLY_CREATED);

  public static final String ERROR_IO = ERROR_PREFIX + ".io";
  public static final String ERROR_SERVICE_REQUIRED = ERROR_PREFIX + ".service.required";
  public static final String ERROR_SERVICE_OCCURED = ERROR_PREFIX + ".service.errorOccured";

  private MessageKeys() {
    throw new UnsupportedOperationException();
  }

  private static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }
}

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

package org.openlmis.servicedesk.service.refernecedata;

import java.util.UUID;
import org.openlmis.servicedesk.service.referencedata.UserDto;

public class UserDtoDataBuilder {

  private static int instanceNumber = 0;

  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private boolean active;

  /**
   * Constructor for {@link UserDtoDataBuilder}.
   * Sets default values for new instance of {@link UserDto} class.
   */
  public UserDtoDataBuilder() {
    instanceNumber++;

    id = UUID.randomUUID();
    username = "user" + instanceNumber;
    firstName = "John";
    lastName = "Doe";
    active = true;
  }

  public UserDto build() {
    return new UserDto(id, username, firstName, lastName, active);
  }
}

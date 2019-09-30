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

package org.openlmis.servicedesk.service.notification;

import java.util.UUID;

public class UserContactDetailsDtoDataBuilder {

  private static int instanceNumber = 0;

  private UUID referenceDataUserId;
  private String phoneNumber;
  private Boolean allowNotify;
  private EmailDetailsDto emailDetails;

  /**
   * Constructor for {@link UserContactDetailsDtoDataBuilder}.
   * Sets default values for new instance of {@link UserContactDetailsDto} class.
   */
  public UserContactDetailsDtoDataBuilder() {
    instanceNumber++;

    referenceDataUserId = UUID.randomUUID();
    phoneNumber = "123-123-12" + instanceNumber;
    allowNotify = true;
    emailDetails = new EmailDetailsDto(instanceNumber + "user@siglofa.org", true);
  }

  public UserContactDetailsDtoDataBuilder withReferenceDataUserId(UUID referenceDataUserId) {
    this.referenceDataUserId = referenceDataUserId;
    return this;
  }

  public UserContactDetailsDto build() {
    return new UserContactDetailsDto(referenceDataUserId, phoneNumber, allowNotify, emailDetails);
  }
}

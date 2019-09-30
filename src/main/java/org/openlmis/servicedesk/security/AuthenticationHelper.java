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

package org.openlmis.servicedesk.security;

import static org.openlmis.servicedesk.i18n.MessageKeys.USER_CONTACT_DETAILS_NOT_FOUND;
import static org.openlmis.servicedesk.i18n.MessageKeys.USER_NOT_FOUND;

import java.util.UUID;
import org.openlmis.servicedesk.exception.NotFoundException;
import org.openlmis.servicedesk.service.notification.UserContactDetailsDto;
import org.openlmis.servicedesk.service.notification.UserContactDetailsNotificationService;
import org.openlmis.servicedesk.service.referencedata.UserDto;
import org.openlmis.servicedesk.service.referencedata.UserReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private UserContactDetailsNotificationService userContactDetailsNotificationService;

  /**
   * Method returns current user based on Spring context
   * and fetches his data from reference-data service.
   *
   * @return UserDto entity of current user.
   * @throws NotFoundException if user cannot be found.
   */
  public UserDto getCurrentUser() {
    OAuth2Authentication authentication =
        (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    UserDto user = null;

    if (!authentication.isClientOnly()) {
      UUID userId = (UUID) authentication.getPrincipal();
      user = userReferenceDataService.findOne(userId);

      if (user == null) {
        throw new NotFoundException(USER_NOT_FOUND, userId.toString());
      }
    }

    return user;
  }

  /**
   * Method returns current user based on Spring context
   * and fetches his data from reference-data service.
   *
   * @return UserDto entity of current user.
   * @throws NotFoundException if user cannot be found.
   */
  public UserContactDetailsDto getCurrentUserContactDetails() {
    OAuth2Authentication authentication =
        (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    UserContactDetailsDto userContactDetailsDto = null;

    if (!authentication.isClientOnly()) {
      UUID userId = (UUID) authentication.getPrincipal();
      userContactDetailsDto = userContactDetailsNotificationService.findOne(userId);

      if (userContactDetailsDto == null) {
        throw new NotFoundException(USER_CONTACT_DETAILS_NOT_FOUND, userId.toString());
      }
    }

    return userContactDetailsDto;
  }
}

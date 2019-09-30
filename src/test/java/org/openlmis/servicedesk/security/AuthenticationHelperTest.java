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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.servicedesk.exception.NotFoundException;
import org.openlmis.servicedesk.service.notification.UserContactDetailsDto;
import org.openlmis.servicedesk.service.notification.UserContactDetailsDtoDataBuilder;
import org.openlmis.servicedesk.service.notification.UserContactDetailsNotificationService;
import org.openlmis.servicedesk.service.referencedata.UserDto;
import org.openlmis.servicedesk.service.referencedata.UserReferenceDataService;
import org.openlmis.servicedesk.service.refernecedata.UserDtoDataBuilder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationHelperTest {

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @Mock
  private UserContactDetailsNotificationService userContactDetailsNotificationService;

  @InjectMocks
  private AuthenticationHelper authenticationHelper;

  private UserDto user = new UserDtoDataBuilder().build();
  private UserContactDetailsDto userContactDetails = new UserContactDetailsDtoDataBuilder()
      .withReferenceDataUserId(user.getId())
      .build();

  @Before
  public void setUp() {
    OAuth2Authentication authentication = mock(OAuth2Authentication.class);
    when(authentication.getPrincipal()).thenReturn(user.getId());

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void shouldReturnUser() {
    when(userReferenceDataService.findOne(user.getId())).thenReturn(user);

    assertThat(authenticationHelper.getCurrentUser(), is(user));
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowExceptionIfUserDoesNotExist() {
    when(userReferenceDataService.findOne(any(UUID.class))).thenReturn(null);

    authenticationHelper.getCurrentUser();
  }

  @Test
  public void shouldReturnUserContactDetails() {
    when(userContactDetailsNotificationService.findOne(user.getId()))
        .thenReturn(userContactDetails);

    assertThat(authenticationHelper.getCurrentUserContactDetails(), is(userContactDetails));
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowExceptionIfUserContactDetailsDoesNotExist() {
    when(userContactDetailsNotificationService.findOne(any(UUID.class))).thenReturn(null);

    authenticationHelper.getCurrentUserContactDetails();
  }
}
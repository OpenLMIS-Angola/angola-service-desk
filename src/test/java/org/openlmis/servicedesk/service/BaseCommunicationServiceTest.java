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

package org.openlmis.servicedesk.service;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.servicedesk.service.auth.AuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public abstract class BaseCommunicationServiceTest<T> {

  private static final String BASIC_TOKEN_HEADER = "Basic ";
  private static final String BEARER_TOKEN_HEADER = "Bearer ";
  private static final String TOKEN = UUID.randomUUID().toString();

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Mock
  protected RestTemplate restTemplate;

  @Mock
  protected AuthService authService;

  @Captor
  protected ArgumentCaptor<URI> uriCaptor;

  @Captor
  protected ArgumentCaptor<HttpEntity> entityCaptor;

  @Before
  public void setUp() {
    prepareService();
    mockAuth();
  }

  protected abstract T generateInstance();

  protected abstract BaseCommunicationService<T> getService();

  protected BaseCommunicationService<T> prepareService() {
    BaseCommunicationService<T> service = getService();
    ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
    ReflectionTestUtils.setField(service, "authService", authService);
    return service;
  }

  protected void assertAuthHeader(HttpEntity value, String token) {
    List<String> authorization = value.getHeaders().get(HttpHeaders.AUTHORIZATION);

    assertThat(authorization, hasSize(1));
    assertThat(authorization, hasItem(BASIC_TOKEN_HEADER + token));
  }

  protected void assertAuthHeader(HttpEntity entity) {
    assertThat(entity.getHeaders().get(HttpHeaders.AUTHORIZATION),
        is(singletonList(BEARER_TOKEN_HEADER + TOKEN)));
  }

  protected void verifyPostRequest(int times, Class requestType) {
    verify(restTemplate, times(times)).exchange(uriCaptor.capture(), eq(HttpMethod.POST),
        entityCaptor.capture(), eq(requestType));
  }

  protected void verifyGetRequest(int times, Class requestType) {
    verify(restTemplate, times(times)).exchange(uriCaptor.capture(), eq(HttpMethod.GET),
        entityCaptor.capture(), eq(requestType));
  }

  private void mockAuth() {
    when(authService.obtainAccessToken()).thenReturn(TOKEN);
  }
}

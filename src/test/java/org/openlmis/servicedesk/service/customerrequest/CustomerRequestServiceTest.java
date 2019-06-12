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

package org.openlmis.servicedesk.service.customerrequest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Base64;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.servicedesk.service.BaseCommunicationService;
import org.openlmis.servicedesk.service.BaseCommunicationServiceTest;
import org.openlmis.servicedesk.util.RequestHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class CustomerRequestServiceTest extends BaseCommunicationServiceTest<CustomerRequest> {

  private static final String serviceDeskUrl =
      "https://openlmis.atlassian.net/rest/servicedeskapi";
  private static final String userEmail = "user@siglofa.com";
  private static final String token = "token";

  private CustomerRequestService service;

  private CustomerRequest customerRequest;
  private CustomerRequestResponse customerRequestResponse =
      new CustomerRequestResponseDataBuilder().build();
  private String encodedString;

  @Before
  public void setUp() {
    super.setUp();
    service = (CustomerRequestService) prepareService();

    ReflectionTestUtils.setField(service, "serviceDeskUrl", serviceDeskUrl);
    ReflectionTestUtils.setField(service, "userEmail", userEmail);
    ReflectionTestUtils.setField(service, "token", token);

    customerRequest = generateInstance();

    encodedString = Base64.getEncoder()
        .encodeToString(String.format("%s:%s", userEmail, token).getBytes());
  }

  @Test
  public void shouldSubmitCustomerRequest() {
    given(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class),
        any(Class.class))).willReturn(ResponseEntity.ok(customerRequestResponse));

    CustomerRequestResponse response = service.submit(customerRequest).getBody();

    verifyRequest(1);
    assertEquals(customerRequestResponse, response);
    assertEquals(RequestHelper.createUri(serviceDeskUrl + "/request"), uriCaptor.getValue());
    assertNotNull(entityCaptor.getValue().getBody());
    assertEquals(customerRequest, entityCaptor.getValue().getBody());
    assertAuthHeader(entityCaptor.getValue(), encodedString);
  }

  @Override
  protected CustomerRequest generateInstance() {
    return new CustomerRequestDataBuilder().build();
  }

  protected BaseCommunicationService<CustomerRequest> getService() {
    return new CustomerRequestService();
  }

  private void verifyRequest(int times) {
    verify(restTemplate, times(times)).exchange(uriCaptor.capture(), eq(HttpMethod.POST),
        entityCaptor.capture(), eq(CustomerRequestResponse.class));
  }
}
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.net.URI;
import java.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.servicedesk.service.BaseCommunicationService;
import org.openlmis.servicedesk.service.BaseCommunicationServiceTest;
import org.openlmis.servicedesk.util.RequestHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class CustomerServiceTest extends BaseCommunicationServiceTest<Customer> {

  private static final String serviceDeskUrl = "https://openlmis.atlassian.net/rest/servicedeskapi";
  private static final String serviceDeskId = "1";
  private static final String userEmail = "user@siglofa.com";
  private static final String token = "token";

  private CustomerService service;

  private String encodedString;

  @Before
  public void setUp() {
    super.setUp();
    service = (CustomerService) prepareService();

    ReflectionTestUtils.setField(service, "serviceDeskUrl", serviceDeskUrl);
    ReflectionTestUtils.setField(service, "serviceDeskId", serviceDeskId);
    ReflectionTestUtils.setField(service, "userEmail", userEmail);
    ReflectionTestUtils.setField(service, "token", token);

    encodedString = Base64.getEncoder()
        .encodeToString(String.format("%s:%s", userEmail, token).getBytes());
  }

  @Test
  public void shouldCreateCustomer() {
    CreatedCustomer expectedResponse = new CreatedCustomerDataBuilder().build();
    given(restTemplate.exchange(
        any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .willReturn(ResponseEntity.ok(expectedResponse));
    Customer customer = new CustomerDataBuilder().build();

    CreatedCustomer response = service.create(customer).getBody();

    verifyPostRequest(1, CreatedCustomer.class);
    assertEquals(expectedResponse, response);
    assertEquals(RequestHelper.createUri(serviceDeskUrl + "/customer"), uriCaptor.getValue());
    assertNotNull(entityCaptor.getValue().getBody());
    assertAuthHeader(entityCaptor.getValue(), encodedString);
  }

  @Test
  public void shouldAddCustomerToServiceDesk() {
    given(restTemplate.exchange(
        any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .willReturn(ResponseEntity.ok(new Object()));
    AddCustomersRequest addCustomersRequest = new AddCustomersRequestDataBuilder().build();

    service.addToServiceDesk(addCustomersRequest);

    verifyPostRequest(1, Object.class);
    assertEquals(RequestHelper.createUri(
        String.format("%s/servicedesk/%s/customer", serviceDeskUrl, serviceDeskId)),
        uriCaptor.getValue());
    assertNotNull(entityCaptor.getValue().getBody());
    assertAuthHeader(entityCaptor.getValue(), encodedString);
  }

  @Test
  public void shouldGetAllServiceDeskCustomers() {
    CustomersResponse expectedResponse = new CustomersResponseDataBuilder().build();
    given(restTemplate.exchange(
        any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
        .willReturn(ResponseEntity.ok(expectedResponse));
    String email = "test@email.com";

    CustomersResponse response = service.getServiceDeskCustomers(email).getBody();

    verifyGetRequest(1, CustomersResponse.class);
    assertEquals(expectedResponse, response);
    assertEquals(RequestHelper.createUri(
        String.format("%s/servicedesk/%s/customer?query=%s", serviceDeskUrl, serviceDeskId, email)),
        uriCaptor.getValue());
    assertAuthHeader(entityCaptor.getValue(), encodedString);
  }

  @Override
  protected Customer generateInstance() {
    return new CustomerDataBuilder().build();
  }

  protected BaseCommunicationService<Customer> getService() {
    return new CustomerService();
  }
}

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

package org.openlmis.servicedesk.web.issue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;

import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequest;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestBuilder;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestDataBuilder;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestResponse;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestResponseDataBuilder;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestService;
import org.openlmis.servicedesk.web.BaseWebIntegrationTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class IssueControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String ISSUES_URL = "/api/issues";

  @MockBean
  private CustomerRequestBuilder customerRequestBuilder;

  @MockBean
  private CustomerRequestService customerRequestService;

  private CustomerRequestResponse customerRequestResponse =
      new CustomerRequestResponseDataBuilder().build();
  private CustomerRequest customerRequest = new CustomerRequestDataBuilder().build();
  private IssueDto issueDto = new IssueDtoDataBuilder().build();

  @Before
  public void setUp() {

    given(customerRequestBuilder.build(eq(issueDto))).willReturn(customerRequest);
    given(customerRequestService.submit(eq(customerRequest)))
        .willReturn(ResponseEntity.ok(customerRequestResponse));
  }

  @Test
  public void shouldCallCustomerRequestServiceWhenCreatingAnIssue() {
    CustomerRequestResponse response = restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .body(issueDto)
        .post(ISSUES_URL)
        .then()
        .log().all()
        .statusCode(201)
        .extract()
        .as(CustomerRequestResponse.class);

    // then
    assertEquals(customerRequestResponse, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }
}

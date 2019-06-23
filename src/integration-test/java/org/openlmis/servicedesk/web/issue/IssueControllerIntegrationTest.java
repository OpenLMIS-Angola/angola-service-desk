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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.servicedesk.service.IssueService;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequest;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequestDataBuilder;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequestResponse;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequestResponseDataBuilder;
import org.openlmis.servicedesk.web.BaseWebIntegrationTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class IssueControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String ISSUES_URL = "/api/issues";
  private static final String ATTACHMENT_URL = ISSUES_URL + "/{issueId}/attachment";

  @MockBean
  private IssueService issueService;

  private CustomerRequestResponse customerRequestResponse =
      new CustomerRequestResponseDataBuilder().build();
  private CustomerRequest customerRequest = new CustomerRequestDataBuilder().build();
  private IssueDto issueDto = new IssueDtoDataBuilder().build();

  @Before
  public void setUp() {
    given(issueService.prepareCustomerRequest(eq(issueDto)))
        .willReturn(customerRequest);
    given(issueService.sendCustomerRequest(eq(customerRequest)))
        .willReturn(customerRequestResponse);
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
  }

  @Test
  public void shouldAttachFileToIssue() throws IOException {
    ClassPathResource fileToUpload = new ClassPathResource("some-file.txt");
    int issueId = 10;

    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .multiPart("file",
            fileToUpload.getFilename(),
            fileToUpload.getInputStream())
        .pathParam("issueId", issueId)
        .when()
        .post(ATTACHMENT_URL)
        .then()
        .statusCode(201);

    verify(issueService).attachFile(any(MultipartFile.class), eq(issueId));
  }
}

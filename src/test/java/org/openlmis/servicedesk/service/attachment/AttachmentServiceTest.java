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

package org.openlmis.servicedesk.service.attachment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.io.IOException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class AttachmentServiceTest extends BaseCommunicationServiceTest<AttachmentRequest> {

  private static final String serviceDeskUrl =
      "https://openlmis.atlassian.net/rest/servicedeskapi";
  private static final String serviceDeskId = "1";
  private static final String userEmail = "user@siglofa.com";
  private static final String token = "token";

  private AttachmentService service;

  private String encodedString;

  @Before
  public void setUp() {
    super.setUp();
    service = (AttachmentService) prepareService();

    ReflectionTestUtils.setField(service, "serviceDeskUrl", serviceDeskUrl);
    ReflectionTestUtils.setField(service, "serviceDeskId", serviceDeskId);
    ReflectionTestUtils.setField(service, "userEmail", userEmail);
    ReflectionTestUtils.setField(service, "token", token);

    encodedString = Base64.getEncoder()
        .encodeToString(String.format("%s:%s", userEmail, token).getBytes());
  }

  @Test
  public void shouldSubmitCustomerRequest() throws IOException {
    TemporaryAttachmentResponse expectedResponse =
        new TemporaryAttachmentResponseDataBuilder().build();
    MultipartFile multipartFile1 = new MockMultipartFile("file1", "some-text".getBytes());
    MultipartFile multipartFile2 = new MockMultipartFile("file2", "other-text".getBytes());
    MultipartFile[] files = {multipartFile1, multipartFile2};

    given(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class),
        any(Class.class))).willReturn(ResponseEntity.ok(expectedResponse));

    TemporaryAttachmentResponse response = service.createTemporaryFiles(files).getBody();

    verifyRequest(1, TemporaryAttachmentResponse.class);
    assertEquals(expectedResponse, response);
    assertEquals(RequestHelper.createUri(
        String.format("%s/servicedesk/%s/attachTemporaryFile", serviceDeskUrl, serviceDeskId)),
        uriCaptor.getValue());
    assertNotNull(entityCaptor.getValue().getBody());
    assertAuthHeader(entityCaptor.getValue(), encodedString);
  }

  @Test
  public void shouldCreateAttachment() {
    int issueId = 1;
    AttachmentRequest attachmentRequest = generateInstance();

    given(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class),
        any(Class.class))).willReturn(ResponseEntity.ok("OK"));

    service.createAttachments(attachmentRequest, issueId);

    verifyRequest(1, Object.class);
    assertEquals(RequestHelper.createUri(
        String.format("%s/request/%s/attachment", serviceDeskUrl, issueId)),
        uriCaptor.getValue());
    assertNotNull(entityCaptor.getValue().getBody());
    assertEquals(attachmentRequest, entityCaptor.getValue().getBody());
    assertAuthHeader(entityCaptor.getValue(), encodedString);
  }

  @Override
  protected AttachmentRequest generateInstance() {
    return new AttachmentRequestDataBuilder().build();
  }

  protected BaseCommunicationService<AttachmentRequest> getService() {
    return new AttachmentService();
  }
}
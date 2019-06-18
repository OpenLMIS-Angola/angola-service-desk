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

import static org.openlmis.servicedesk.i18n.MessageKeys.ATTACHMENT_FAILED_TO_READ;
import static org.openlmis.servicedesk.util.RequestHelper.createUri;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openlmis.servicedesk.exception.ServiceDeskException;
import org.openlmis.servicedesk.exception.ValidationMessageException;
import org.openlmis.servicedesk.service.BaseCommunicationService;
import org.openlmis.servicedesk.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService extends BaseCommunicationService<AttachmentRequest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);

  @Value("${serviceDeskApi.url}")
  private String serviceDeskUrl;

  @Value("${serviceDeskApi.serviceDeskId}")
  private String serviceDeskId;

  @Value("${serviceDeskApi.userEmail}")
  private String userEmail;

  @Value("${serviceDeskApi.token}")
  private String token;

  /**
   * Attaches temporary file to Service Desk.
   *
   * @param multipartFile file to be attached
   * @return              temporary attachment response
   */
  public ResponseEntity<TemporaryAttachmentResponse> createTemporaryFiles(
      MultipartFile multipartFile) {
    LOGGER.info("Creating temporary attachment using Service Desk API: {}",
        multipartFile.getOriginalFilename());

    String url = String.format("%s/servicedesk/%s/attachTemporaryFile",
        serviceDeskUrl, serviceDeskId);

    Map<String, String> headers = new HashMap<>();
    headers.put("X-Atlassian-Token", "no-check");
    headers.put("Expect", "100-continue");
    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    try {
      formData.add("file", transformFile(multipartFile));
      return runWithRetry(() ->
          restTemplate.exchange(
              createUri(url),
              HttpMethod.POST,
              RequestHelper.createEntity(
                  formData, String.format("%s:%s", userEmail, token), true, headers),
              TemporaryAttachmentResponse.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Creating temporary attachment in Service Desk failed: {}",
          ex.getResponseBodyAsString());
      throw new ServiceDeskException(ex.getResponseBodyAsString(), ex);
    } catch (IOException ex) {
      LOGGER.error("Creating temporary attachment in Service Desk failed: {}",
          ex.getLocalizedMessage());
      throw new ValidationMessageException(ex, ATTACHMENT_FAILED_TO_READ);
    }
  }

  /**
   * Attaches file to Service Desk issue.
   *
   * @param attachmentRequest request to be send
   */
  public void createAttachments(AttachmentRequest attachmentRequest, int issueId) {
    LOGGER.info("Attaching file to issue using Service Desk API: {}", attachmentRequest);

    String url = String.format("%s/request/%s/attachment", serviceDeskUrl, issueId);

    try {
      runWithRetry(() ->
          restTemplate.exchange(
              createUri(url),
              HttpMethod.POST,
              RequestHelper.createEntity(attachmentRequest,
                  String.format("%s:%s", userEmail, token)),
              Object.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Sending customer request to Service Desk API failed: {}",
          ex.getResponseBodyAsString());
      throw new ServiceDeskException(ex.getResponseBodyAsString(), ex);
    }
  }

  private Object transformFile(MultipartFile file) throws IOException {
    return new ByteArrayResource(file.getBytes()) {

      @Override
      public String getFilename() {
        return file.getOriginalFilename();
      }

      @Override
      public long contentLength() {
        return file.getSize();
      }
    };
  }
}

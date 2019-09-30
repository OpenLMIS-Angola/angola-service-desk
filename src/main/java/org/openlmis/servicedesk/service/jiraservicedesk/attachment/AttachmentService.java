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

package org.openlmis.servicedesk.service.jiraservicedesk.attachment;

import static org.openlmis.servicedesk.i18n.MessageKeys.ATTACHMENT_FAILED_TO_READ;
import static org.openlmis.servicedesk.util.RequestHelper.createUri;

import java.io.IOException;
import org.openlmis.servicedesk.exception.ServiceDeskException;
import org.openlmis.servicedesk.exception.ValidationMessageException;
import org.openlmis.servicedesk.service.RequestHeaders;
import org.openlmis.servicedesk.service.jiraservicedesk.BaseJiraServiceDeskService;
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
public class AttachmentService extends BaseJiraServiceDeskService<AttachmentRequest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);

  @Value("${serviceDeskApi.serviceDeskId}")
  private String serviceDeskId;

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

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    try {
      formData.add("file", transformFile(multipartFile));
      return runWithRetry(() ->
          restTemplate.exchange(
              createUri(String.format("%s/servicedesk/%s/attachTemporaryFile",
                  getServiceUrl(), serviceDeskId)),
              HttpMethod.POST,
              RequestHelper.createEntity(
                  formData,
                  getJiraServiceDeskToken(),
                  true,
                  true,
                  addExpectHeader(
                      addAtlassianTokenHeader(RequestHeaders.init()))),
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

    try {
      runWithRetry(() ->
          restTemplate.exchange(
              createUri(String.format("%s/request/%s/attachment", getServiceUrl(), issueId)),
              HttpMethod.POST,
              RequestHelper.createEntity(attachmentRequest, getJiraServiceDeskToken(), true),
              Object.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Sending customer request to Service Desk API failed: {}",
          ex.getResponseBodyAsString());
      throw new ServiceDeskException(ex.getResponseBodyAsString(), ex);
    }
  }

  @Override
  protected String getUrl() {
    return getServiceUrl();
  }

  @Override
  protected Class<AttachmentRequest> getResultClass() {
    return AttachmentRequest.class;
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

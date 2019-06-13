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

import org.openlmis.servicedesk.service.attachment.AttachmentRequest;
import org.openlmis.servicedesk.service.attachment.AttachmentService;
import org.openlmis.servicedesk.service.attachment.TemporaryAttachmentResponse;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequest;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestBuilder;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestResponse;
import org.openlmis.servicedesk.service.customerrequest.CustomerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/issues")
public class IssueController {

  @Autowired
  private CustomerRequestBuilder customerRequestBuilder;

  @Autowired
  private CustomerRequestService customerRequestService;

  @Autowired
  private AttachmentService attachmentService;

  /**
   * Creates new Jra issue.
   *
   * @param issue reported issue
   * @return      created issue
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerRequestResponse createIssue(@RequestBody IssueDto issue) {
    CustomerRequest customerRequest = customerRequestBuilder.build(issue);
    return customerRequestService.submit(customerRequest).getBody();
  }

  /**
   * Attaches file to Service Desk issue.
   *
   * @param file    File to be attached
   * @param issueId Issue that file will be attached to
   */
  @PostMapping("{issueId}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void upload(@RequestPart("file") MultipartFile file,
      @RequestParam int issueId) {
    TemporaryAttachmentResponse response = attachmentService.attachTemporaryFile(file).getBody();
    attachmentService.createAttachment(
        new AttachmentRequest(response.findAttachment(file.getName()).getTemporaryAttachmentId()),
        issueId);
  }
}

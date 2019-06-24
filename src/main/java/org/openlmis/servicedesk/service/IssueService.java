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

import org.openlmis.servicedesk.domain.ServiceDeskCustomer;
import org.openlmis.servicedesk.reporitory.ServiceDeskCustomerRepository;
import org.openlmis.servicedesk.service.servicedesk.attachment.AttachmentRequest;
import org.openlmis.servicedesk.service.servicedesk.attachment.AttachmentService;
import org.openlmis.servicedesk.service.servicedesk.attachment.TemporaryAttachmentResponse;
import org.openlmis.servicedesk.service.servicedesk.customer.AddCustomersRequest;
import org.openlmis.servicedesk.service.servicedesk.customer.CreatedCustomer;
import org.openlmis.servicedesk.service.servicedesk.customer.Customer;
import org.openlmis.servicedesk.service.servicedesk.customer.CustomerService;
import org.openlmis.servicedesk.service.servicedesk.customer.CustomersResponse;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequest;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequestBuilder;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequestResponse;
import org.openlmis.servicedesk.service.servicedesk.customerrequest.CustomerRequestService;
import org.openlmis.servicedesk.web.issue.IssueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IssueService {

  @Autowired
  private ServiceDeskCustomerRepository serviceDeskCustomerRepository;

  @Autowired
  private CustomerService customerService;

  @Autowired
  private CustomerRequestBuilder customerRequestBuilder;

  @Autowired
  private CustomerRequestService customerRequestService;

  @Autowired
  private AttachmentService attachmentService;

  /**
   * Creates Service Desk customer request from issue send by user.
   *
   * @param  issue dto data send by user
   * @return       request ready to send to Service Desk
   */
  public CustomerRequest prepareCustomerRequest(IssueDto issue) {
    ServiceDeskCustomer customer = serviceDeskCustomerRepository
        .findByEmail(issue.getEmail())
        .orElseGet(() -> createNewCustomer(issue.getEmail(), issue.getDisplayName()));

    return customerRequestBuilder.build(issue, customer == null ? null : customer.getCustomerId());
  }

  /**
   * Sends customer request to Service Desk API.
   *
   * @param  customerRequest request body
   * @return                 Service Desk response
   */
  public CustomerRequestResponse sendCustomerRequest(CustomerRequest customerRequest) {
    return customerRequestService.submit(customerRequest).getBody();
  }

  /**
   * Attaches file to Service Desk request.
   *
   * @param file    multipart file to be attached
   * @param issueId id of a already existing request
   */
  public void attachFile(MultipartFile file, int issueId) {
    TemporaryAttachmentResponse response = attachmentService.createTemporaryFiles(file).getBody();
    attachmentService.createAttachments(
        new AttachmentRequest(
            response.findAttachment(file.getOriginalFilename()).getTemporaryAttachmentId()),
        issueId);
  }

  private ServiceDeskCustomer createNewCustomer(String email, String displayName) {
    if (email == null) {
      return null;
    }

    CreatedCustomer customer = customerService.create(new Customer(displayName, email)).getBody();
    if (customer == null) {
      CustomersResponse response = customerService.getServiceDeskCustomers(email).getBody();
      customer = response.findByEmail(email);
    } else {
      customerService.addToServiceDesk(new AddCustomersRequest(customer.getEmailAddress()));
    }
    return serviceDeskCustomerRepository.save(
        new ServiceDeskCustomer(customer.getEmailAddress(), customer.getAccountId()));
  }
}

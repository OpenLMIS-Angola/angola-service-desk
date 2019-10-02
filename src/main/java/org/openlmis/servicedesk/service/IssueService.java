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

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.openlmis.servicedesk.i18n.MessageKeys.CANNOT_FIND_AND_CREATE_CUSTOMER_WITH_EMAIL;
import static org.openlmis.servicedesk.i18n.MessageKeys.CURRENT_USER_HAS_NO_EMAIL;

import org.openlmis.servicedesk.domain.ServiceDeskCustomer;
import org.openlmis.servicedesk.exception.ValidationMessageException;
import org.openlmis.servicedesk.reporitory.ServiceDeskCustomerRepository;
import org.openlmis.servicedesk.security.AuthenticationHelper;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.AttachmentRequest;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.AttachmentService;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.TemporaryAttachmentResponse;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.AddCustomersRequest;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CreatedCustomer;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.Customer;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CustomerService;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CustomersResponse;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequest;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestResponse;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestService;
import org.openlmis.servicedesk.service.notification.UserContactDetailsDto;
import org.openlmis.servicedesk.service.referencedata.UserDto;
import org.openlmis.servicedesk.util.Message;
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

  @Autowired
  private AuthenticationHelper authenticationHelper;

  /**
   * Creates Service Desk customer request from issue send by user.
   *
   * @param  issue dto data send by user
   * @return       request ready to send to Service Desk
   */
  public CustomerRequest prepareCustomerRequest(IssueDto issue) {
    UserDto user = authenticationHelper.getCurrentUser();
    UserContactDetailsDto userContactDetails = authenticationHelper.getCurrentUserContactDetails();

    String email = userContactDetails.getEmailDetails().getEmail();
    if (isBlank(email)) {
      throw new ValidationMessageException(CURRENT_USER_HAS_NO_EMAIL);
    }

    ServiceDeskCustomer customer = serviceDeskCustomerRepository
        .findByEmail(email)
        .orElseGet(() -> createNewCustomer(email, user.getDisplayName()));

    return customerRequestBuilder.build(
        issue,
        customer == null ? null : customer.getCustomerId(),
        email,
        user.getUsername());
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
      CustomersResponse customers = customerService.getServiceDeskCustomers(email).getBody();
      if (isEmpty(customers.getValues())) {
        throw new ValidationMessageException(
            new Message(CANNOT_FIND_AND_CREATE_CUSTOMER_WITH_EMAIL, email));
      }
      customer = customers.getValues().get(0);
      customer.setEmailAddress(email);
    } else {
      customerService.addToServiceDesk(new AddCustomersRequest(customer.getAccountId()));
    }
    return serviceDeskCustomerRepository.save(
        new ServiceDeskCustomer(customer.getEmailAddress(), customer.getAccountId()));
  }
}

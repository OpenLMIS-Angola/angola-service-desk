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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.servicedesk.i18n.MessageKeys.CANNOT_FIND_AND_CREATE_CUSTOMER_WITH_EMAIL;
import static org.openlmis.servicedesk.i18n.MessageKeys.CURRENT_USER_HAS_NO_EMAIL;

import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.servicedesk.domain.ServiceDeskCustomer;
import org.openlmis.servicedesk.domain.ServiceDeskCustomerDataBuilder;
import org.openlmis.servicedesk.exception.ValidationMessageException;
import org.openlmis.servicedesk.reporitory.ServiceDeskCustomerRepository;
import org.openlmis.servicedesk.security.AuthenticationHelper;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.AttachmentRequest;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.AttachmentService;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.TemporaryAttachmentDataBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.TemporaryAttachmentResponse;
import org.openlmis.servicedesk.service.jiraservicedesk.attachment.TemporaryAttachmentResponseDataBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CreatedCustomer;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CreatedCustomerDataBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CustomerService;
import org.openlmis.servicedesk.service.jiraservicedesk.customer.CustomersResponseDataBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequest;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestDataBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestResponse;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestResponseDataBuilder;
import org.openlmis.servicedesk.service.jiraservicedesk.customerrequest.CustomerRequestService;
import org.openlmis.servicedesk.service.notification.UserContactDetailsDto;
import org.openlmis.servicedesk.service.notification.UserContactDetailsDtoDataBuilder;
import org.openlmis.servicedesk.service.referencedata.UserDto;
import org.openlmis.servicedesk.service.refernecedata.UserDtoDataBuilder;
import org.openlmis.servicedesk.util.Message;
import org.openlmis.servicedesk.web.issue.IssueDto;
import org.openlmis.servicedesk.web.issue.IssueDtoDataBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ServiceDeskCustomerRepository serviceDeskCustomerRepository;

  @Mock
  private CustomerService customerService;

  @Mock
  private CustomerRequestBuilder customerRequestBuilder;

  @Mock
  private CustomerRequestService customerRequestService;

  @Mock
  private AttachmentService attachmentService;

  @Mock
  private AuthenticationHelper authenticationHelper;

  @InjectMocks
  private IssueService issueService;

  private UserDto user = new UserDtoDataBuilder().build();
  private UserContactDetailsDto userContactDetails = new UserContactDetailsDtoDataBuilder()
      .withReferenceDataUserId(user.getId())
      .build();
  private String email = userContactDetails.getEmailDetails().getEmail();
  private IssueDto issue = new IssueDtoDataBuilder().build();
  private CustomerRequest customerRequest = new CustomerRequestDataBuilder().build();
  private ServiceDeskCustomer savedCustomer = new ServiceDeskCustomerDataBuilder().build();

  @Before
  public void setUp() {
    when(authenticationHelper.getCurrentUser()).thenReturn(user);
    when(authenticationHelper.getCurrentUserContactDetails()).thenReturn(userContactDetails);

    when(serviceDeskCustomerRepository.save(any(ServiceDeskCustomer.class)))
        .thenReturn(savedCustomer);

    when(customerRequestBuilder.build(
        eq(issue),
        eq(savedCustomer.getCustomerId()),
        eq(email),
        eq(user.getUsername())))
        .thenReturn(customerRequest);
  }

  @Test
  public void shouldPrepareCustomerRequestWithCustomerFromDataBase() {
    when(serviceDeskCustomerRepository.findByEmail(email))
        .thenReturn(Optional.of(savedCustomer));

    assertThat(issueService.prepareCustomerRequest(issue), is(customerRequest));
  }

  @Test
  public void shouldPrepareCustomerRequestAndCreateNewCustomer() {
    when(serviceDeskCustomerRepository.findByEmail(email)).thenReturn(Optional.empty());

    when(customerService.create(anyObject()))
        .thenReturn(ResponseEntity.ok(new CreatedCustomerDataBuilder().build()));

    assertThat(issueService.prepareCustomerRequest(issue), is(customerRequest));
    verify(customerService).addToServiceDesk(any());
    verify(serviceDeskCustomerRepository).save(any(ServiceDeskCustomer.class));
  }

  @Test
  public void shouldPrepareCustomerRequestAndGetExistingCustomerFromServiceDesk() {
    when(serviceDeskCustomerRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(customerService.create(anyObject())).thenReturn(ResponseEntity.badRequest().build());

    CreatedCustomer createdCustomer = new CreatedCustomerDataBuilder()
        .withEmailAddress(userContactDetails.getEmailDetails().getEmail())
        .build();
    when(customerService.getServiceDeskCustomers(email)).thenReturn(ResponseEntity.ok(
        new CustomersResponseDataBuilder().withCreatedCustomer(createdCustomer).build()));

    assertThat(issueService.prepareCustomerRequest(issue), is(customerRequest));
    verify(serviceDeskCustomerRepository).save(any(ServiceDeskCustomer.class));
  }

  @Test
  public void shouldThrowExceptionIfCannotCreateAndFindCustomerWithEmail() {
    expectedException.expect(ValidationMessageException.class);
    expectedException
        .expectMessage(new Message(CANNOT_FIND_AND_CREATE_CUSTOMER_WITH_EMAIL, email).toString());

    when(serviceDeskCustomerRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(customerService.create(anyObject())).thenReturn(ResponseEntity.badRequest().build());
    when(customerService.getServiceDeskCustomers(email)).thenReturn(ResponseEntity.ok(
        new CustomersResponseDataBuilder().withEmptyCustomers().build()));

    issueService.prepareCustomerRequest(issue);
  }

  @Test
  public void shouldThrowExceptionIfUserHasNoEmail() {
    expectedException.expect(ValidationMessageException.class);
    expectedException.expectMessage(new Message(CURRENT_USER_HAS_NO_EMAIL).toString());

    userContactDetails.getEmailDetails().setEmail(null);

    issueService.prepareCustomerRequest(issue);
  }

  @Test
  public void shouldSendCustomerRequest() {
    CustomerRequestResponse response = new CustomerRequestResponseDataBuilder().build();
    when(customerRequestService.submit(customerRequest)).thenReturn(ResponseEntity.ok(response));

    assertThat(issueService.sendCustomerRequest(customerRequest), is(response));
  }

  @Test
  public void shouldAttachFile() {
    String filename = "filename.txt";
    MockMultipartFile firstFile =
        new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
    int issueId = 1;

    TemporaryAttachmentResponse response = new TemporaryAttachmentResponseDataBuilder()
        .withTemporaryAttachment(new TemporaryAttachmentDataBuilder()
            .withFileName(filename)
            .build())
        .build();
    when(attachmentService.createTemporaryFiles(firstFile)).thenReturn(ResponseEntity.ok(response));

    issueService.attachFile(firstFile, issueId);

    verify(attachmentService).createTemporaryFiles(firstFile);
    verify(attachmentService).createAttachments(any(AttachmentRequest.class), eq(issueId));
  }
}

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

package org.openlmis.servicedesk.service.servicedesk.customerrequest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.servicedesk.web.issue.IssueDto;
import org.openlmis.servicedesk.web.issue.IssueDtoDataBuilder;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CustomerRequestBuilderTest {

  @InjectMocks
  private CustomerRequestBuilder customerRequestBuilder;

  private int serviceDeskId = 1;
  private String requestParticipant = "participant";
  private IssueDto issueDto = new IssueDtoDataBuilder().build();
  private CustomerRequest customerRequest = new CustomerRequestDataBuilder()
      .withServiceDeskId(serviceDeskId)
      .withRequestTypeId(issueDto.getType().getValue())
      .withRequestFieldValues(new RequestFieldValuesDataBuilder()
          .withSummary(issueDto.getSummary())
          .withDescription(issueDto.getDescription())
          .withImpact(new CustomField(issueDto.getImpact().getValue().toString()))
          .withPriority(new CustomField(issueDto.getPriority().getValue().toString()))
          .build())
      .withRaiseOnBehalfOf(requestParticipant)
      .build();

  @Before
  public void setUp() {
    ReflectionTestUtils.setField(customerRequestBuilder, "serviceDeskId", serviceDeskId);
  }

  @Test
  public void shouldBuildCustomerRequestFromIssueDto() throws Exception {
    assertEquals(customerRequest, customerRequestBuilder.build(issueDto, requestParticipant));
  }
}
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

package org.openlmis.servicedesk.service.customerrequest;

import static org.openlmis.servicedesk.util.RequestHelper.createUri;

import org.openlmis.servicedesk.exception.ServiceDeskException;
import org.openlmis.servicedesk.service.BaseCommunicationService;
import org.openlmis.servicedesk.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Service
public class CustomerRequestService extends BaseCommunicationService<CustomerRequest> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRequestService.class);

  @Value("${serviceDeskApi.url}")
  private String serviceDeskUrl;

  @Value("${serviceDeskApi.userEmail}")
  private String userEmail;

  @Value("${serviceDeskApi.token}")
  private String token;

  /**
   * Submits Customer Requests to Service Desk API.
   *
   * @param  customerRequest request to be send
   * @return                 Service Desk response
   */
  public ResponseEntity<CustomerRequestResponse> submit(CustomerRequest customerRequest) {
    LOGGER.info("Creating customer request using Service Desk API: {}", customerRequest);

    String url = serviceDeskUrl + "/request";

    try {
      return runWithRetry(() ->
          restTemplate.exchange(
              createUri(url),
              HttpMethod.POST,
              RequestHelper.createEntity(customerRequest, String.format("%s:%s", userEmail, token)),
              CustomerRequestResponse.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Sending customer request to Service Desk API failed: {}",
          ex.getResponseBodyAsString());
      throw new ServiceDeskException(ex.getResponseBodyAsString(), ex);
    }
  }
}

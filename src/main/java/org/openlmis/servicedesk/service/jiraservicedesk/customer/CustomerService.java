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

package org.openlmis.servicedesk.service.jiraservicedesk.customer;

import static org.openlmis.servicedesk.i18n.MessageKeys.CANNOT_ADD_CUSTOMER_TO_SERVICE_DESK;
import static org.openlmis.servicedesk.util.RequestHelper.createUri;

import org.openlmis.servicedesk.exception.ServiceDeskException;
import org.openlmis.servicedesk.service.RequestHeaders;
import org.openlmis.servicedesk.service.RequestParameters;
import org.openlmis.servicedesk.service.jiraservicedesk.BaseJiraServiceDeskService;
import org.openlmis.servicedesk.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Service
public class CustomerService extends BaseJiraServiceDeskService<Customer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

  @Value("${serviceDeskApi.serviceDeskId}")
  private String serviceDeskId;

  /**
   * Creates customer using Service Desk API.
   *
   * @param  customer customer to be created
   * @return          Service Desk response
   */
  public ResponseEntity<CreatedCustomer> create(Customer customer) {
    LOGGER.info("Creating customer using Service Desk API: {}", customer);

    try {
      return runWithRetry(() ->
          restTemplate.exchange(
              createUri(getServiceUrl() + "/customer"),
              HttpMethod.POST,
              RequestHelper.createEntity(
                  customer,
                  getJiraServiceDeskToken(),
                  true,
                  false,
                  addAtlassianTokenHeader(RequestHeaders.init())),
              CreatedCustomer.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Creating customer using Service Desk API failed: {}",
          ex.getResponseBodyAsString());
      return ResponseEntity.ok(null);
    }
  }

  /**
   * Adds created customers to specific Service Desk using its API.
   *
   * @param  addCustomersRequest object with list of customer account ids
   */
  public void addToServiceDesk(AddCustomersRequest addCustomersRequest) {
    LOGGER.info("Adding customer to service desk using Service Desk API: {}", addCustomersRequest);

    try {
      runWithRetry(() ->
          restTemplate.exchange(
              createUri(
                  String.format("%s/servicedesk/%s/customer", getServiceUrl(), serviceDeskId)),
              HttpMethod.POST,
              RequestHelper.createEntity(
                  addCustomersRequest,
                  getJiraServiceDeskToken(),
                  true,
                  false,
                  addAtlassianTokenHeader(RequestHeaders.init())),
              Object.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Failed to add customer to Service Desk: {}",
          ex.getResponseBodyAsString());
      throw new ServiceDeskException(CANNOT_ADD_CUSTOMER_TO_SERVICE_DESK, ex);
    }
  }

  /**
   * Gets all customers using Service Desk API.
   *
   * @return list of Service Desk customers
   */
  public ResponseEntity<CustomersResponse> getServiceDeskCustomers(String email) {
    LOGGER.info("Getting customers from Service Desk by email: {}", email);

    try {
      return runWithRetry(() ->
          restTemplate.exchange(
              createUri(
                  String.format("%s/servicedesk/%s/customer", getServiceUrl(), serviceDeskId),
                  RequestParameters.init().set("query", email)),
              HttpMethod.GET,
              RequestHelper.createEntity(
                  getJiraServiceDeskToken(),
                  true,
                  addExperimentalApiHeader(RequestHeaders.init())),
              CustomersResponse.class
          ));
    } catch (HttpStatusCodeException ex) {
      LOGGER.error("Creating customer using Service Desk API failed: {}",
          ex.getResponseBodyAsString());
      return null;
    }
  }

  @Override
  protected String getUrl() {
    return getServiceUrl();
  }

  @Override
  protected Class<Customer> getResultClass() {
    return Customer.class;
  }
}

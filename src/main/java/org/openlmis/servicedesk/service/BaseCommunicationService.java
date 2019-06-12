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

import static org.openlmis.servicedesk.i18n.MessageKeys.ERROR_SERVICE_OCCURED;
import static org.openlmis.servicedesk.i18n.MessageKeys.ERROR_SERVICE_REQUIRED;

import org.openlmis.servicedesk.exception.DataRetrievalException;
import org.openlmis.servicedesk.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD.TooManyMethods")
public abstract class BaseCommunicationService<T> {

  protected RestOperations restTemplate = new RestTemplate();

  protected abstract Class<T> getResultClass();

  protected abstract String getServiceName();

  protected <P> ResponseEntity<P> runWithRetry(HttpTask<P> task) {
    try {
      return task.run();
    } catch (HttpStatusCodeException ex) {
      if (ex.getStatusCode().is4xxClientError() || ex.getStatusCode().is5xxServerError()) {
        return task.run();
      }
      throw ex;
    }
  }

  @FunctionalInterface
  protected interface HttpTask<T> {
    ResponseEntity<T> run();
  }

  protected DataRetrievalException buildDataRetrievalException(HttpStatusCodeException ex) {
    String errorKey;
    if (ex.getStatusCode().is5xxServerError() || ex.getStatusCode() == HttpStatus.NOT_FOUND) {
      errorKey = ERROR_SERVICE_REQUIRED;
    } else {
      errorKey = ERROR_SERVICE_OCCURED;
    }
    return new DataRetrievalException(
        new Message(errorKey, getServiceName()),
        getResultClass().getSimpleName(),
        ex.getStatusCode(),
        ex.getResponseBodyAsString());
  }

  void setRestTemplate(RestOperations template) {
    this.restTemplate = template;
  }
}

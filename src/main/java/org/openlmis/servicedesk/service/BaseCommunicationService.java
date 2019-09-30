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

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.servicedesk.exception.DataRetrievalException;
import org.openlmis.servicedesk.service.auth.AuthService;
import org.openlmis.servicedesk.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public abstract class BaseCommunicationService<T> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AuthService authService;

  protected abstract String getServiceUrl();

  protected abstract String getUrl();

  protected abstract Class<T> getResultClass();

  protected RestOperations restTemplate = new RestTemplate();

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

  /**
   * Return one object from service.
   *
   * @param id UUID of requesting object.
   * @return Requesting reference data object.
   */
  public T findOne(UUID id) {
    return findOne(id.toString(), RequestParameters.init());
  }

  /**
   * Return one object from service.
   *
   * @param resourceUrl Endpoint url.
   * @param parameters  Map of query parameters.
   * @return one reference data T objects.
   */
  public T findOne(String resourceUrl, RequestParameters parameters) {
    return findOne(resourceUrl, parameters, getResultClass());
  }

  /**
   * Return one object from service.
   *
   * @param resourceUrl Endpoint url.
   * @param parameters  Map of query parameters.
   * @param type        set to what type a response should be converted.
   * @return one reference data T objects.
   */
  protected T findOne(String resourceUrl, RequestParameters parameters, Class<T> type) {
    String url = getServiceUrl() + getUrl() + StringUtils.defaultIfBlank(resourceUrl, "");

    RequestParameters params = RequestParameters
        .init()
        .setAll(parameters);

    try {
      ResponseEntity<T> responseEntity = restTemplate.exchange(
          RequestHelper.createUri(url, params),
          HttpMethod.GET,
          RequestHelper.createEntity(authService.obtainAccessToken()),
          type);
      return responseEntity.getBody();
    } catch (HttpStatusCodeException ex) {
      if (HttpStatus.NOT_FOUND == ex.getStatusCode()) {
        logger.warn(
            "{} matching params does not exist. Params: {}",
            getResultClass().getSimpleName(), parameters
        );

        return null;
      } else {
        throw buildDataRetrievalException(ex);
      }
    }
  }

  protected DataRetrievalException buildDataRetrievalException(HttpStatusCodeException ex) {
    return new DataRetrievalException(getResultClass().getSimpleName(), ex);
  }

  @FunctionalInterface
  protected interface HttpTask<T> {
    ResponseEntity<T> run();
  }
}

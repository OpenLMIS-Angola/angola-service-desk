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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class RequestHeaders {

  private Map<String, String> headers = Maps.newHashMap();
  private MediaType contentType;
  private List<MediaType> accept = new ArrayList<>();

  private RequestHeaders() {
  }

  public static RequestHeaders init() {
    return new RequestHeaders();
  }

  /**
   * Sets Authorization header.
   *
   * @param  token       encoded token value
   * @param  isBasicAuth if true "Basic " will be used as prefix for token, "Bearer " otherwise
   * @return             request headers with Authorization header added
   */
  public RequestHeaders setAuth(String token, boolean isBasicAuth) {
    return isNotBlank(token)
        ? set(HttpHeaders.AUTHORIZATION, (isBasicAuth ? "Basic " : "Bearer ") + token)
        : this;
  }

  /**
   * Set parameter (key argument) with the value only if the value is not null.
   */
  public RequestHeaders set(String key, String value) {
    if (isNotBlank(value)) {
      headers.put(key, value);
    }

    return this;
  }

  /**
   * Sets Content-Type header.
   *
   * @param  contentType Content-Type header value
   * @return             request headers object with Content-Type set
   */
  public RequestHeaders setContentType(MediaType contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * Sets Accept header.
   *
   * @param  accept Accept header value
   * @return        request headers object with Accept set
   */
  public RequestHeaders addAccept(MediaType accept) {
    this.accept.add(accept);
    return this;
  }

  /**
   * Converts this instance to {@link org.springframework.http.HttpHeaders}.
   */
  public HttpHeaders toHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (accept != null) {
      httpHeaders.setAccept(accept);
    }
    if (contentType != null) {
      httpHeaders.setContentType(contentType);
    }
    forEach((entry -> httpHeaders.set(entry.getKey(), entry.getValue())));
    return httpHeaders;
  }

  void forEach(Consumer<Map.Entry<String, String>> action) {
    headers.entrySet().forEach(action);
  }
}

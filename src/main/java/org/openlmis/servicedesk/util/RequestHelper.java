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

package org.openlmis.servicedesk.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.openlmis.servicedesk.exception.EncodingException;
import org.openlmis.servicedesk.service.RequestHeaders;
import org.openlmis.servicedesk.service.RequestParameters;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@SuppressWarnings("PMD.TooManyMethods")
public final class RequestHelper {

  private RequestHelper() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a {@link URI} from the given string representation without any parameters.
   */
  public static URI createUri(String url) {
    return createUri(url, null);
  }

  /**
   * Creates a {@link URI} from the given string representation and with the given parameters.
   */
  public static URI createUri(String url, RequestParameters parameters) {
    UriComponentsBuilder builder = UriComponentsBuilder.newInstance().uri(URI.create(url));

    RequestParameters
        .init()
        .setAll(parameters)
        .forEach(e -> e.getValue().forEach(one -> {
          try {
            builder.queryParam(e.getKey(),
                UriUtils.encodeQueryParam(String.valueOf(one),
                    StandardCharsets.UTF_8.name()));
          } catch (UnsupportedEncodingException ex) {
            throw new EncodingException(ex);
          }
        }));

    return builder.build(true).toUri();
  }

  /**
   * Creates an {@link HttpEntity} with the given payload as a body and adds an authorization
   * header with the provided token.
   *
   * @param  payload   the body of the request, pass null if no body
   * @param  token     the token to put into the authorization header
   * @param  <E>       the type of the body for the request
   * @param  isBasic   specifies if authorization is Basic or Bearer
   * @param  multipart if true multipart/form-data header will be set, otherwise application/json
   * @param  headers   additional http headers
   * @return           the {@link HttpEntity} to use
   */
  public static <E> HttpEntity<E> createEntity(
      E payload, String token, boolean isBasic, boolean multipart, RequestHeaders headers) {

    if (payload == null) {
      return createEntity(createHeaders(token, isBasic));
    } else {
      return createEntity(payload, createHeaders(token, isBasic, multipart, headers));
    }
  }

  /**
   * Creates an {@link HttpEntity} with an authorization header with the provided token.
   *
   * @param  token          the token to put into the authorization header
   * @param  isBasic        is token Basic or Bearer
   * @param  requestHeaders additional Http headers
   * @return                the {@link HttpEntity} to use
   */
  public static <E> HttpEntity<E> createEntity(
      String token, boolean isBasic, RequestHeaders requestHeaders) {
    return createEntity(null, createHeaders(token, isBasic, requestHeaders));
  }

  /**
   * Creates an {@link HttpEntity} with an authorization header with the provided token.
   *
   * @param token the token to put into the authorization header
   * @param <E> the type of the body for the request
   * @return the {@link HttpEntity} to use
   */
  public static <E> HttpEntity<E> createEntity(String token) {
    return createEntity(null, createHeaders(token));
  }

  /**
   * Creates an {@link HttpEntity} with the given payload as a body and adds an authorization
   * header with the provided token.
   *
   * @param token   true if authorization is basic, bearer otherwise
   * @param isBasic the token to put into the authorization header
   * @param payload the body of the request, pass null if no body
   * @param <E>     the type of the body for the request
   * @return        the {@link HttpEntity} to use
   */
  public static <E> HttpEntity<E> createEntity(E payload, String token, boolean isBasic) {
    if (payload == null) {
      return createEntity(createHeaders(token, isBasic));
    } else {
      return createEntity(payload, createHeaders(token, isBasic));
    }
  }

  /**
   * Creates an {@link HttpEntity} with the given payload as a body and headers.
   */
  public static <E> HttpEntity<E> createEntity(E payload, RequestHeaders headers) {
    return new HttpEntity<>(payload, headers.toHeaders());
  }

  /**
   * Creates an {@link HttpEntity} with the given headers.
   */
  public static <E> HttpEntity<E> createEntity(RequestHeaders headers) {
    return new HttpEntity<>(headers.toHeaders());
  }

  /**
   * Encodes credentials to token using base64 encoding.
   *
   * @param  credentials username and password separated by ":"
   * @return             encoded token
   */
  public static String encodeToken(String credentials) {
    return Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  private static RequestHeaders createHeaders(String token) {
    return createHeaders(token, false);
  }

  private static RequestHeaders createHeaders(String token, boolean isBasicAuth) {
    return createHeaders(token, isBasicAuth, false, null);
  }

  private static RequestHeaders createHeaders(
      String token, boolean isBasicAuth, RequestHeaders requestHeaders) {
    return createHeaders(token, isBasicAuth, false, requestHeaders);
  }

  private static RequestHeaders createHeaders(
      String token, boolean isBasicAuth, boolean isMultipart, RequestHeaders requestHeaders) {

    return (requestHeaders == null ? RequestHeaders.init() : requestHeaders)
        .setAuth(token, isBasicAuth)
        .setContentType(isMultipart ? MediaType.MULTIPART_FORM_DATA : MediaType.APPLICATION_JSON)
        .addAccept(MediaType.APPLICATION_JSON);
  }
}

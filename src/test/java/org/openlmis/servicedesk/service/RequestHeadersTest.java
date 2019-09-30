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

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RequestHeadersTest {

  @Test
  public void shouldSetParameter() {
    RequestHeaders params = RequestHeaders
        .init()
        .set("a", "b")
        .setAuth("token", false);

    Map<String, Object> map = toMap(params);
    assertThat(map, hasEntry("a", "b"));
    assertThat(map, hasEntry(AUTHORIZATION, "Bearer token"));
  }

  @Test
  public void shouldNotSetParametersValueIsNull() {
    RequestHeaders params = RequestHeaders
        .init()
        .set("a", null)
        .setAuth(null, false);

    Map<String, Object> map = toMap(params);
    assertThat(map, not(hasKey("a")));
    assertThat(map, not(hasKey(AUTHORIZATION)));
  }

  @Test
  public void shouldSetBasicAuth() {
    RequestHeaders params = RequestHeaders
        .init()
        .set("a", "b")
        .setAuth("token", true);

    Map<String, Object> map = toMap(params);
    assertThat(map, hasEntry("a", "b"));
    assertThat(map, hasEntry(AUTHORIZATION, "Basic token"));
  }

  @Test
  public void shouldSetContentTypeHeader() {
    RequestHeaders params = RequestHeaders
        .init()
        .setContentType(MediaType.APPLICATION_JSON)
        .setAuth(null, false);

    HttpHeaders headers = params.toHeaders();
    assertThat(headers.getContentType(), is(MediaType.APPLICATION_JSON));
  }

  @Test
  public void shouldSetAcceptHeader() {
    RequestHeaders params = RequestHeaders
        .init()
        .addAccept(MediaType.MULTIPART_FORM_DATA)
        .setAuth(null, false);

    HttpHeaders headers = params.toHeaders();
    assertThat(headers.getAccept(), hasItem(MediaType.MULTIPART_FORM_DATA));
  }

  @Test
  public void shouldReturnHeaders() {
    RequestHeaders params = RequestHeaders
        .init()
        .set("a", "b")
        .setContentType(MediaType.APPLICATION_JSON);

    HttpHeaders headers = params.toHeaders();

    assertThat(headers.containsKey("a"), is(true));
    assertThat(headers.getContentType(), is(MediaType.APPLICATION_JSON));
  }

  private Map<String, Object> toMap(RequestHeaders parameters) {
    Map<String, Object> map = Maps.newHashMap();
    parameters.forEach(e -> map.put(e.getKey(), e.getValue()));

    return map;
  }
}

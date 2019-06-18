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

package org.openlmis.servicedesk.errorhandling;

import org.openlmis.servicedesk.exception.NotFoundException;
import org.openlmis.servicedesk.exception.ServiceDeskException;
import org.openlmis.servicedesk.exception.ValidationMessageException;
import org.openlmis.servicedesk.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global error handling for all controllers in the service.
 * Contains common error handling mappings.
 */
@ControllerAdvice
public class GlobalErrorHandling extends AbstractErrorHandling {

  /**
   * Handles not found exceptions and returns status 404 Not Found.
   *
   * @param ex the NotFoundException to handle
   * @return the error response for the user
   */
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public Message.LocalizedMessage handleNotFoundException(NotFoundException ex) {
    return getLocalizedMessage(ex);
  }

  /**
   * Handles Message exceptions and returns status 400 Bad Request.
   *
   * @param ex the ValidationMessageException to handle
   * @return the error response for the user
   */
  @ExceptionHandler(ValidationMessageException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Message.LocalizedMessage handleMessageException(ValidationMessageException ex) {
    return getLocalizedMessage(ex);
  }

  /**
   * Handles Message exceptions and returns status 500.
   *
   * @param ex the DataRetrievalException to handle
   * @return the error response for the user
   */
  @ExceptionHandler(ServiceDeskException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleServiceDeskException(ServiceDeskException ex) {
    return ex.getMessage();
  }
}

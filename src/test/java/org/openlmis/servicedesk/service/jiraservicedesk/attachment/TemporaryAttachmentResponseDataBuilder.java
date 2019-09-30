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

package org.openlmis.servicedesk.service.jiraservicedesk.attachment;

import java.util.ArrayList;
import java.util.List;

public class TemporaryAttachmentResponseDataBuilder {

  private List<TemporaryAttachment> temporaryAttachments;

  /**
   * Constructor for {@link TemporaryAttachmentResponseDataBuilder}.
   * Sets default values for new instance of {@link TemporaryAttachmentResponse} class.
   */
  public TemporaryAttachmentResponseDataBuilder() {
    temporaryAttachments = new ArrayList<>();
    temporaryAttachments.add(new TemporaryAttachmentDataBuilder().build());
  }

  public TemporaryAttachmentResponseDataBuilder withTemporaryAttachment(
      TemporaryAttachment temporaryAttachment) {
    temporaryAttachments.add(temporaryAttachment);
    return this;
  }

  public TemporaryAttachmentResponse build() {
    return new TemporaryAttachmentResponse(temporaryAttachments);
  }
}

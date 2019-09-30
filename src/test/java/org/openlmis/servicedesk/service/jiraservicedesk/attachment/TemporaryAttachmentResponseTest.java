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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.openlmis.servicedesk.i18n.MessageKeys.ATTACHMENT_NOT_FOUND;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.servicedesk.ToStringTestUtils;
import org.openlmis.servicedesk.exception.NotFoundException;
import org.openlmis.servicedesk.util.Message;

public class TemporaryAttachmentResponseTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Test
  public void equalsContract() {
    EqualsVerifier
        .forClass(TemporaryAttachmentResponse.class)
        .withRedefinedSuperclass()
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  public void shouldImplementToString() {
    TemporaryAttachmentResponse temporaryAttachmentResponse =
        new TemporaryAttachmentResponseDataBuilder().build();

    ToStringTestUtils.verify(TemporaryAttachmentResponse.class, temporaryAttachmentResponse);
  }

  @Test
  public void shouldFindProperAttachment() {
    TemporaryAttachment correctAttachment = new TemporaryAttachmentDataBuilder()
        .withFileName("correctFile")
        .build();
    TemporaryAttachmentResponse temporaryAttachmentResponse =
        new TemporaryAttachmentResponseDataBuilder()
            .withTemporaryAttachment(correctAttachment)
            .withTemporaryAttachment(new TemporaryAttachmentDataBuilder()
                .withFileName("incorrectFile")
                .build())
            .build();

    assertThat(temporaryAttachmentResponse.findAttachment("correctFile"),
        equalTo(correctAttachment));
  }

  @Test
  public void shouldReturnEmptyListIfAttachmentIsNotFound() {
    String missingFile = "MISSING";

    expectedException.expect(NotFoundException.class);
    expectedException.expectMessage(new Message(ATTACHMENT_NOT_FOUND, missingFile).toString());

    TemporaryAttachmentResponse temporaryAttachmentResponse =
        new TemporaryAttachmentResponseDataBuilder().build();

    temporaryAttachmentResponse.findAttachment(missingFile);
  }
}
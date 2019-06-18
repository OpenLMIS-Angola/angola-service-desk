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

package org.openlmis.servicedesk.service.attachment;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.servicedesk.ToStringTestUtils;

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

    assertThat(temporaryAttachmentResponse.findAttachments(singletonList("correctFile")),
        hasItems(correctAttachment));
  }

  @Test
  public void shouldFindProperAttachments() {
    TemporaryAttachment correctAttachment1 = new TemporaryAttachmentDataBuilder()
        .withFileName("correctFile1")
        .build();
    TemporaryAttachment correctAttachment2 = new TemporaryAttachmentDataBuilder()
        .withFileName("correctFile2")
        .build();
    TemporaryAttachmentResponse temporaryAttachmentResponse =
        new TemporaryAttachmentResponseDataBuilder()
            .withTemporaryAttachment(correctAttachment1)
            .withTemporaryAttachment(correctAttachment2)
            .withTemporaryAttachment(new TemporaryAttachmentDataBuilder()
                .withFileName("incorrectFile")
                .build())
            .build();

    assertThat(temporaryAttachmentResponse.findAttachments(asList("correctFile1", "correctFile2")),
        hasItems(correctAttachment1, correctAttachment2));
  }

  @Test
  public void shouldReturnEmptyListIfAttachmentIsNotFound() {
    String missingFile = "MISSING";

    TemporaryAttachmentResponse temporaryAttachmentResponse =
        new TemporaryAttachmentResponseDataBuilder().build();

    assertEquals(0, temporaryAttachmentResponse.findAttachments(singletonList(missingFile)).size());
  }
}
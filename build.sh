#!/bin/sh

# Sync with Transifex
/transifex/sync_transifex.sh \
  --resource service-desk.messages \
  --pattern 'src/main/resources/messages_<lang>.properties' \
  --source-file src/main/resources/messages_en.properties

# Run Gradle build
gradle clean build

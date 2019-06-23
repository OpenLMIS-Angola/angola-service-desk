FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/schema /schema
COPY build/consul /consul

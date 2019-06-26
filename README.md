# OpenLMIS Angola Service Desk
Service for managing communication with Service Desk API for reporting issues.

## Workflow

This service is build to connect with Jira Service Desk using its API.
[Service Desk API documentation](https://developer.atlassian.com/cloud/jira/service-desk/rest/#api-rest-servicedeskapi-request-post)

This service exposes 2 POST endpoints for managing reporting issues. Please look into [API definition](./src/main/resources/api-definition.yaml) for more details.

### Creating new issue

To create new issue users can use POST "/api/issues" endpoint. 
In order to create proper issue service is sending following requests:
* POST /customer -
  creates new customer, sent only if OLMIS user is using Service Desk form for the first time and his customer account id is not stored in db yet
* POST /serviceDesk/{serviceDeskId}/customer -
  attaches user to specific Service Desk, in our case this would be Angola Service Desk, used only if previous request did not fail
* GET /serviceDesk/{serviceDeskId}/customer?query={emailAddress} -
  called if creating new user failed, i.e. because given email address was already used, this endpoint searches for users with given email address
* POST /serviceDesk/{serviceDeskId}/request -
  creates new customer request, makes given previously created or retrieved from db customer a reported of that issue
  
There are 2 special cases that might occur:
* If user does not have email address specified SIGLOFA admin user will be used as reporter. User will not receive notifications about issue.
* There is a possibility, that there is a existing customer with given email and he is not attached to Angola Service Desk. In that case we won't be able to retrieve this customer info. In this case admin should attach this user to Angola Service Desk using its UI.

### Attaching files

To attach file to already created issue users can use POST "/api/issues/{issueId}/attachment" endpoint.
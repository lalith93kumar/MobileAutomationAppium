Feature: Search in Flickr2

  Scenario Outline: validate search results2

    Given Login with Username: <username> and Password: <password>
    When Wait until check credentials page disappear
    Then Validate the error message as <ErrorMessage>

    Examples:
      | username  | password | ErrorMessage |
      |  |  | Please enter valid credentials |
      | kjbrfkljwebnf | skndvbksebvkjbekljd | Please enter valid credentials |

  @proxy
  Scenario Outline: validate search results
    Then Set login api response with status code as : <StatusCode> and with message as <Message>
    Given Login with Username: <username> and Password: <password>
    When Wait until check credentials page disappear
    Then Validate the app is not loged with user cred

    Examples:
      | username      | password  | StatusCode | Message          |
      | lalithmax20   | abcd.1234 | 404        | Path not founded |
      | lalithmax20   | abcd.1234 | 503        | Service Unavailable |

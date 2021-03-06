Feature: Search in Flickr

  @proxy
  Scenario Outline: validate search results
    Then Set login api response with status code as : 404 and with message as internal error
    Given Login with Username: <username> and Password: <password>
    When Wait until check credentials page disappear
    Then Validate the user: <username> login into the app

    Examples:
      | username  | password |
      | lalithmax20   | abcd.1234 |



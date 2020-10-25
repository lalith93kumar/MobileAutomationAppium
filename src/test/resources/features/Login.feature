Feature: Search in Flickr

  Scenario Outline: validate search results

    Given Login with Username: <username> and Password: <password>
    When Wait until check credentials page disappear
    Then Validate the user: <username> login into the app

    Examples:
      | username  | password |
      | lalithmax20   | abcd.1234 |



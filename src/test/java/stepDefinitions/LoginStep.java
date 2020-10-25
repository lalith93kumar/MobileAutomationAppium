package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.Login;

public class LoginStep {

    Login login = new Login();

    @Given("^Login with Username: (.*) and Password: (.*)$")
    public void userIsOnSearchScreen(String userName, String password) throws Throwable {
        login.signUp(userName,password);
    }

    @When("^Wait until check credentials page disappear$")
    public void userSearchForATextInSearchScreen() throws Throwable {
        login.waitForCheckCredentaials();
    }

    @Then("^Validate the user: (.*) login into the app$")
    public void searchResultsShouldDisplayForSearchCriteria(String userName) throws Throwable {
       login.verifySignUp(userName);
    }

    @Then("^Validate the app is not loged with user cred$")
    public void verifywhetherAppLoginError() throws Throwable {
        login.verifyNotSignUp();
    }

    @Then("^Validate the error message as (.*)$")
    public void validateErrorMessage(String message) throws Throwable {
        login.verifyErrorMessage(message);
    }
}

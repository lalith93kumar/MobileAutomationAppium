package stepDefinitions;

import io.cucumber.java.en.Then;
import proxyserver.conversationModifiers.LoginAPIModifier;
import proxyserver.utility.ForwardExpectationHandler;

import java.util.HashMap;
import java.util.Map;

public class ProxySteps {
    @Then("^Set login api response with status code as : (.*) and with message as (.*)$")
    public void searchResultsShouldDisplayForSearchCriteria(String statusCode, String message) throws Throwable {
        LoginAPIModifier client = new LoginAPIModifier();
        Map<String,String> param = new HashMap<String ,String >();
        param.put("StatusCode",statusCode);
        param.put("Message",message);
        client.addFunctionModifier("setErrorStatusCode",param);
        new ForwardExpectationHandler().responseClassCallback2(client);
    }
}

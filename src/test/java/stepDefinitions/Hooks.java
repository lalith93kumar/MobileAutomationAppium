package stepDefinitions;

import UIFrame.Server;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import proxyserver.utility.ForwardExpectationHandler;
import utility.AdbCommand;
import java.net.InetAddress;
import java.util.Collection;

public class Hooks {

    public Server server =new Server();
    /*
     *   Start Appium Server Programmatically before each scenario
     */
    @Before
    public void startServer(Scenario scenario) throws Exception {
        server.startServer();
        setUpProxy(scenario);
    }

    public void setUpProxy(Scenario scenario) throws Exception {
        Collection<String> tags = scenario.getSourceTagNames();
        String deviceUdid = server.getDevice();
        new AdbCommand().removehttpProxy(deviceUdid);
        if (tags.toString().contains("@proxy")) {
            Integer port = ForwardExpectationHandler.getPortMapping().get(Thread.currentThread().getId());
            new AdbCommand().sethttpProxy(deviceUdid, InetAddress.getLocalHost().getHostAddress().toString() + ":" + port);
        }
    }

    /*
     *   Stop Appium Server Programmatically before each scenario
     */
    @After
    public void stopServer() {
        server.stopServer();
    }
}

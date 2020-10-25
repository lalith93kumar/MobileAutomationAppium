package proxyserver.utility;

import org.mockserver.client.MockServerClient;
import org.mockserver.mock.action.ExpectationForwardAndResponseCallback;
import org.mockserver.mock.action.ExpectationForwardCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;

public class ForwardExpectationHandler {

    public static Map<Long,Integer> getPortMapping()
    {
        Map<Long,Integer> port = new HashMap();
        try (BufferedReader br = new BufferedReader(new FileReader("src/ipconfig.txt")))
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
                String[] row = sCurrentLine.split("=");
                port.put(Long.parseLong(row[0]), Integer.parseInt(row[1]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    public void responseClassCallback2(ResponseProxyTemplate expectationClassProperty) {
        new MockServerClient("localhost", ForwardExpectationHandler.getPortMapping().get(Thread.currentThread().getId()))
                .when(
                        request()
                                .withPath(expectationClassProperty.getURlRegx())
                ).forward(
                new ExpectationForwardCallback() {
                    @Override
                    public HttpRequest handle(HttpRequest httpRequest) throws Exception {
                        return httpRequest;
                    }
                },
                new ExpectationForwardAndResponseCallback() {
                    @Override
                    public HttpResponse handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
                        return expectationClassProperty.modify(httpResponse);
                    }
                }
        );
    }
}


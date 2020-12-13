package proxyserver.conversationModifiers;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mockserver.model.HttpResponse;
import proxyserver.utility.Common;
import proxyserver.utility.ResponseProxyTemplate;

import java.util.Map;

public class LoginAPIModifier extends Common implements ResponseProxyTemplate {
    @Override
    public String getURlRegx() {
        return "/login";
    }

    @Override
    public HttpResponse modify(HttpResponse response) {
        this.response = response;
        invokers();
        return response;
    }

    public void setErrorStatusCode(Map<String,String> value) throws ParseException {
        response.withStatusCode(Integer.valueOf(value.get("StatusCode")));
        responseBody = new JSONParser().parse("{'error':'"+value.get("Message")+"'}");
    }
}

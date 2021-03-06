package proxyserver.conversationModifiers;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mockserver.model.HttpResponse;
import proxyserver.utility.Common;
import proxyserver.utility.ResponseProxyTemplate;

import java.util.HashMap;
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

    public void setErrorStatusCode(HashMap<String,String> value) throws ParseException {
        response.withStatusCode(Integer.valueOf(value.get("StatusCode")));
        String s = "{\"error\":\""+value.get("Message")+"\"}";
        Object responseBody1 = new JSONParser().parse(s);
        responseBody = new JSONParser().parse(s);
    }
}

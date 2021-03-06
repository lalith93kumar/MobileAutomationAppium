package proxyserver.utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mockserver.model.HttpResponse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

public class Common {
    public HashMap<String, Object> functionCall = new HashMap();
    public HttpResponse response;
    public Object responseBody;

    public void setAttributes()
    {
        String val = this.response.getBodyAsString();

        try {
            this.responseBody = new JSONParser().parse(val);
        } catch (Exception e) {
            String s =  e.getMessage();
        }
    }

    public void addFunctionModifier(String methodName, Object args)
    {
        functionCall.put(methodName,args);
    }

    public void setStatusCode(Integer statusCode)
    {
        response.withStatusCode(statusCode);
    }

    public void invokers()
    {
        setAttributes();
        try{
            Iterator it = functionCall.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                Class cls = this.getClass();
                System.out.println("The name of class is " +
                        cls.getName());
                Method methodcall1 = cls.getDeclaredMethod((String) pair.getKey(),
                        pair.getValue().getClass());
                methodcall1.invoke(this, pair.getValue());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        finally {
            setModifiedResponseBody();
        }
    }

    public void setModifiedResponseBody()
    {
        JSONObject jsonObject = responseBody instanceof JSONObject ? ((JSONObject) responseBody) : null;
        JSONArray jsonArray = responseBody instanceof JSONArray ? ((JSONArray) responseBody) : null;
        if(jsonObject != null)
            this.response
                    .removeHeader(CONTENT_LENGTH.toString())
                    .withBody(jsonObject.toJSONString());
        if(jsonArray != null)
            this.response
                    .removeHeader(CONTENT_LENGTH.toString())
                    .withBody(jsonArray.toJSONString());
    }

}


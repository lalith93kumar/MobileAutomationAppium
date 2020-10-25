package proxyserver.utility;

import org.mockserver.model.HttpResponse;

public interface ResponseProxyTemplate {
    public String getURlRegx();
    public HttpResponse modify(HttpResponse response);
}

package proxyserver.utility;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.socket.PortFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class CucumberListener implements ConcurrentEventListener {

    private String certificatePath;
    HashMap<Long, ClientAndServer> portMapping = new HashMap();
    private File file;
    public CucumberListener(String certificatePath)
    {
        this.certificatePath = certificatePath;
    };

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        System.out.println("Event register");
        publisher.registerHandlerFor(TestRunStarted.class, this::runStarted);
        publisher.registerHandlerFor(TestRunFinished.class, this::runFinished);
        publisher.registerHandlerFor(TestCaseStarted.class, this::scenarioStarted);
        publisher.registerHandlerFor(TestCaseFinished.class, this::scenarioFinished);
    };

    public void deleteFileIfExist(File file)
    {
        if(file.exists())
            file.delete();
    }

    public void createPortFile(File file)
    {
        try {
            file.createNewFile();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void setupPortFile(String path)
    {
        File file = new File(path);
        deleteFileIfExist(file);
        file = new File(path);
        this.file = file;
    }

    public void runStarted(TestRunStarted event) {
        setupPortFile("src/ipconfig.txt");
    };

    public void scenarioFinished(TestCaseFinished event)
    {
        portMapping.get(Thread.currentThread().getId()).reset();
    }

    public void runFinished(TestRunFinished event) {
        Iterator it = portMapping.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(((ClientAndServer) pair.getValue()).getLocalPort());
            ((ClientAndServer) pair.getValue()).stop();
            System.out.println("lalith");
        }
    };

    public void scenarioStarted(TestCaseStarted event) {
        if(!portMapping.containsKey(Thread.currentThread().getId()))
        {
            ClientAndServer mockServer = initiateServer();
            portMapping.put(Thread.currentThread().getId(),mockServer);
            appendPortTofile(mockServer.getLocalPort());
        }
    };

    public void appendPortTofile(Integer port)
    {
        try
        {
            FileWriter fileWritter = new FileWriter(this.file,true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(Thread.currentThread().getId()+"="+port+"\n");
            bw.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public ClientAndServer initiateServer() {
        ConfigurationProperties.maxSocketTimeout(120000L);
        ConfigurationProperties.dynamicallyCreateCertificateAuthorityCertificate(true);
        ConfigurationProperties.directoryToSaveDynamicSSLCertificate(this.certificatePath);
        ConfigurationProperties.sslCertificateDomainName("localhost");
        ConfigurationProperties.addSslSubjectAlternativeNameDomains(new String[]{"www.example.com", "www.another.com"});
        ConfigurationProperties.addSslSubjectAlternativeNameIps(new String[]{"127.0.0.1"});
        ConfigurationProperties.enableCORSForAPI(true);
        ConfigurationProperties.enableCORSForAllResponses(true);
        ConfigurationProperties.disableSystemOut(true);
        ConfigurationProperties.logLevel("WARN");
        int port = PortFactory.findFreePort();
        try {
            System.out.println("Please add this IP address to your client devices : "+ InetAddress.getLocalHost().getHostAddress().toString() + ":" + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return startClientAndServer(port);
    }
}

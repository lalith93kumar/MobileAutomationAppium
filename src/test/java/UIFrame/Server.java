package UIFrame;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import org.mockserver.socket.PortFactory;
import org.openqa.selenium.remote.DesiredCapabilities;
import utility.AdbCommand;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static ThreadLocal<AppiumDriverLocalService> service = new ThreadLocal<>();
    static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    static Map<Long,String> threadToDeviceMapping =  new HashMap();
    static Map<String,Long> deviceToThreadMapping = new HashMap();

    public void startServer() throws IOException, InterruptedException {
        AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder()
                .usingDriverExecutable(new File("/usr/local/bin/node"))
                .withAppiumJS(new File("/usr/local/bin/appium"))
                .withIPAddress("127.0.0.1")
                .usingAnyFreePort();
        service.set(appiumServiceBuilder.build());
        service.get().start();

        if (service == null || !service.get().isRunning()) {
            throw new AppiumServerHasNotBeenStartedLocallyException("An appium server node is not started!");
        }
        new AdbCommand().uninstallApp(getDevice(),"com.malmstein.yahnac");
        File app = new File("app/app-debug.apk");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.UDID, getDevice());
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        capabilities.setCapability("appWaitDuration", 500000);
        capabilities.setCapability("noReset", true);
        int port = PortFactory.findFreePort();
        capabilities.setCapability(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER.getArgument(),port);
        capabilities.setCapability(AndroidMobileCapabilityType.AVD_LAUNCH_TIMEOUT, 500000);
        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        capabilities.setCapability("appPackage", "com.malmstein.yahnac");
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT,500000);
        capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS,true);
        capabilities.setCapability(AndroidMobileCapabilityType.DEVICE_READY_TIMEOUT,500000);
        driver.set(new AndroidDriver<>(service.get().getUrl(), capabilities));
    }

    public String getDevice() {
        if(threadToDeviceMapping.keySet().contains(Thread.currentThread().getId()))
            return threadToDeviceMapping.get(Thread.currentThread().getId());
        try {
            for (String s : new AdbCommand().getAllDevices()) {
                if (!deviceToThreadMapping.keySet().contains(s)) {
                    deviceToThreadMapping.put(s, Thread.currentThread().getId());
                    threadToDeviceMapping.put(Thread.currentThread().getId(), s);
                    return s;
                }
            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
        return "";
    }

    public AppiumDriver getDriver() {
        return driver.get();
    }

    public AppiumDriverLocalService getService() {
        return service.get();
    }

    public void stopServer() {
        if (driver.get() != null) {
            driver.get().quit();
        }
        if (service.get() != null) {
            service.get().stop();
        }
    }
}

package UIFrame;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.remote.DesiredCapabilities;
import utility.AdbCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static ThreadLocal<AppiumDriverLocalService> service = new ThreadLocal<>();
    static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    static Map<Long,String> threadToDeviceMapping =  new HashMap();
    static Map<String,Long> deviceToThreadMapping = new HashMap();

    public void startServer() {
        AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder()
                .usingDriverExecutable(new File("/usr/local/bin/node"))
                .withAppiumJS(new File("/usr/local/bin/appium"))
                .withIPAddress("0.0.0.0")
                .usingAnyFreePort();
        service.set(appiumServiceBuilder.build());
        service.get().start();

        if (service == null || !service.get().isRunning()) {
            throw new AppiumServerHasNotBeenStartedLocallyException("An appium server node is not started!");
        }

        File app = new File("app/app-debug.apk");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, getDevice().);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, getDevice());
        capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
        capabilities.setCapability("appWaitDuration", 30);
        capabilities.setCapability(AndroidMobileCapabilityType.AVD_LAUNCH_TIMEOUT, 500000);
        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT,500000);
        driver.set(new IOSDriver(service.get().getUrl(), capabilities));
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

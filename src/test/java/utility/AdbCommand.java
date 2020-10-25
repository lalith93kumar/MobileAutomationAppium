package utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Runtime.getRuntime;

public class AdbCommand {
    public void sethttpProxy(String deviceUdid, String port) throws IOException, InterruptedException {
        System.out.println(port);
        String commandStopActivity = String.format("adb -s %s shell settings put global http_proxy " + port, deviceUdid);
        executeCommand(commandStopActivity);
    }

    public void removehttpProxy(String deviceUdid) throws IOException, InterruptedException {
        String commandStopActivity = String.format("adb -s %s shell settings delete global http_proxy ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings delete global global_http_proxy_host ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings delete global global_http_proxy_port ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings put global http_proxy :0 ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings delete global http_proxy ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings delete global global_http_proxy_host ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings delete global global_http_proxy_port ", deviceUdid);
        executeCommand(commandStopActivity);
        commandStopActivity = String.format("adb -s %s shell settings put global http_proxy :0 ", deviceUdid);
        executeCommand(commandStopActivity);
    }

    public List<String> getAllDevices() throws IOException, InterruptedException {
        String commandUninstallApp = String.format("adb devices");
        String[] data = executeCommand(commandUninstallApp).split("\n");
        List<String> output = new ArrayList<String>();;
        for(String s : data)
        {
            if(!s.contains("List of devices attached") && !s.equals(""))
            {
                output.add(s.split("\t")[0]);
            }
        }
        return output;
    }

    public void uninstallApp(String deviceUdid, String appName) throws IOException, InterruptedException {
        String commandUninstallApp = String.format("adb -s %s uninstall %s", deviceUdid, appName);
        executeCommand(commandUninstallApp);
    }

    public String executeCommand(String command) throws IOException, InterruptedException {
        Process process = getRuntime().exec(command);

        StringBuilder output = new StringBuilder();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        return output.toString();
    }

    public void closeAppUsingADBCommand(String deviceUdid, String appPkgName) throws IOException, InterruptedException {
        String commandStopActivity = String.format("adb -s %s shell am force-stop " + appPkgName, deviceUdid);
        executeCommand(commandStopActivity);
    }

    public void relaunchAppUsingADBCommand(String deviceUdid, String appPkgName, String homeLaunchableActivityName) throws IOException, InterruptedException {
        String commandLaunchActivity = String.format("adb -s %s shell am start " + appPkgName + "/" + homeLaunchableActivityName, deviceUdid);
        executeCommand(commandLaunchActivity);
    }
}

package Mytask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Runtime.getRuntime;

public class AdbCommand {

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
}

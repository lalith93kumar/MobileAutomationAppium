# AppiumCucumber
Sample Appium test automation using Cucumber-JVM

## Pre-requisites
* Appium 1.7
* Java 8
* Maven
* IDE with Cucumber Plugin installed
* More info on [Appium Setup instructions](http://appium.io/slate/en/master/?ruby#running-appium-on-mac-os-x)

## Dependencies
* Appium Java Client
* Cucumber JVM
* Cucumber Java

## MockServerIntegration
[Mockserver](https://www.mock-server.com/), a library that allows handy access to network requests & response of devices made in the middle of your test runs. It is a free and open-source interactive Mock Server and proxy developed in Java.
It aids send a mocked or modified response for a client request which unblocks us to automate test cases depends on service side configuration like firebase, the feature enables, serve down, etc.

## Test Execution
$ `https://github.com/lalith93kumar/MobileAutomationAppium.git `

$ `./gradlew clean build`

### Run Configuration for CucumberJava.

MockServerIntegration runs your tests in parallel. N number of mock server will be triggered based on the parallel thread count used in cucumber. Each mock server will be mapped with the parallel threadID & written in the file path "src/ipconfig.txt".

* Add customized cucumber listener as <b>-p proxyserver.utils.CucumberListener:(Path to Certificate)</b>
```
Program arguments: -p proxyserver.utils.CucumberListener:/Users/x.y/MockserverCert
```
* Modify the appPackage value in testfeed json file as 'com.gojek.app.dev' for consumer app.

* Proxy Ip address & port mapping will be done programmatically. So don't need to configure proxy explicitly in the test device while executing the script.
 
<b>Note:</b> Dynamically CA certificate will be generated only once for device or machine in which the mock server is initiated. So try to configure & execute with an empty folder path for the certificate. After the first execution, You can find the CertificateAuthorityCertificate.pem in the configured folder path. Install the certificate in test devices connected to the machine.

### Run Configuration for ParallelExecution.

ParallelExecution is groovy task create to run the cucumber feature file based on number of devices connected to the machine.
```
VM Option: -DproxySeverCertifiedPath:/Users/x.y/MockserverCert/
```

Command Line:
```
./gradlew parallelExecution -DproxySeverCertifiedPath:/Users/x.y/MockserverCert/
```


package Mytask

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ParallelExecution extends DefaultTask {

    org.gradle.api.file.FileCollection classpathFolder
    String proxySeverCertifiedPath

    @TaskAction
    def run() {
       def var = new AdbCommand().getAllDevices().size()
        println "pool size -- " + var
        println "Classpath -- " + classpathFolder
        def cucumberArgs = getArgs(var)
        project.javaexec {
            main = "io.cucumber.core.cli.Main"
            classpath = classpathFolder
            args = getArgs(var)
            ignoreExitValue = true
        }

    }

    def getArgs(int size) {
        List<String> args = []
        args.add("-p")
        args.add("json:target/cucumber-parallel-report")
        args.add("--threads")
        args.add(size)
        args.add("-p")
        args.add("pretty")
        args.add("--glue")
        args.add("stepDefinitions")
        args.add("${project.projectDir}/src/test/resources/features")
        return args
    }
}

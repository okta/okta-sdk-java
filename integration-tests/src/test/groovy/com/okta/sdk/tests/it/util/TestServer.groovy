/*
 * Copyright 2017 Okta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.sdk.tests.it.util

import com.okta.sdk.lang.Strings
import org.testng.TestNGException

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Starts the Node.js based test server @okta/okta-sdk-test-server.
 */
class TestServer {
    public static final TEST_SERVER_BASE_URL = "okta.testServer.baseUrl"

    private Process process
    private File testServerLogFile

    TestServer start(List<String> scenarios, String serverBin, boolean verbose = false) {

        StringBuilder scenariosArgBuilder = new StringBuilder()
        scenarios.each {scenario ->
            scenariosArgBuilder.append(",")
            scenariosArgBuilder.append(scenario)
        }

        List<String> command = [serverBin]
        if (verbose) command.add('--verbose')
        // if we have scenarios (which we should), then add the '--scenarios' flag
        if (Strings.hasLength(scenariosArgBuilder)) {
            command.add("--scenarios")
            command.add(scenariosArgBuilder.substring(1))
        }

        // set a property so we can point the client at the right location
        System.setProperty(TEST_SERVER_BASE_URL, "http://localhost:8080/")

        File targetDirectory = new File(System.getProperty("user.dir"), "target")
        File workingDirectory = new File(targetDirectory, "test-classes")
        testServerLogFile = new File(targetDirectory, "okta-sdk-test-server.log")

        ProcessBuilder processBuilder = new ProcessBuilder(command)
        processBuilder.directory(workingDirectory)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.to(testServerLogFile))
        processBuilder.redirectErrorStream(true)
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
        processBuilder.environment().put("TERM", "vt220")

        process = processBuilder.start()
        if (process.waitFor(5, SECONDS)) {
            String message = "okta-sdk-test-server process exited while starting with exit value " +
                    "of '${process.exitValue()}'.\nProcess output follows:\n" + testServerLogFile.text

            throw new TestNGException(message)
        }

        return this
    }

    void stop() {

        if (process != null && process.isAlive()) {
            process.destroy()
            if (process.waitFor(5, SECONDS)) {
                if (process.exitValue() != 0) {
                    String message = "okta-sdk-test-server process exited with a non-zero status " +
                            "of '${process.exitValue()}'.\nProcess output follows:\n" + testServerLogFile.text

                    throw new TestNGException(message)
                }
            }
            else {
                process.destroyForcibly()
                String message = "Force killed test-server, manually check to see if it is still running.\n" +
                        "The test-server output can be found in: ${testServerLogFile.absolutePath}"
                throw new TestNGException(message)
            }
        }
    }
}

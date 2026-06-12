/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package dev.mamii.instafel.updater.utils;

public class CommandOutput {

    int exitCode;
    String log, errorLog;

    public CommandOutput(int exitCode, String log, String errorLog) {
        this.exitCode = exitCode;
        this.log = log;
        this.errorLog = errorLog;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getLog() {
        return log;
    }

    public String getErrorLog() {
        return errorLog;
    }
}

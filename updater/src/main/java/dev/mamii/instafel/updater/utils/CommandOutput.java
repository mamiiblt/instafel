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

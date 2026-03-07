// IUserService.aidl
package dev.mamii.instafel.updater;

interface IUserService {

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    String executeShellCommand(String command) = 0;
}

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package dev.mamii.instafel.updater.utils;

public class AppPreferences {
    private boolean send_notification, use_mobile_data, hour_mode12, disable_error_notifications, crash_logger;

    public AppPreferences(boolean send_notification, boolean use_mobile_data, boolean hour_mode12, boolean disable_error_notifications, boolean crash_logger) {
        this.send_notification = send_notification;
        this.use_mobile_data = use_mobile_data;
        this.hour_mode12 = hour_mode12;
        this.disable_error_notifications = disable_error_notifications;
        this.crash_logger = crash_logger;
    }

    public boolean isAllowNotification() {
        return send_notification;
    }

    public boolean isAllowUseMobileData() {
        return use_mobile_data;
    }

    public boolean isAllow12HourMode() {
        return hour_mode12;
    }

    public boolean isCrashLoggerEnabled() {
        return crash_logger;
    }

    public boolean isDisable_error_notifications() {
        return disable_error_notifications;
    }
}

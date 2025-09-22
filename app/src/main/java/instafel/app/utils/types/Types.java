package instafel.app.utils.types;

import android.widget.TextView;

import instafel.app.utils.GeneralFn;

public class Types {

    public static class FreqLabels {
        public static int EVERY_OPEN = 0;
        public static int NEVER = 1;
        public static int EVERY_DAY = 2;
        public static int ONCE_IN_THREE_DAYS = 3;
        public static int ONCE_IN_FIVE_DAYS = 4;
        public static int ONCE_A_WEEK = 5;
    }

    public static class PreferenceTypes {
        public static int INT = 1;
        public static int STRING = 2;
        public static int BOOLEAN = 3;
        public static int LONG = 4;
    }

    public class DialogItem {
        private final TextView textView;
        private final String string_name;

        public DialogItem(TextView textView, String string_name) {
            this.textView = textView;
            this.string_name = string_name;
        }

        public String getStringName() {
            return string_name;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public static class AdminUserData {
        String username;
        String password;

        public AdminUserData(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return GeneralFn.decodeString(username);
        }

        public String getPassword() {
            return GeneralFn.decodeString(password);
        }
    }

}

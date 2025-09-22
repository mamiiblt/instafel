package instafel.app.utils.crashlog;

public class CLogDataTypes {
    public static class CrashData {
        private Object msg, trace, className, date;

        public CrashData(Object msg, Object trace, Object className) {
            this.msg = msg;
            this.trace = trace;
            this.className = className;
        }

        public Object getMsg() {
            return msg;
        }
        public Object getTrace() {
            return trace;
        }
        public Object getClassName() {
            return className;
        }
        public Object getDate() {
            return date;
        }
    }

    public static class AppData {
        private Object ifl_ver, ig_ver, ig_ver_code, ig_itype;

        public AppData(Object ifl_ver, Object ig_ver, Object ig_ver_code, Object ig_itype) {
            this.ifl_ver = ifl_ver;
            this.ig_ver = ig_ver;
            this.ig_ver_code = ig_ver_code;
            this.ig_itype = ig_itype;
        }

        public Object getIfl_ver() {
            return ifl_ver;
        }
        public Object getIg_ver() {
            return ig_ver;
        }
        public Object getIg_ver_code() {
            return ig_ver_code;
        }
        public Object getIg_itype() {
            return ig_itype;
        }
    }

    public static class DeviceData {

        private Object aver, sdk, model, brand, product;

        public DeviceData(Object aver, Object sdk, Object model, Object brand, Object product) {
            this.aver = aver;
            this.sdk = sdk;
            this.model = model;
            this.brand = brand;
            this.product = product;
        }

        public Object getAver() {
            return aver;
        }
        public Object getSdk() {
            return sdk;
        }
        public Object getModel() {
            return model;
        }
        public Object getBrand() {
            return brand;
        }
        public Object getProduct() {
            return product;
        }
    }
}

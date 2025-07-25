package me.mamiiblt.instafel.utils.crashlog;

import org.json.JSONObject;

public class Crashlog {

    private CLogDataTypes.AppData appData;
    private CLogDataTypes.DeviceData deviceData;
    private CLogDataTypes.CrashData crashData;
    private Object date;

    public Crashlog(CLogDataTypes.AppData appData, CLogDataTypes.DeviceData deviceData, CLogDataTypes.CrashData crashData, Object date) {
        this.appData = appData;
        this.deviceData = deviceData;
        this.crashData = crashData;
        this.date = date;
    }

    public Object getDate() {
        return date;
    }

    public String convertToString() {
       try {
           JSONObject logObject = new JSONObject();
           JSONObject newAppData = new JSONObject();
           newAppData.put("ifl_ver", appData.getIfl_ver());
           newAppData.put("ig_ver", appData.getIg_ver());
           newAppData.put("ig_ver_code", appData.getIg_ver_code());
           newAppData.put("ig_itype", appData.getIg_itype());
           logObject.put("appData", newAppData);

           JSONObject newDeviceData = new JSONObject();
           newDeviceData.put("aver", deviceData.getAver());
           newDeviceData.put("sdk", deviceData.getSdk());
           newDeviceData.put("model", deviceData.getModel());
           newDeviceData.put("brand", deviceData.getBrand());
           newDeviceData.put("product", deviceData.getProduct());
           logObject.put("deviceData", newDeviceData);

           JSONObject newCrashData = new JSONObject();
           newCrashData.put("msg", crashData.getMsg());
           newCrashData.put("trace", crashData.getTrace());
           newCrashData.put("class", crashData.getClassName());
           logObject.put("crashData", newCrashData);

           logObject.put("date", date);

           return logObject.toString();
       } catch (Exception e) {
           e.printStackTrace();
           return "ERROR_WHILE_CONVERTING_CRASHLOG";
       }
    }

    public CLogDataTypes.AppData getAppData() {
        return appData;
    }
    public CLogDataTypes.DeviceData getDeviceData() {
        return deviceData;
    }
    public CLogDataTypes.CrashData getCrashData() {
        return crashData;
    }
}
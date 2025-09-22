package instafel.app.api.models;

import org.json.JSONObject;

public class InstafelResponse {

    JSONObject parsedResult;
    String status;
    String desc;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public JSONObject getExtra() {
        return extra;
    }

    JSONObject extra;

    public InstafelResponse(String rawResponse) {
        try {
            parsedResult = new JSONObject(rawResponse);

            if (parsedResult.has("status"))  {
                this.status = parsedResult.getString("status");
            }

            if (parsedResult.has("desc")) {
                this.desc = parsedResult.getString("desc");
            }

            if (parsedResult.has("extra")) {
                this.extra = parsedResult.getJSONObject("extra");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

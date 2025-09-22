package instafel.app.managers.modals;

import org.json.JSONObject;

public class ParseResult {
    private JSONObject mappingObject;
    private String flagId;

    public ParseResult(String flagId, JSONObject mappingObject) {
        this.flagId = flagId;
        this.mappingObject = mappingObject;
    }

    public JSONObject getMappingObject() {
        return mappingObject;
    }
    public String getFlagId() {
        return flagId;
    }
}
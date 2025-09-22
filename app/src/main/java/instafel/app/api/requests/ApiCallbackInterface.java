package instafel.app.api.requests;

import instafel.app.api.models.InstafelResponse;

public interface ApiCallbackInterface {
    void getResponse(InstafelResponse instafelResponse, int taskId);
    void getResponse(String rawResponse, int taskId);
}

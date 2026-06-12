/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.app.api.models;

import org.json.JSONObject;

public class BackupListItem {
    private String id;
    private String name;
    private String author;

    public BackupListItem(String id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String convertForPutIntoActivity() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("author", author);
            jsonObject.put("name", name);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "CANNOT_BE_CONVERTED";
        }
    }
}

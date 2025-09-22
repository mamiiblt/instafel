package instafel.app.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

public class InstafelFileProvider extends ContentProvider {

    private static final String AUTHORITY = "instafel.app.fileprovider";
    private static final String IFL_FILES_DIR = "instafel_files";
    private static final int FILE = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static Uri getFileUri(String fileName) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("content")
                .authority(AUTHORITY)
                .appendPath(IFL_FILES_DIR)
                .appendPath(fileName);

        return builder.build();
    }

    @Override
    public boolean onCreate() {
        uriMatcher.addURI(AUTHORITY, IFL_FILES_DIR + "/*", FILE);
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (uriMatcher.match(uri) != FILE) {
            throw new FileNotFoundException("Unsupported URI: " + uri);
        }

        String filename = uri.getLastPathSegment();
        File file = new File(getContext().getExternalFilesDir(null), IFL_FILES_DIR + "/" + filename);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public String getType(Uri uri) {
        return "application/vnd.android.package-archive";
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
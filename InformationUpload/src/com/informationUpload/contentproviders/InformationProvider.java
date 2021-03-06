package com.informationUpload.contentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InformationProvider
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationProvider extends ContentProvider{

    private static final String LOG_TAG = "InformationProvider";
    private static final String DATABASE_NAME = "informationsDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String INFORMATION_TABLE_NAME = "informationTable";

    public static class InformationDbHelper extends SQLiteOpenHelper{

        private static volatile InformationDbHelper mInstance;

        private InformationDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mInstance = this;
        }

        public static InformationDbHelper getInstance(Context context) {
            if (mInstance == null) {
                synchronized (InformationDbHelper.class) {
                    if (mInstance == null) {
                        mInstance = new InformationDbHelper(context);
                    }
                }
            }
            return mInstance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + INFORMATION_TABLE_NAME + " ("
            + Informations.Information.ID + "  INTEGER PRIMARY KEY"
            + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + INFORMATION_TABLE_NAME);
            onCreate(db);
        }

        public void deleteDb(){
            close();
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.delete(INFORMATION_TABLE_NAME, null, null);
            } catch (Exception e) {
            }
        }
    }

    private InformationDbHelper mInformationDbHelper;
    private static final UriMatcher uriMatcher;
    private static final int INFORMATION = 1;
    private static final int INFORMATION_ID = 2;


    private static HashMap<String, String> maps;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Informations.AUTHORITY, "Information", INFORMATION);
        uriMatcher.addURI(Informations.AUTHORITY, "Information/#", INFORMATION_ID);

        maps = new HashMap<String, String>();
        maps.put(Informations.Information.ID, Informations.Information.ID);
    }


    @Override
    public boolean onCreate() {
        mInformationDbHelper = mInformationDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                sqb.setTables(INFORMATION_TABLE_NAME);
                sqb.setProjectionMap(maps);
                return queryDb(sqb, mInformationDbHelper, uri, projection, selection, selectionArgs, sortOrder);
            case INFORMATION_ID:
                sqb.setTables(INFORMATION_TABLE_NAME);
                sqb.setProjectionMap(maps);
                sqb.appendWhere(Informations.Information.ID + "=" + uri.getPathSegments().get(1));
                return queryDb(sqb, mInformationDbHelper, uri, projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Cursor queryDb(SQLiteQueryBuilder sqb, SQLiteOpenHelper helper, Uri uri, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mInformationDbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                long _id = db.insert(INFORMATION_TABLE_NAME, null, values);
                if (_id > 0) {
                    Uri uri1 = ContentUris.withAppendedId(Informations.Information.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(uri1, null);
                    return uri1;
                }
                break;
        }
        return null;
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mInformationDbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                count = db.delete(INFORMATION_TABLE_NAME, selection, selectionArgs);
                break;
            case INFORMATION_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(INFORMATION_TABLE_NAME, Informations.Information.ID
                        + "="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mInformationDbHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case INFORMATION:
                count = db.update(INFORMATION_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case INFORMATION_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(INFORMATION_TABLE_NAME, values, Informations.Information.ID
                        + "="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection+ ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

package com.codef1.oldcode;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.codef1.oldcode.data.PWDbHelper;
import com.codef1.oldcode.data.PasswordContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuzhu on 2017/12/17.
 */

public class PWViewModel extends AndroidViewModel {
    private PWDbHelper mDbHelper;
    private MutableLiveData<List<Password>> mData;

    public PWViewModel(@NonNull Application application) {
        super(application);

        mDbHelper = new PWDbHelper(application);
    }

    public LiveData<List<Password>> getData() {
        if (mData == null) {
            mData = new MutableLiveData<>();
            loadData();
        }
        return mData;
    }

    private void loadData() {
        ArrayList<Password> arrayData = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(PasswordContract.PWEntry.TABLE,
                new String[]{
                        PasswordContract.PWEntry._ID,
                        PasswordContract.PWEntry.COL_PW_NAME,
                        PasswordContract.PWEntry.COL_PW_VALUE,
                }, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(PasswordContract.PWEntry._ID);
            int titleIndex = cursor.getColumnIndex(PasswordContract.PWEntry.COL_PW_NAME);
            int valueIndex = cursor.getColumnIndex(PasswordContract.PWEntry.COL_PW_VALUE);
            arrayData.add(new Password(cursor.getLong(idIndex), cursor.getString(titleIndex), cursor.getString(valueIndex)));
        }
        cursor.close();
        sqLiteDatabase.close();
        mData.setValue(arrayData);
    }

    public void addPassword(String title, String value) {
        //Persist
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PasswordContract.PWEntry.COL_PW_NAME, title);
        values.put(PasswordContract.PWEntry.COL_PW_VALUE, value);
        long id = db.insertWithOnConflict(PasswordContract.PWEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        //LiveData
        List<Password> data = mData.getValue();
        ArrayList<Password> clonedData = new ArrayList<>(data.size());
        for(int i = 0; i < data.size(); i++){
            clonedData.add(new Password(data.get(i)));
        }
        Password password = new Password(id , title, value);
        clonedData.add(password);
        mData.setValue(clonedData);
    }

    public void removePassword(long id) {
        //Persist
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        sqLiteDatabase.delete(PasswordContract.PWEntry.TABLE, PasswordContract.PWEntry._ID + " = ? ",
                new String[]{Long.toString(id)});
        sqLiteDatabase.close();

        //LiveData
        List<Password> data = mData.getValue();
        ArrayList<Password> clonedData = new ArrayList<>(data.size());
        for(int i = 0; i < data.size(); i++){
            clonedData.add(new Password(data.get(i)));
        }
        int index = -1;
        for(int i = 0; i < clonedData.size(); i++){
            Password password = clonedData.get(i);
            if (password.getID() == id) {
                index = i;
            }
        }
        if (index != -1) {
            clonedData.remove(index);
        }
        mData.setValue(clonedData);
    }
}

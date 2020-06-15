/*
 *
 *  The MIT License
 *
 *  Copyright 2019 ITON Solutions.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */
package org.iton.jssi.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import org.iton.jssi.store.model.Plaintext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author ITON Solutions
 */
public class PlaintextDao implements Serializable {

    private static final String TAG = PlaintextDao.class.getName();
    private SQLiteDatabase database;

    public PlaintextDao(DatabaseHelper helper) {
        database = helper.getWritableDatabase();
    }

    public long create(Plaintext tag)  {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Column.TagPlaintext.ITEM_ID, tag.getItemId());
        values.put(DatabaseHelper.Column.TagPlaintext.NAME, tag.getName());
        values.put(DatabaseHelper.Column.TagPlaintext.VALUE, tag.getValue());
        return database.insert(
                DatabaseHelper.Table.TAGS_PLAINTEXT,
                null,
                values);
    }

    public long create(Collection<Plaintext> tags)  {
        long result = 0;
        for(Plaintext tag : tags){
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.Column.TagPlaintext.ITEM_ID, tag.getItemId());
            values.put(DatabaseHelper.Column.TagPlaintext.NAME, tag.getName());
            values.put(DatabaseHelper.Column.TagPlaintext.VALUE, tag.getValue());
            result += database.insert(
                    DatabaseHelper.Table.TAGS_PLAINTEXT,
                    null,
                    values);
        }
        return result;
    }

    public int update(Collection<Plaintext> tags)  {
        int result = 0;
        for(Plaintext tag : tags){
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.Column.TagPlaintext.NAME, tag.getName());
            values.put(DatabaseHelper.Column.TagPlaintext.VALUE, tag.getValue());

            String[] item_id = {String.valueOf(tag.getItemId())};
            result += database.update(
                    DatabaseHelper.Table.TAGS_PLAINTEXT,
                    values, "item_id = ?",
                    item_id);
        }
        return result;
    }

    public int delete(Collection<Plaintext> tags)  {
        int result = 0;
        for(Plaintext tag : tags){
            String[] item_id = {String.valueOf(tag.getItemId())};
            result += database.delete(
                    DatabaseHelper.Table.TAGS_PLAINTEXT,
                    "item_id = ?",
                    item_id);
        }
        return result;
    }

    public List<Plaintext> queryForAll(int item_id) {
        List<Plaintext> plaintext = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseHelper.Table.TAGS_PLAINTEXT,
                null,
                DatabaseHelper.Column.TagPlaintext.ITEM_ID + " = ? ",
                new String[] { String.valueOf(item_id) },
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            Wrapper wrapper = new Wrapper(cursor);
            while (!wrapper.isAfterLast()) {
                Plaintext tag = wrapper.wrap();
                plaintext.add(tag);
                wrapper.moveToNext();
            }
        }

        cursor.close();
        return plaintext;
    }

    private class Wrapper extends CursorWrapper {

        Wrapper(Cursor cursor) {
            super(cursor);
        }

        Plaintext wrap() {
            int item_id  = getInt(getColumnIndex(DatabaseHelper.Column.TagPlaintext.ITEM_ID));
            byte[] name  = getBlob(getColumnIndex(DatabaseHelper.Column.TagPlaintext.NAME));
            byte[] value = getBlob(getColumnIndex(DatabaseHelper.Column.TagPlaintext.VALUE));
            return new Plaintext(item_id, name, value);
        }
    }
}

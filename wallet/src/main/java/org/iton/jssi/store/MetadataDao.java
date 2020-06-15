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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.iton.jssi.store.model.Metadata;


public class MetadataDao {

    private static final String TAG = MetadataDao.class.getName();
    private SQLiteDatabase database;
    private DatabaseHelper helper;

    public MetadataDao(DatabaseHelper helper) {
        this.helper = helper;
        database = helper.getWritableDatabase();
    }

    public void create(Metadata metadata) {

        if(getCount() > 0){
            Log.e(TAG, String.format("Error: %s", "Metadata already exists"));
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Column.Metadata.VALUE, metadata.getValue());
        database.insert(DatabaseHelper.Table.METADATA, null, values);
    }

    public Metadata getMetadata(int id) {
        Metadata result = null;

        Cursor cursor = database.query(
                DatabaseHelper.Table.METADATA,
                null,
                DatabaseHelper.Column.Metadata.ID + " = ? ",
                new String[] { String.valueOf(id) },
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            Wrapper wrapper = new Wrapper(cursor);
            result = wrapper.wrap();
        }

        cursor.close();
        return result;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.Table.METADATA);
    }

    private class Wrapper extends CursorWrapper {

        public Wrapper(Cursor cursor) {
            super(cursor);
        }

        public Metadata wrap() {
            int id = getInt(getColumnIndex(DatabaseHelper.Column.Metadata.ID));
            byte[] value = getBlob(getColumnIndex(DatabaseHelper.Column.Metadata.VALUE));
            return new Metadata(id, value);
        }
    }
}

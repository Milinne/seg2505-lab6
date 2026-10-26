package com.example.lab6sqlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.lab6sqlite.FeedReaderContract.FeedEntry;

/**
 * Test instrumenté (exécuté sur l'émulateur) de la base SQLite.
 * Comme dans le tutoriel « Tester et déboguer votre base de données »,
 * on utilise une base EN MÉMOIRE, recréée avant chaque test et fermée après.
 */
@RunWith(AndroidJUnit4.class)
public class FeedReaderDbHelperTest {

    private FeedReaderDbHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new FeedReaderDbHelper(context, null); // null = base en mémoire
        db = dbHelper.getWritableDatabase();
    }

    @After
    public void closeDb() {
        dbHelper.close();
    }

    private long insert(String title, String subtitle) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);
        return db.insert(FeedEntry.TABLE_NAME, null, values);
    }

    private int countByTitle(String title) {
        Cursor cursor = db.query(FeedEntry.TABLE_NAME, null,
                FeedEntry.COLUMN_NAME_TITLE + " = ?", new String[]{title},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    @Test
    public void writeEntryAndReadIt() {
        long id = insert("george", "sous-titre");
        assertTrue(id > 0);

        Cursor cursor = db.query(FeedEntry.TABLE_NAME, null,
                FeedEntry._ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals("george",
                cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)));
        assertEquals("sous-titre",
                cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE)));
        cursor.close();
    }

    @Test
    public void updateEntry() {
        insert("ancien", "s");

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, "nouveau");
        int rows = db.update(FeedEntry.TABLE_NAME, values,
                FeedEntry.COLUMN_NAME_TITLE + " = ?", new String[]{"ancien"});

        assertEquals(1, rows);
        assertEquals(0, countByTitle("ancien"));
        assertEquals(1, countByTitle("nouveau"));
    }

    @Test
    public void deleteEntry() {
        insert("a supprimer", "s");
        insert("a garder", "s");

        int rows = db.delete(FeedEntry.TABLE_NAME,
                FeedEntry.COLUMN_NAME_TITLE + " = ?", new String[]{"a supprimer"});

        assertEquals(1, rows);
        assertEquals(0, countByTitle("a supprimer"));
        assertEquals(1, countByTitle("a garder"));
    }
}
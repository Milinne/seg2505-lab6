package com.example.lab6sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import com.example.lab6sqlite.FeedReaderContract.FeedEntry;

/**
 * Démontre les 4 opérations du tutoriel : insérer, lire, supprimer, mettre à jour.
 * Le résultat de chaque étape est affiché à l'écran et dans Logcat (tag "Lab6").
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Lab6";
    private FeedReaderDbHelper dbHelper;
    private final StringBuilder log = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new FeedReaderDbHelper(this);

        // Repartir d'une table vide à chaque lancement pour une démo lisible
        dbHelper.getWritableDatabase().delete(FeedEntry.TABLE_NAME, null, null);

        // 1) INSERTION
        long id1 = insert("Mon titre", "Mon sous-titre");
        long id2 = insert("Autre titre", "Autre sous-titre");
        log("Insertion : lignes " + id1 + " et " + id2 + " ajoutées");
        log("Contenu : " + readAll());

        // 2) LECTURE avec sélection
        log("Lecture (title = 'Mon titre') : ids " + readByTitle("Mon titre"));

        // 3) MISE À JOUR
        int updated = updateTitle("Mon titre", "Mon nouveau titre");
        log("Mise à jour : " + updated + " ligne(s) modifiée(s)");
        log("Contenu : " + readAll());

        // 4) SUPPRESSION
        int deleted = deleteByTitle("Autre titre");
        log("Suppression : " + deleted + " ligne(s) supprimée(s)");
        log("Contenu : " + readAll());

        ((TextView) findViewById(R.id.output)).setText(log.toString());
    }

    /** Insère une ligne et renvoie la clé primaire de la nouvelle ligne. */
    private long insert(String title, String subtitle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        return db.insert(FeedEntry.TABLE_NAME, null, values);
    }

    /** Lit les ids des lignes dont le titre correspond (requête paramétrée). */
    private List<Long> readByTitle(String title) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { title };
        String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<Long> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        return itemIds;
    }

    /** Lit toute la table, sous forme lisible : [id:titre/sous-titre, ...]. */
    private String readAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FeedEntry.TABLE_NAME, null, null, null, null, null,
                FeedEntry._ID + " ASC");
        List<String> rows = new ArrayList<>();
        while (cursor.moveToNext()) {
            rows.add(cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID)) + ":"
                    + cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)) + "/"
                    + cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE)));
        }
        cursor.close();
        return rows.toString();
    }

    /** Met à jour le titre ; renvoie le nombre de lignes modifiées. */
    private int updateTitle(String oldTitle, String newTitle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, newTitle);

        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { oldTitle };

        return db.update(FeedEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /** Supprime les lignes par titre ; renvoie le nombre de lignes supprimées. */
    private int deleteByTitle(String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { title };

        return db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void log(String msg) {
        Log.d(TAG, msg);
        log.append(msg).append("\n\n");
    }

    /** Fermer la connexion quand l'activité est détruite (recommandé par le tutoriel). */
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

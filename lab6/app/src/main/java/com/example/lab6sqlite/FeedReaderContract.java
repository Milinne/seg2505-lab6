package com.example.lab6sqlite;

import android.provider.BaseColumns;

/**
 * Contrat : définit le schéma de la base (nom de table, noms de colonnes)
 * et les requêtes SQL de création / suppression de la table.
 */
public final class FeedReaderContract {

    // Empêche l'instanciation accidentelle de la classe contrat.
    private FeedReaderContract() {}

    /* Classe interne qui définit le contenu de la table */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}

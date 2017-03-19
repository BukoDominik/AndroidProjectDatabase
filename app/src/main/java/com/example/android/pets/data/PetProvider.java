package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by D3B3st on 3/18/2017.
 */

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** URI matcher - kod dla zadania tabeli PETS */
    private static final int PETS = 100;

    /** URI matcher - kod żądania dla pojedyńczego przedmiotu w tabeli PETS */
    private static final int PET_ID = 101;

    static {
        // Podminamy nasz URI matcher zadajac (Content authority, nazwe tabeli, kod dla zadania
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS+"/#", PET_ID);
    }
    // Tworzymy obiekt mDbHelper do tworzenia database
    private PetDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        // Problem z przypisaneim kontekstu uzyj getContext()
        mDbHelper = new PetDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Ustawiamy databaze w stan odczytywania
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // towrzymy cursor interfrace'u cursor
        Cursor cursor = null;

        // Sprawdzamy czy uzyskalismy odpowiedni kod żądania
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Dla opcji w ktorym żądana byla tabela potrzebujemy dwie zmienne - nazwe tabeli
                // i nazwy kolumn
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, null, null,
                        null, null, null);

                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // przesylamy żądanie dalej zadanymi parametrami
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // W razie zgodnosci wstawiamy obiekt
            case PETS:
                return insertPet(uri, contentValues);
            default:
                // w razie niepowodzenia wyrzucony exeption
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // funkcja pozwalajaca wstawic obiekt
    private Uri insertPet(Uri uri, ContentValues values) {
        // database z klasy SQLiteDatabase ustawiona w stan zapisywania
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // wstawienie do databazy obiektu zadanymi wartosciami
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, values);

        // Walidacja nazwy
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        // Walidacja wagi
        int weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }

        // Log w razie niepowodzenia i zwrocenie wartosci null potrzebne do wyswietlenia komunikatu
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Databaza w stanie nadpisywania
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // sprawdzenie zgodnosci
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Usun wiersze zgodne z selekcja i jej argumentami
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Usun pojedynczy wiersz zgodnie z zdananym ID
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

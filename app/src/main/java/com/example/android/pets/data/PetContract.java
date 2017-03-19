package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by D3B3st on 3/18/2017.
 */

public final class PetContract {
// Podstawowe dane do stworzenia CONCENT_URI zapisane w danych zamiast twardo kodowane
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    public static abstract class PetEntry implements BaseColumns {
        // Nasze docelowe URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        // Przypisanie odpowiednich nazw kolumnom i tabeli
        public static final String TABLE_NAME = "pets";
        public static final String _ID = "_ID";
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * Wartości dla kolumny GENDER
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

    }
}

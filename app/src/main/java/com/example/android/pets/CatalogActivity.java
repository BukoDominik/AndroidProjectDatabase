/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;


public class CatalogActivity extends AppCompatActivity {

    @Override protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Onclick Listener dla naszego buttona
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayDatabaseInfo();
    }

    public void displayDatabaseInfo() {
        // Zdefiniowanie projection - jakie kolumny chcemy zeby zostaly wyswietlone
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        // Tworzymy obiekt cursor interface'u cursore , content resolverem dostajemy sie do funkcji
        // i zadajemu mu odpowiednie parametry
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection,null, null, null);
        // Znajdujemy nasz tekstView ktory chcemy nadpisac
        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {
            // Zmieniamy tekst w naszym view na ilosc wierszy jakie jest w naszej tabeli dzieki
            // cursor.getCount
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
            // metoda append wyswietlamy nazwy wszystkich kolumn w tabeli
            displayView.append(PetEntry._ID + " - " +
                    PetEntry.COLUMN_PET_NAME + " - " +
                    PetEntry.COLUMN_PET_BREED + " - " +
                    PetEntry.COLUMN_PET_GENDER + " - " +
                    PetEntry.COLUMN_PET_WEIGHT + "\n");

            // ustalamy Index kazdej kolumny
            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Petla while przechodzaca przez kazdy wiersz w tabeli
            while (cursor.moveToNext()) {
                // Z kazdej kolumny dzieki temu ze znamy index wyciagnety wczesniej funkcja getColumnIndex
                // mozemy pobrac dane i zapisac je w odpowiednim kontenerze
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentBreed = cursor.getString(breedColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);
                // Wyswietlamy w linii wszystkie wartosci wlozone do kontenerow
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentBreed + " - " +
                        currentGender + " - " +
                        currentWeight));
            }
        } finally {
            // Zamkniecie kursora na koniec dzialania
            cursor.close();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflatujemy menu z res/menu/menu_catalog.xml zawierajace przedmioty w app barze
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet(){

        // Funkcja pozwalajaca wprowadzic przykladowego zwierzaka
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, "7");
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

    };
    private void deleteAll(){


        int deleted = getContentResolver().delete(PetEntry.CONTENT_URI,null,null);

    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Po odpaleniu okna menu w barze u gory
        switch (item.getItemId()) {
            // Wstawianie przykladowego zwierzaka
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Usuniecie databazy
            case R.id.action_delete_all_entries:
                deleteAll();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

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
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.android.pets.data.PetContract;

/**
 * Stworzenie nowego obiektu
 */
public class EditorActivity extends AppCompatActivity {

    // Containery na Imie, rase, wage , plec
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;

    /**
     Plec wybierana z opcji 0- Unknown 1-Male 2-Female
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Zbieranie danych ze wszystkich pol do wpisania potrzebnych do stworzenia obiektu
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    /**
     * Spinner pozwalajacy wybrac plec z 3 opcji
     */
    private void setupSpinner() {
        // Adapter dla spinnera
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Wypor stylu Spinnera
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Podpinamy adapter do Spinnera
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Przypisujemy odpowiednie wartosci na kazde klikniecie
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Trzeba nadpisac klase onNothingSelected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Wyswietl manu z  res/menu/menu_editor.xml
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    public void collectData(){
        // Zbierz dane z pol i zapisz je w odpowiednich pojemnikach
        String nameOfPet = mNameEditText.getText().toString().trim();
        String BreedOfPet = mBreedEditText.getText().toString().trim();
        String WeightOfPet = mWeightEditText.getText().toString();
        int WeightOfPetInt = Integer.parseInt(WeightOfPet);
        // Odpowiednie wartosci values na podstawie zebranych danych
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameOfPet);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, BreedOfPet);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, WeightOfPetInt);
        // Wstaw do bazy danych nadpisana fukncja insert
        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

        // Toast w zaleznosci od tego czy udalo nam sie wstawic nowy obiekt czy nie
        if (newUri==null){

        Context context = getApplicationContext();
            String text = getString(R.string.editor_insert_pet_failed);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();}
        else {
            Context context = getApplicationContext();
            String text = getString(R.string.editor_insert_pet_successful);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Na wybraniu odpowiedniego przedmiotu w menu
        switch (item.getItemId()) {
            // zebranie daty z pol i zapisanie w bazie danych w momencie klikniecia safe
            case R.id.action_save:
                collectData();
                finish();

                return true;
            // W razie klikniecie delete
            case R.id.action_delete:

                // Na razie nic
                return true;
            // RStrzalka do cofniecia
            case android.R.id.home:
                // Przekierowanie do poprzedniego activity
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
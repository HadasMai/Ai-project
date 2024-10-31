/**
 * HeroStoryQuestions - This activity allows the user to set up basic details about the hero for a storybook.
 * The user can choose the hero's character type (such as child, girl, man, woman, or animal) and provide a hero name.
 * For animal characters, an additional input for specifying the animal type is provided.
 * After saving the details, the user is navigated to either BookName or DescribeHero depending on the character type.
 */

package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class HeroStoryQuestions extends AppCompatActivity {

     // UI elements for hero details
    private EditText editTextHeroName;
    private EditText editTextAnimalType;
    private Button  buttonDescribeHero;
    private ImageButton buttonChild, buttonGirl, buttonMan, buttonWoman, buttonAnimal;
    private String characterType = "";

    // Firebase references for authentication and database
    private FirebaseAuth firebaseAuth;
    private DatabaseReference booksRef;
    private String bookId;

      /**
     * Called when the activity is first created.
     * Initializes the UI elements, retrieves the book ID, and sets up Firebase references.
     * Sets up listeners for character type buttons and save button.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_story_questions);

        // Initialize UI components
        editTextHeroName = findViewById(R.id.editTextHeroName);
        editTextAnimalType = findViewById(R.id.editTextAnimalType);
        buttonChild = findViewById(R.id.button_child);
        buttonGirl = findViewById(R.id.button_girl);
        buttonMan = findViewById(R.id.button_man);
        buttonWoman = findViewById(R.id.button_woman);
        buttonAnimal = findViewById(R.id.button_animal);
        buttonDescribeHero = findViewById(R.id.button_describe_hero);

        // Initialize Firebase Auth and Database reference
        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

         // Retrieve book ID from intent
        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null) {
            Toast.makeText(HeroStoryQuestions.this, "Failed to get book ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

         // Set listener for character type buttons
        buttonAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterType = "חיה";
                editTextAnimalType.setVisibility(View.VISIBLE);
            }
        });

        // Generic listener for selecting character type and handling button states
        View.OnClickListener setCharacterTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAnimalType.setVisibility(View.GONE);

                // Reset all buttons
                buttonChild.setSelected(false);
                buttonGirl.setSelected(false);
                buttonMan.setSelected(false);
                buttonWoman.setSelected(false);
                buttonAnimal.setSelected(false);

                // Set the clicked button as selected
                v.setSelected(true);

                 // Update character type based on selected button
                int id = v.getId();
                if (id == R.id.button_child) {
                    characterType = "boy";
                } else if (id == R.id.button_girl) {
                    characterType = "girl";
                } else if (id == R.id.button_man) {
                    characterType = "man";
                } else if (id == R.id.button_woman) {
                    characterType = "woman";
                } else if (id == R.id.button_animal) {
                    characterType = "חיה";
                    editTextAnimalType.setVisibility(View.VISIBLE);
                }
            }
        };

         // Apply the listener to character type buttons
        buttonChild.setOnClickListener(setCharacterTypeListener);
        buttonGirl.setOnClickListener(setCharacterTypeListener);
        buttonMan.setOnClickListener(setCharacterTypeListener);
        buttonWoman.setOnClickListener(setCharacterTypeListener);
        buttonAnimal.setOnClickListener(setCharacterTypeListener);

        // Listener for the button to save hero details and navigate to the next activity
        buttonDescribeHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHeroDetails();
                checkCharacterTypeAndNavigate();
            }
        });
    }

     /**
     * saveHeroDetails - Saves the hero's name and character type in Firebase.
     * For animal characters, also saves the animal type.
     */
    private void saveHeroDetails() {
        String heroName = editTextHeroName.getText().toString().trim();
        String animalType = editTextAnimalType.getText().toString().trim();

        // Save hero details in the specific book
        booksRef.child(bookId).child("heroName").setValue(heroName);
        booksRef.child(bookId).child("characterType").setValue(characterType);

        if (characterType.equals("חיה")) {
            booksRef.child(bookId).child("animalType").setValue(animalType);
        }
    }

   /**
     * checkCharacterTypeAndNavigate - Checks the character type and navigates to the appropriate activity.
     * If the character type is "חיה" (animal), navigates to BookName. Otherwise, navigates to DescribeHero.
     */
    private void checkCharacterTypeAndNavigate() {
        booksRef.child(bookId).child("characterType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String characterType = snapshot.getValue(String.class);
                if ("חיה".equals(characterType)) {
                    // If characterType is "חיה", go to BookName
                    Intent intent = new Intent(HeroStoryQuestions.this, BookName.class);
                    intent.putExtra("bookId", bookId);
                    startActivity(intent);
                } else {
                    // Otherwise, go to DescribeHero
                    Intent intent = new Intent(HeroStoryQuestions.this, DescribeHero.class);
                    intent.putExtra("bookId", bookId);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HeroStoryQuestions.this, "Failed to retrieve character type", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

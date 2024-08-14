//package com.example.mystoryapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class HeroStoryQuestions extends AppCompatActivity {
//
//    private EditText editTextHeroName;
//    private EditText editTextAnimalType;
//    private Button buttonChild, buttonGirl, buttonMan, buttonWoman, buttonAnimal, buttonDescribeHero;
//    private String characterType = "";
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference booksRef;
//    private String bookId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.hero_story_questions);
//        editTextHeroName = findViewById(R.id.editTextHeroName);
//        editTextAnimalType = findViewById(R.id.editTextAnimalType);
//        buttonChild = findViewById(R.id.button_child);
//        buttonGirl = findViewById(R.id.button_girl);
//        buttonMan = findViewById(R.id.button_man);
//        buttonWoman = findViewById(R.id.button_woman);
//        buttonAnimal = findViewById(R.id.button_animal);
//        buttonDescribeHero = findViewById(R.id.button_describe_hero);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
//                .getReference("Books");
//
//        // Get the bookId passed from UserAccount
//        bookId = getIntent().getStringExtra("bookId");
//        if (bookId == null) {
//            Toast.makeText(HeroStoryQuestions.this, "Failed to get book ID", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        buttonAnimal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                characterType = "חיה";
//                editTextAnimalType.setVisibility(View.VISIBLE);
//            }
//        });
//
//        View.OnClickListener setCharacterTypeListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editTextAnimalType.setVisibility(View.GONE);
//                int id = v.getId();
//                if (id == R.id.button_child) {
//                    characterType = "ילד";
//                } else if (id == R.id.button_girl) {
//                    characterType = "ילדה";
//                } else if (id == R.id.button_man) {
//                    characterType = "איש";
//                } else if (id == R.id.button_woman) {
//                    characterType = "אישה";
//                }
//            }
//        };
//
//        buttonChild.setOnClickListener(setCharacterTypeListener);
//        buttonGirl.setOnClickListener(setCharacterTypeListener);
//        buttonMan.setOnClickListener(setCharacterTypeListener);
//        buttonWoman.setOnClickListener(setCharacterTypeListener);
//
//        buttonDescribeHero.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveHeroDetails();
//                Intent intent = new Intent(HeroStoryQuestions.this, DescribeHero.class);
//                // Pass the bookId to HeroStoryQuestions
//                intent.putExtra("bookId", bookId);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void saveHeroDetails() {
//        String heroName = editTextHeroName.getText().toString().trim();
//        String animalType = editTextAnimalType.getText().toString().trim();
//
//        // Save hero details in the specific book
//        booksRef.child(bookId).child("heroName").setValue(heroName);
//        booksRef.child(bookId).child("characterType").setValue(characterType);
//
//        if (characterType.equals("חיה")) {
//            booksRef.child(bookId).child("animalType").setValue(animalType);
//        }
//    }
//}
package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText editTextHeroName;
    private EditText editTextAnimalType;
    private Button buttonChild, buttonGirl, buttonMan, buttonWoman, buttonAnimal, buttonDescribeHero;
    private String characterType = "";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference booksRef;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_story_questions);
        editTextHeroName = findViewById(R.id.editTextHeroName);
        editTextAnimalType = findViewById(R.id.editTextAnimalType);
        buttonChild = findViewById(R.id.button_child);
        buttonGirl = findViewById(R.id.button_girl);
        buttonMan = findViewById(R.id.button_man);
        buttonWoman = findViewById(R.id.button_woman);
        buttonAnimal = findViewById(R.id.button_animal);
        buttonDescribeHero = findViewById(R.id.button_describe_hero);

        firebaseAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance("https://mystory-2784d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Books");

        // Get the bookId passed from UserAccount
        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null) {
            Toast.makeText(HeroStoryQuestions.this, "Failed to get book ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterType = "חיה";
                editTextAnimalType.setVisibility(View.VISIBLE);
            }
        });

        View.OnClickListener setCharacterTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAnimalType.setVisibility(View.GONE);
                int id = v.getId();
                if (id == R.id.button_child) {
                    characterType = "ילד";
                } else if (id == R.id.button_girl) {
                    characterType = "ילדה";
                } else if (id == R.id.button_man) {
                    characterType = "איש";
                } else if (id == R.id.button_woman) {
                    characterType = "אישה";
                }
            }
        };
        buttonChild.setOnClickListener(setCharacterTypeListener);
        buttonGirl.setOnClickListener(setCharacterTypeListener);
        buttonMan.setOnClickListener(setCharacterTypeListener);
        buttonWoman.setOnClickListener(setCharacterTypeListener);

        buttonDescribeHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHeroDetails();
                checkCharacterTypeAndNavigate();
            }
        });
    }

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

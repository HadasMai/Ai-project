package com.example.mystoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class HeroStoryQuestions extends AppCompatActivity {

    private EditText editTextHeroName;
    private EditText editTextAnimalType;
    private Button buttonChild;
    private Button buttonGirl;
    private Button buttonMan;
    private Button buttonWoman;
    private Button buttonAnimal;
    private Button buttonDescribeHero;

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

        buttonAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAnimalType.setVisibility(View.VISIBLE);
            }
        });

        View.OnClickListener hideAnimalTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAnimalType.setVisibility(View.GONE);
            }
        };

        buttonChild.setOnClickListener(hideAnimalTypeListener);
        buttonGirl.setOnClickListener(hideAnimalTypeListener);
        buttonMan.setOnClickListener(hideAnimalTypeListener);
        buttonWoman.setOnClickListener(hideAnimalTypeListener);

        buttonDescribeHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroStoryQuestions.this, NewPage.class);
                startActivity(intent);
            }
        });
    }
}

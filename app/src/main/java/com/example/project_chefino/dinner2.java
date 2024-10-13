package com.example.project_chefino;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_chefino.LunchRecipe;
import com.example.project_chefino.LunchRecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class dinner2 extends AppCompatActivity {

    private RecyclerView lunchRecyclerView;
    private LunchRecipeAdapter lunchRecipeAdapter;
    private List<LunchRecipe> recipeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner2);

        lunchRecyclerView = findViewById(R.id.lunchRecyclerView);
        lunchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list
        recipeList = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        // Initialize the adapter with the list and set it to RecyclerView
        lunchRecipeAdapter = new LunchRecipeAdapter(recipeList);
        lunchRecyclerView.setAdapter(lunchRecipeAdapter);

        // Get Firebase reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes/dinner");

        // Fetch data from Firebase and update the list
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String cal = snapshot.child("cal").getValue(String.class);
                    String image = snapshot.child("image").getValue(String.class);
                    String preTime = snapshot.child("pre_time").getValue(String.class);
                    String vedio = snapshot.child("vedio").getValue(String.class);
                    String id = snapshot.child("id").getValue(String.class);

                    // Check if the recipe is bookmarked by checking the "bookmarked" node
                    DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference("bookmarked").child(userId).child(name);
                    bookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot bookmarkSnapshot) {
                            boolean isBookmarked = bookmarkSnapshot.exists(); // True if recipe is already bookmarked

                            // Create a new LunchRecipe object
                            LunchRecipe lunchRecipe = new LunchRecipe(name, preTime, image, cal, isBookmarked,vedio,id,"dinner");
                            recipeList.add(lunchRecipe);

                            // Notify the adapter that data has changed
                            lunchRecipeAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle possible errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}

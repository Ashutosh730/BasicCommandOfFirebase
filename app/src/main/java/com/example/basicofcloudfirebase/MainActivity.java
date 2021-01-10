package com.example.basicofcloudfirebase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private TextView loadingText;
    private EditText editTextTags;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef=db.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        loadingText = findViewById(R.id.loadingText);
        editTextPriority = findViewById(R.id.edit_text_priority);
        editTextTags  = findViewById(R.id.edit_text_tags);
        updateArray();
    }

    public void addNotes(View v) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0");
        }
        int priority = Integer.parseInt(editTextPriority.getText().toString());

        String allTags = editTextTags.getText().toString();
        String tags[] = allTags.split("\\s*,\\s*");
        Map<String, Boolean> allTagsList = new HashMap<>();

        for(String tag : tags){
            allTagsList.put(tag,true);
        }

        Note note = new Note(title, description, priority, allTagsList);

        notebookRef.add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Added Successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadNotes(View v) {
        notebookRef.whereArrayContains("tags", "ha1").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocId(documentSnapshot.getId());
                            String documentId = note.getDocId();
                            data += "ID: " + documentId;
                            for (String tag : note.getTags().keySet()) {
                                data += "\n-" + tag;
                            }
                            data += "\n\n";
                        }
                        loadingText.setText(data);
                    }
                });
    }
    private void updateArray(){
        notebookRef.document("BEfmRF01K02u1aCJL5cJ")
//                .update("tags", FieldValue.arrayUnion("hi1"));
        .update("tags.ha1.nested1.nested2.nested3",false);
    }

}
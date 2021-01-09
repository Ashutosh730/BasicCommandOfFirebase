package com.example.basicofcloudfirebase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;


public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private TextView loadingText;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef=db.collection("Notebook");
    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        loadingText = findViewById(R.id.loadingText);
        editTextPriority = findViewById(R.id.edit_text_priority);
        executeBatchedWrite();
    }

    public void addNotes(View v) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0");
        }
        int priority = Integer.parseInt(editTextPriority.getText().toString());


        Note note = new Note(title, description,priority);

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

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for(DocumentChange dc : value.getDocumentChanges()){
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    String id = documentSnapshot.getId();
                    int oldIndex = dc.getOldIndex();
                    int newIndex = dc.getNewIndex();

                    switch(dc.getType()){
                        case ADDED:
                            loadingText.append("\nAdded: " + id +
                                    "\nOld Index: " + oldIndex + "New Index: " + newIndex);
                            break;

                        case MODIFIED:
                            loadingText.append("\nModified: " + id +
                                    "\nOld Index: " + oldIndex + "New Index: " + newIndex);
                            break;

                        case REMOVED:
                            loadingText.append("\nRemove: " + id +
                                    "\nOld Index: " + oldIndex + "New Index: " + newIndex);
                            break;

                    }
                }
            }
        });
    }

    public void loadNotes(View v) {

        Query query;
        if (lastResult == null) {
            query = notebookRef.orderBy("priority")
                    .limit(3);
        } else {
            query = notebookRef.orderBy("priority")
                    .startAfter(lastResult)
                    .limit(3);
        }


        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocId(documentSnapshot.getId());
                            String documentId = note.getDocId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();
                            data += "ID: " + documentId
                                    + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";
                        }
                        if (queryDocumentSnapshots.size() > 0) {
                            data += "___________\n\n";
                            loadingText.append(data);
                            lastResult = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });
    }
    private void executeBatchedWrite() {
        WriteBatch batch = db.batch();
        DocumentReference doc1 = notebookRef.document("New Note");
        batch.set(doc1, new Note("New Note", "New Note", 1));
        DocumentReference doc2 = notebookRef.document("72FamhnDSkddrIIGhvYH");
        batch.update(doc2, "title", "Updated Note");
        DocumentReference doc3 = notebookRef.document("SWUsDtcEfECSJXE6zuZc");
        batch.delete(doc3);
        DocumentReference doc4 = notebookRef.document();
        batch.set(doc4, new Note("Added Note", "Added Note", 1));
        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingText.setText(e.toString());
            }
        });
    }
}
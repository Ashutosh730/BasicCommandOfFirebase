package com.example.basicofcloudfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPriority;
    private TextView loadingText;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/My First Note");
    private CollectionReference notebookRef=db.collection("Notebook");
    private ListenerRegistration noteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        loadingText = findViewById(R.id.loadingText);
        editTextPriority = findViewById(R.id.edit_text_priority);
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        noteListener = notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error != null){
//                    Toast.makeText(MainActivity.this, error.getMessage()+"", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String data= "";
//
//                if(value != null) {
//                    for(QueryDocumentSnapshot querySnapshot : value){
//
//                        Note note = querySnapshot.toObject(Note.class);
//                        note.setDocId(querySnapshot.getId());
//                        String documentId = note.getDocId();
//                        int priority = note.getPriority();
//                        data +=  "ID: " + documentId
//                                + "\nTitle: " + note.getTitle() + "\n Description:"
//                                + note.getDescription() + "\nPriority: " + priority+"\n\n";
//                    }
//                        loadingText.setText(data);
//                }
//                else{
//                    Toast.makeText(MainActivity.this, "No data to show!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        noteListener.remove();
//    }

//    public void updateDescription(View v){
//        String description = editTextDescription.getText().toString();
//        Map<String , Object> note = new HashMap<>();
//
//        note.put(KEY_DESCRIPTION,description);
//        noteRef.set(note , SetOptions.merge());
//        noteRef.update(note);
//    }
//
//    public void deleteDescription(View v){
//        Map<String , Object> note = new HashMap<>();
//
//        note.put(KEY_DESCRIPTION, FieldValue.delete());
//        noteRef.update(note);
//    }
//
//    public void deleteNote(View v){
//        noteRef.delete();
//    }

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

    public void loadNotes(View v) {
        Task task1 = notebookRef.whereGreaterThan("priority",2)
                .orderBy("priority")
                .get();

        Task task2 = notebookRef.whereEqualTo("title","ha")
                .get();


        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1 , task2 );
                allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> tasks) {
                        String data = "";
                        for (QuerySnapshot queryDocumentSnapshots : tasks) {
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
                        }
                        loadingText.setText(data);
                    }
                });

    }
}
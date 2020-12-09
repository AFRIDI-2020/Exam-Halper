package com.example.examhalperbtqm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private AlertDialog loginDialog;
    private DatabaseReference rootRef, adminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
    }

    //initializing variables
    private void init() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        adminRef = rootRef.child("admin");
    }

    //login button operations
    public void getLogin(View view) {
        showLoginDialog();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.login_dialog,null);
        EditText nameET = dialogView.findViewById(R.id.et_name);
        EditText passwordET = dialogView.findViewById(R.id.et_password);
        TextView teachersRoom = dialogView.findViewById(R.id.teacher_room);
        TextView adminPanel = dialogView.findViewById(R.id.admin_panel);
        builder.setView(dialogView);
        builder.setCancelable(false);
        loginDialog = builder.create();

        adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String password = passwordET.getText().toString();
                if(!name.isEmpty() && !password.isEmpty()){
                    loginWithNamePassword(name,password);
                }
            }
        });
        loginDialog.show();
    }

    private void loginWithNamePassword(String name, String password) {
        adminRef.child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    if(name.equals("Admin") && password.equals("admin")){
                        Toast.makeText(MainActivity.this, name+" "+password, Toast.LENGTH_SHORT).show();
                        createAdminChildInDatabase(password);
                        goToAdminActivity();
                        Log.d("TAG","dataSnapshot doesn't exist");
                    }
                }else {
                    Log.d("TAG","dataSnapshot exists");
                    String existingPassword = dataSnapshot.child("password").getValue().toString();
                    if(password.equals(existingPassword)){
                        goToAdminActivity();
                    }else {
                        Toast.makeText(MainActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createAdminChildInDatabase(String password) {
        adminRef.child("password").setValue(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "error: "+error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //to go MainActivity to AdminActivity
    private void goToAdminActivity() {
        Intent intent = new Intent(this,AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
package com.example.examhalperbtqm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    private AlertDialog changePasswordDialog;
    private DatabaseReference rootRef, adminRef, adminPasswordRef;
    private String existingPassword;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();
        setUpToolbar();
        existingPassword = getExistingPassword();
    }

    private void setUpToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void init() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        adminRef = rootRef.child("admin");
        adminPasswordRef = adminRef.child("password");
        toolbar = findViewById(R.id.admin_activity_toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.adminPasswordChange){
            showPasswordChangeDialog();
            return true;
        }else if(item.getItemId() == R.id.logOut){
            Toast.makeText(this, "log out", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPasswordChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View changePasswordDialogView = getLayoutInflater().inflate(R.layout.change_password_dialog,null);
        TextInputEditText currentPasswordET = changePasswordDialogView.findViewById(R.id.et_current_password);
        TextInputEditText newPasswordET = changePasswordDialogView.findViewById(R.id.et_new_password);
        TextInputEditText confirmNewPasswordET = changePasswordDialogView.findViewById(R.id.et_confirm_new_password);
        Button changePasswordButton = changePasswordDialogView.findViewById(R.id.change_password_button);
        builder.setView(changePasswordDialogView);
        builder.setCancelable(true);
        changePasswordDialog = builder.create();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = currentPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                String confirmNewPassword = confirmNewPasswordET.getText().toString();
                if(!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty()){
                    if(currentPassword.equals(existingPassword)){
                        if(newPassword.equals(confirmNewPassword)){
                            changePassword(newPassword);
                        }
                        else {
                            Toast.makeText(AdminActivity.this, "New passwords do not match!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(AdminActivity.this, "Wrong current password!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AdminActivity.this, "Please, fill up all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changePasswordDialog.show();
    }

    private void changePassword(String newPassword) {
        adminPasswordRef.setValue(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    changePasswordDialog.dismiss();
                }
                else {
                    Toast.makeText(AdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String getExistingPassword() {
        String password;
        adminPasswordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    existingPassword = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        password = existingPassword;

        return password;
    }

}
package com.vdsl.cybermart.Account.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.databinding.FragmentAddStaffsBinding;

public class FragmentAddStaff extends Fragment {
    FragmentAddStaffsBinding binding;
    FirebaseAuth userAuth;
    DatabaseReference userDatabase;
    FirebaseUser currentUser;
    int latestUserID = 0;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddStaffsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Account");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(getActivity());

        binding.btnSignUp.setOnClickListener(v -> {
            String userName = binding.edtUserNameSignUp.getText().toString().trim();
            String email = binding.edtEmailSignUp.getText().toString().trim();
            String password = binding.edtPassSignUp.getText().toString().trim();
            String confirmPassword = binding.edtConfirmPassSignUp.getText().toString().trim();

            boolean error = false;
            String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

            if (TextUtils.isEmpty(userName)) {
                binding.edtUserNameSignUp.setError("Please enter Staff name!");
                error = true;
            }
            if (TextUtils.isEmpty(email)) {
                binding.edtEmailSignUp.setError("Please enter Staff Email!");
                error = true;
            } else if (!email.matches(emailPattern)) {
                binding.edtEmailSignUp.setError("Wrong email format!");
                error = true;
            }
            if (TextUtils.isEmpty(password)) {
                binding.edtPassSignUp.setError("Please enter Password!");
                error = true;

            } else if (password.length() < 6) {
                binding.edtPassSignUp.setError("Please enter more than 6 characters!");
                error = true;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                binding.edtConfirmPassSignUp.setError("Please confirm Password!");
                error = true;

            } else if (!confirmPassword.equals(password)) {
                binding.edtConfirmPassSignUp.setError("Password is not match!");
                error = true;
            }
            if (!error) {
                Log.e("check41", "onViewCreated: " + "done");
                progressDialog.setMessage("loading...");
                progressDialog.show();
                if (userAuth.getCurrentUser() != null) {
                    userDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                progressDialog.dismiss();
                                binding.edtEmailSignUp.setError("Email already exists!");
                            } else {
                                userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        String userID = dataSnapshot.getKey();
                                                        if (userID != null && userID.startsWith("Id")) {
                                                            int id = Integer.parseInt(userID.substring(2));
                                                            if (id > latestUserID) {
                                                                latestUserID = id;
                                                            }
                                                        }
                                                    }

                                                    latestUserID++;
                                                    String ID = "Id" + latestUserID;
                                                    DatabaseReference currentUserDB = userDatabase.child(ID);
                                                    currentUserDB.child("fullName").setValue(userName);
                                                    currentUserDB.child("email").setValue(email);
                                                    currentUserDB.child("role").setValue("Staff");
                                                    currentUserDB.child("status").setValue("offline");
                                                    progressDialog.dismiss();
                                                    General.showSuccessPopup(requireContext(),
                                                                             "Successfully", "An staff account " +
                                                                                     "added successfully",
                                                                             new OnDialogButtonClickListener() {
                                                                                 @Override
                                                                                 public void onDismissClicked(Dialog dialog) {
                                                                                     super.onDismissClicked(dialog);
                                                                                     binding.edtUserNameSignUp.setText(null);
                                                                                     binding.edtEmailSignUp.setText(null);
                                                                                     binding.edtPassSignUp.setText(null);
                                                                                     binding.edtConfirmPassSignUp.setText(null);
                                                                                 }
                                                                             });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    progressDialog.dismiss();
                                                }
                                            });
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(requireContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            } else {
                progressDialog.dismiss();
            }
        });
    }
}

package com.vdsl.cybermart.Message;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.Message.Adapter.ViewPagerAdapter;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentMessageBinding;

import java.util.HashMap;

public class FragmentMessage extends Fragment {

    FragmentMessageBinding binding;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String userEmail;

    @Override
    public void onStart() {
        super.onStart();
        getIdFromEmail(userEmail, new OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                if (id != null) {
                    reference = FirebaseDatabase.getInstance().getReference("Account").child(id);


                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("FragmentMessage", "Snapshot value: " + firebaseUser.getUid());
                            UserModel user = snapshot.getValue(UserModel.class);

                            if (user != null) {
                                binding.txtUsername.setText(user.getFullName());
                                if (user.getAvatar() != null && !user.getAvatar().isEmpty() ){
                                    Picasso.get().load(user.getAvatar()).into(binding.imgProfile, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("check47", "onSuccess: " + user.getAvatar());
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            binding.imgProfile.setImageResource(R.drawable.img_default_profile_image);
                                        }
                                    });
                                }else {
                                    binding.imgProfile.setImageResource(R.drawable.img_default_profile_image);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra trong quá trình truy vấn
                        }
                    });
                } else {
                    Log.d("FragmentMessage", "No user found with this email");
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         userEmail = firebaseUser.getEmail();
        Log.d("fragcheck", "Snapshot value: " + userEmail);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(viewPagerAdapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout,binding.viewPager,((tab, i) -> {
            switch (i){
                case 0:{
                    tab.setText("Chats");
                    break;
                }
                case 1:{
                    tab.setText("History");
                    break;
                }
            }
        }));
        mediator.attach();
    }

    private void getIdFromEmail(String email, final OnIdReceivedListener listener) {
        DatabaseReference peopleReference = FirebaseDatabase.getInstance().getReference().child("Account");
        peopleReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String accountId = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        accountId = dataSnapshot1.getKey();
                        Log.d("TAG", "onDataChange: sdone" + accountId);
                        break;
                    }
                }
                listener.onIdReceived(accountId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onIdReceived(null);
            }
        });
    }

    public interface OnIdReceivedListener {
        void onIdReceived(String id);
    }


}

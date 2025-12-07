package com.example.myapplication.UI.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private TextView tvEmpty;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Setup Toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        rvUsers = findViewById(R.id.rv_users);
        tvEmpty = findViewById(R.id.tv_empty);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        userList = new ArrayList<>();
        
        setupRecyclerView();
        loadUsers();
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(userList);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void loadUsers() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
                
                if (userList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvUsers.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvUsers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Inner Adapter Class
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> users;

        public UserAdapter(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.tvName.setText(user.getName() != null ? user.getName() : "No Name");
            holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email");
            
            String role = user.getRole() != null ? user.getRole() : "user";
            holder.tvRole.setText("Role: " + role);

            // SỬA LỖI Ở ĐÂY: Dùng getImageUrl() thay vì getAvatar()
            if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(user.getImageUrl())
                        .placeholder(R.drawable.ic_dash_user)
                        .into(holder.imgAvatar);
            } else {
                holder.imgAvatar.setImageResource(R.drawable.ic_dash_user);
            }
            
            // Xử lý sự kiện click vào user nếu cần (ví dụ: xem chi tiết, xóa)
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(ManageUsersActivity.this, "User: " + user.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            CircleImageView imgAvatar;
            TextView tvName, tvEmail, tvRole;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                imgAvatar = itemView.findViewById(R.id.img_user_avatar);
                tvName = itemView.findViewById(R.id.tv_user_name);
                tvEmail = itemView.findViewById(R.id.tv_user_email);
                tvRole = itemView.findViewById(R.id.tv_user_role);
            }
        }
    }
}

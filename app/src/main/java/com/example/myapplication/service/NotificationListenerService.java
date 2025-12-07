package com.example.myapplication.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationListenerService extends Service {

    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "order_status_channel";

    private DatabaseReference notificationRef;
    private ChildEventListener childEventListener;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            notificationRef = FirebaseDatabase.getInstance()
                    .getReference("notifications")
                    .child(currentUser.getUid());
            startListeningForNotifications();
        }
        // START_STICKY sẽ khởi động lại service nếu nó bị hệ thống hủy
        return START_STICKY;
    }

    private void startListeningForNotifications() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        Log.d(TAG, "New notification received: " + notification.getTitle());
                        showNotification(notification);
                        // Xóa thông báo sau khi đã hiển thị để không hiển thị lại
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Listener was cancelled", error.toException());
                }
            };
            notificationRef.addChildEventListener(childEventListener);
        }
    }

    private void showNotification(Notification notification) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Thay bằng icon của bạn
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Kiểm tra quyền trước khi gửi
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Trong một ứng dụng thực tế, bạn nên yêu cầu quyền ở Activity
            Log.w(TAG, "POST_NOTIFICATIONS permission not granted");
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Status";
            String description = "Notifications for order status updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationRef != null && childEventListener != null) {
            notificationRef.removeEventListener(childEventListener);
        }
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

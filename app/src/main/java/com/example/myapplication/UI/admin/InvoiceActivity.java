package com.example.myapplication.UI.admin;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.BillDetailAdapter;
import com.example.myapplication.model.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class InvoiceActivity extends AppCompatActivity {

    private TextView txtStoreName, txtStoreAddress, txtStorePhone, txtTime;
    private TextView txtBillId, txtCashier, txtCustomer, txtAddress;
    private TextView txtTotal, txtPaymentMethod;
    private RecyclerView recyclerBillItems;
    private View billRootLayout;
    private Button btnExportInvoice;

    private Order currentOrder;
    private BillDetailAdapter adapter;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,### đ");

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Nhận dữ liệu Order từ Intent (key: "order")
        if (getIntent() != null && getIntent().hasExtra("order")) {
            currentOrder = (Order) getIntent().getSerializableExtra("order");
        } else {
            // Fallback check uppercase "ORDER" just in case
            if (getIntent() != null && getIntent().hasExtra("ORDER")) {
                currentOrder = (Order) getIntent().getSerializableExtra("ORDER");
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin đơn hàng!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        initViews();
        displayOrderData();
        setupRecyclerView();

        btnExportInvoice.setOnClickListener(v -> exportInvoice());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmapToGallery();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                saveBitmapToGallery();
            }
        }
    }

    private void initViews() {
        txtStoreName = findViewById(R.id.txtStoreName);
        txtStoreAddress = findViewById(R.id.txtStoreAddress);
        txtStorePhone = findViewById(R.id.txtStorePhone);
        txtTime = findViewById(R.id.txtTime);
        txtBillId = findViewById(R.id.txtBillId);
        txtCashier = findViewById(R.id.txtCashier);
        txtCustomer = findViewById(R.id.txtCustomer);
        txtAddress = findViewById(R.id.txtAddress);
        txtTotal = findViewById(R.id.txtTotal);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);

        recyclerBillItems = findViewById(R.id.recyclerBillItems);
        billRootLayout = findViewById(R.id.billRootLayout);
        btnExportInvoice = findViewById(R.id.btnExportInvoice);
    }

    private void displayOrderData() {
        if (currentOrder == null) return;

        // Set cứng text Aura Coffee (Bold, size lớn đã chỉnh trong XML)
        txtStoreName.setText("Aura Coffee");

        // Hiển thị mã đơn
        txtBillId.setText("Mã hóa đơn: " + (currentOrder.getOrderId() != null ? currentOrder.getOrderId() : "---"));

        // Hiển thị ngày giờ
        txtTime.setText("Ngày: " + currentOrder.getFormattedDate());

        // Hiển thị tên khách (nếu null/rỗng thì hiện "Khách lẻ")
        String customerName = currentOrder.getCustomerName();
        if (customerName == null || customerName.isEmpty()) customerName = "Khách lẻ";
        txtCustomer.setText("Khách hàng: " + customerName);

        // Hiển thị địa chỉ giao hàng
        String address = currentOrder.getShippingAddress();
        if (address == null || address.isEmpty()) address = "---";
        txtAddress.setText("Địa chỉ: " + address);

        // Phương thức thanh toán
        txtPaymentMethod.setText("Thanh toán: " + (currentOrder.getPaymentMethod() != null ? currentOrder.getPaymentMethod() : "Tiền mặt"));

        // Format số tiền totalAmount
        double total = currentOrder.getTotalAmount();
        txtTotal.setText("Tổng cộng: " + decimalFormat.format(total));
    }

    private void setupRecyclerView() {
        if (recyclerBillItems != null) {
            recyclerBillItems.setLayoutManager(new LinearLayoutManager(this));
            if (currentOrder.getCartItems() != null) {
                adapter = new BillDetailAdapter(this, currentOrder.getCartItems());
                recyclerBillItems.setAdapter(adapter);
            }
        }
    }

    // Hàm xuất hóa đơn: Chụp màn hình và lưu ảnh
    private void exportInvoice() {
        // Kiểm tra quyền đối với Android < 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
                return;
            }
        }
        saveBitmapToGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveBitmapToGallery();
            } else {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để lưu hóa đơn", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveBitmapToGallery() {
        Bitmap bitmap = getBitmapFromView(billRootLayout);
        String fileName = "Invoice_" + (currentOrder != null ? currentOrder.getOrderId() : System.currentTimeMillis()) + ".jpg";

        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues resolver = new ContentValues();
                resolver.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                resolver.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                resolver.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CoffeeInvoices");

                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, resolver);
                if (imageUri != null) {
                    fos = getContentResolver().openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (fos != null) fos.close();
                    Toast.makeText(this, "Đã lưu hóa đơn vào thư viện ảnh", Toast.LENGTH_LONG).show();
                }
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File image = new File(imagesDir, fileName);
                fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Toast.makeText(this, "Đã lưu hóa đơn tại: " + image.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        android.graphics.drawable.Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}

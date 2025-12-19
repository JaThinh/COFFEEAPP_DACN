package com.example.myapplication.UI.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
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
import java.util.ArrayList;

public class InvoiceActivity extends AppCompatActivity {

    private TextView txtStoreName, txtStoreAddress, txtStorePhone, txtTime;
    private TextView txtBillId, txtCashier, txtCustomer, txtEmail;
    private TextView txtTotal, txtPaymentMethod;
    private RecyclerView recyclerBillItems;
    private View billRootLayout;

    private Order currentOrder;
    private BillDetailAdapter adapter;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,### đ");

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill); // Dùng lại layout activity_bill.xml

        // Nhận dữ liệu Order từ Intent
        if (getIntent().hasExtra("ORDER")) {
            currentOrder = (Order) getIntent().getSerializableExtra("ORDER");
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayOrderData();
        setupRecyclerView();

        // Nút xuất PDF có thể được thêm vào layout hoặc gọi từ menu. 
        // Ở đây giả sử layout có sẵn nút hoặc sự kiện click vào root layout để demo
        billRootLayout.setOnClickListener(v -> checkPermissionAndExportPDF());
        
        Toast.makeText(this, "Chạm vào hóa đơn để xuất PDF", Toast.LENGTH_LONG).show();
    }

    private void initViews() {
        txtStoreName = findViewById(R.id.txtStoreName);
        txtStoreAddress = findViewById(R.id.txtStoreAddress);
        txtStorePhone = findViewById(R.id.txtStorePhone);
        txtTime = findViewById(R.id.txtTime);
        txtBillId = findViewById(R.id.txtBillId);
        txtCashier = findViewById(R.id.txtCashier);
        txtCustomer = findViewById(R.id.txtCustomer);
        txtEmail = findViewById(R.id.txtEmail);
        txtTotal = findViewById(R.id.txtTotal);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        
        // RecyclerView không có trong layout activity_bill gốc bạn cung cấp (dùng TableLayout).
        // Tuy nhiên, theo yêu cầu mới, ta cần hiển thị danh sách bằng RecyclerView.
        // Giải pháp: Thay thế TableLayout bằng RecyclerView trong code XML hoặc tìm ID view phù hợp.
        // Giả sử bạn đã thay thế TableLayout bằng RecyclerView có id recyclerBillItems trong layout.
        // Nếu chưa, tôi sẽ ánh xạ tạm vào TableLayout và cần bạn sửa XML, 
        // hoặc tìm view cha chứa items.
        
        // Trong layout bạn gửi, id là layoutItems (TableLayout).
        // Để hiển thị đúng yêu cầu Recycler, ta cần sửa layout XML. 
        // Nhưng tôi không được sửa layout XML trong yêu cầu này (chỉ yêu cầu Java).
        // -> Tôi sẽ tìm id recyclerBillItems giả định layout đã được update.
        // Nếu không tìm thấy, app sẽ crash. 
        // FIX: Vì tôi không thể sửa layout XML ở đây, tôi sẽ coi như layoutItems là nơi chứa list.
        // Nhưng Adapter yêu cầu RecyclerView.
        // Tốt nhất tôi sẽ tìm RecyclerView, nếu null thì log thông báo.
        
        // Để code chạy được với layout hiện tại (chưa có RecyclerView), tôi sẽ add RecyclerView programmatically 
        // hoặc giả định ID recyclerBillItems tồn tại (bạn cần thêm vào XML).
        // Vì tôi đã viết file adapter, tôi sẽ ánh xạ RecyclerView.
        
        recyclerBillItems = findViewById(R.id.recyclerBillItems); 
        // Lưu ý: Cần thêm RecyclerView vào activity_bill.xml thay cho TableLayout @id/layoutItems
        
        billRootLayout = findViewById(R.id.billRootLayout);
    }

    private void displayOrderData() {
        if (currentOrder == null) return;

        txtStoreName.setText("22 AUGUST COFFEE");
        // txtStoreAddress & Phone giữ nguyên hardcode hoặc lấy từ config
        
        txtBillId.setText("Mã hóa đơn: " + (currentOrder.getOrderId() != null ? currentOrder.getOrderId() : "---"));
        txtTime.setText("Ngày đặt: " + currentOrder.getFormattedDate());
        
        String customerName = currentOrder.getCustomerName();
        if (customerName == null || customerName.isEmpty()) customerName = "Khách lẻ";
        txtCustomer.setText("Khách hàng: " + customerName);
        
        // Nếu có email trong Order model thì set, không thì ẩn hoặc để trống
        // Model Order hiện tại không thấy field email rõ ràng, có thể dùng userId hoặc bỏ qua
        txtEmail.setVisibility(View.GONE); 

        txtPaymentMethod.setText("Thanh toán: " + (currentOrder.getPaymentMethod() != null ? currentOrder.getPaymentMethod() : "Tiền mặt"));
        
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

    private void checkPermissionAndExportPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ không cần quyền WRITE_EXTERNAL_STORAGE cho MediaStore
            exportOrderToPDF(billRootLayout);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, 
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                exportOrderToPDF(billRootLayout);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportOrderToPDF(billRootLayout);
            } else {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để lưu PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void exportOrderToPDF(View view) {
        // Tạo Bitmap từ View
        Bitmap bitmap = getBitmapFromView(view);
        
        // Tạo PDF Document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        // Lưu file
        String fileName = "Invoice_" + (currentOrder != null ? currentOrder.getOrderId() : System.currentTimeMillis()) + ".pdf";
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Lưu vào thư mục Download/Documents bằng MediaStore
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/CoffeeInvoices");
                
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    document.writeTo(outputStream);
                    if (outputStream != null) outputStream.close();
                    Toast.makeText(this, "Đã lưu PDF vào thư mục Download/CoffeeInvoices", Toast.LENGTH_LONG).show();
                }
            } else {
                // Lưu theo cách cũ cho Android 9 trở xuống
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(directory, fileName);
                document.writeTo(new FileOutputStream(file));
                Toast.makeText(this, "Đã lưu PDF tại: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }

    private Bitmap getBitmapFromView(View view) {
        // Measure và layout lại view để đảm bảo kích thước đúng
        // (Chỉ cần thiết nếu view chưa hiển thị, ở đây view đã hiển thị trên màn hình)
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        // Vẽ background trắng nếu view trong suốt
        android.graphics.drawable.Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) 
            bgDrawable.draw(canvas);
        else 
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}

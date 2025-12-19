package com.example.myapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

public class PdfInvoiceGenerator {

    public static void generateInvoice(Context context, Order order) {
        // 1. Inflate Layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_invoice_pdf, null);

        // 2. Bind dữ liệu vào View
        bindDataToView(context, view, order);

        // 3. Tính toán kích thước View để PDF không bị cắt
        // A4 size in points (1 point = 1/72 inch)
        int a4Width = 595;
        int a4Height = 842;

        view.measure(
                View.MeasureSpec.makeMeasureSpec(a4Width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int measuredHeight = view.getMeasuredHeight();
        view.layout(0, 0, a4Width, measuredHeight);

        // 4. Tạo PDF Document
        PdfDocument document = new PdfDocument();
        // Đảm bảo chiều cao trang đủ chứa nội dung
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(a4Width, measuredHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // 5. Vẽ View lên Canvas của PDF
        view.draw(canvas);
        document.finishPage(page);

        // 6. Lưu file PDF
        savePdf(context, document, order.getOrderId());
    }

    private static void bindDataToView(Context context, View view, Order order) {
        TextView txtBillId = view.findViewById(R.id.txtBillId);
        TextView txtTime = view.findViewById(R.id.txtTime);
        TextView txtCustomer = view.findViewById(R.id.txtCustomer);
        TextView txtStoreName = view.findViewById(R.id.txtStoreName);
        TextView txtTotal = view.findViewById(R.id.txtTotal);
        TextView txtPaymentMethod = view.findViewById(R.id.txtPaymentMethod);
        TableLayout layoutItems = view.findViewById(R.id.layoutItems);

        // Định dạng tiền tệ
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Điền thông tin chính
        txtBillId.setText("#" + order.getOrderId().substring(0, 6).toUpperCase());
        txtTime.setText(order.getFormattedDate());
        txtCustomer.setText(order.getCustomerName());
        txtStoreName.setText("22 AUGUST COFFEE"); // Hardcoded as requested
        txtTotal.setText(currencyFormat.format(order.getTotalAmount()));
        txtPaymentMethod.setText(order.getPaymentMethod());

        // Xóa các row mẫu (nếu có) trước khi thêm row mới
        layoutItems.removeAllViews();
        
        // Thêm Header cho bảng
        addHeaderRow(context, layoutItems);

        // Thêm danh sách sản phẩm
        if (order.getCartItems() != null) {
            for (CartItem item : order.getCartItems()) {
                TableRow row = new TableRow(context);

                TextView name = createTextView(context, item.getProductName(), TableRow.LayoutParams.WRAP_CONTENT, 0.4f);
                TextView qty = createTextView(context, String.valueOf(item.getQuantity()), TableRow.LayoutParams.WRAP_CONTENT, 0.15f);
                TextView price = createTextView(context, currencyFormat.format(item.getProductPrice()), TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
                TextView lineTotal = createTextView(context, currencyFormat.format(item.getQuantity() * item.getProductPrice()), TableRow.LayoutParams.WRAP_CONTENT, 0.25f);
                
                price.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                lineTotal.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                qty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                row.addView(name);
                row.addView(qty);
                row.addView(price);
                row.addView(lineTotal);
                layoutItems.addView(row);
            }
        }
    }
    
    private static void addHeaderRow(Context context, TableLayout layout) {
        TableRow headerRow = new TableRow(context);
        TextView hName = createHeaderTextView(context, "Tên món", 0.4f);
        TextView hQty = createHeaderTextView(context, "SL", 0.15f);
        TextView hPrice = createHeaderTextView(context, "Giá", 0.2f);
        TextView hTotal = createHeaderTextView(context, "Thành tiền", 0.25f);

        hQty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        hPrice.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        hTotal.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

        headerRow.addView(hName);
        headerRow.addView(hQty);
        headerRow.addView(hPrice);
        headerRow.addView(hTotal);
        layout.addView(headerRow);
    }

    private static TextView createTextView(Context context, String text, int height, float weight) {
        TextView textView = new TextView(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, height, weight);
        params.setMargins(0, 4, 0, 4); // Thêm khoảng cách dọc
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(context.getResources().getColor(android.R.color.black));
        textView.setPadding(4, 8, 4, 8);
        return textView;
    }
    
    private static TextView createHeaderTextView(Context context, String text, float weight) {
        TextView textView = createTextView(context, text, TableRow.LayoutParams.WRAP_CONTENT, weight);
        textView.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Bold);
        return textView;
    }

    private static void savePdf(Context context, PdfDocument document, String orderId) {
        String fileName = "HoaDon_" + orderId.substring(0, 6) + ".pdf";
        Uri pdfUri = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Dùng MediaStore cho Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                pdfUri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (pdfUri == null) {
                    throw new IOException("Failed to create new MediaStore record.");
                }

                try (OutputStream os = context.getContentResolver().openOutputStream(pdfUri)) {
                    document.writeTo(os);
                }
            } else {
                // Cách cũ cho Android < 10
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    document.writeTo(fos);
                }
                pdfUri = Uri.fromFile(file); // Uri cho cách cũ
            }
            document.close();
            Toast.makeText(context, "Lưu hóa đơn thành công!", Toast.LENGTH_LONG).show();

            // Mở file PDF ngay lập tức
            openPdf(context, pdfUri);

        } catch (IOException e) {
            Toast.makeText(context, "Lỗi khi lưu PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void openPdf(Context context, Uri fileUri) {
        if (fileUri == null) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Không tìm thấy ứng dụng để mở file PDF.", Toast.LENGTH_SHORT).show();
        }
    }
}
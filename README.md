# ☕ COFFEE AURA APP

> Ứng dụng bán cà phê trực tuyến toàn diện: Khách hàng đặt đồ uống & Chat hỗ trợ - Admin quản lý vận hành.
> 🛠️ Phát triển: **Android (Java/XML)** | Backend: **Firebase Realtime Database**.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)

---

## 📑 Table of Contents
- [📖 Overview](#-overview)
- [🏗️ Tech Stack](#️-tech-stack)
- [🚀 Features](#-features)
- [🎯 Use Case Diagram](#-use-case-diagram)
- [📸 Screenshots](#-screenshots)
- [⚙️ Installation & Setup](#️-installation--setup)
- [💬 Contact](#-contact)

---

## 📖 Overview

**Coffee Shop App** là giải pháp phần mềm hiện đại phục vụ mô hình kinh doanh F&B, giúp kết nối khách hàng và cửa hàng một cách liền mạch:
- **Khách hàng:** Duyệt menu, chọn topping, đặt hàng, thanh toán và theo dõi lịch sử mua sắm.
- **Tương tác:** Tính năng Chat Realtime giúp khách hàng nhận tư vấn trực tiếp từ Admin.
- **Công nghệ:** Dữ liệu được đồng bộ hóa tức thì (Realtime) trên nền tảng đám mây Firebase.

---

## 🏗️ Tech Stack

| Thành phần | Công nghệ / Mô tả |
|-------------|-------------------|
| **IDE** | 🧰 Android Studio |
| **Ngôn ngữ** | ☕ Java + XML |
| **Thiết kế UI/UX** | 🎨 Figma |
| **Cơ sở dữ liệu** | 💾 Firebase Realtime Database (Lưu trữ Sản phẩm, Đơn hàng, Chat, User) |
| **Xác thực** | 🔐 Firebase Authentication (Quản lý phiên đăng nhập Admin/User) |
| **Lưu trữ ảnh** | 🖼️ Firebase Storage (Ảnh sản phẩm, Avatar) |
| **Thư viện UI** | 🧩 RecyclerView, Glide, Material Design Components |
| **Version Control**| 🗂️ Git & GitHub |
| **Tương thích** | 📱 Android 7.0 (API Level 24) trở lên |

---

## 🚀 Features

### 👤 Khách hàng (Customer)
- 🔐 **Authentication:** Đăng ký và Đăng nhập bảo mật qua Firebase.
- ☕ **Browse Products:** Xem danh sách đồ uống trực quan, cập nhật theo thời gian thực.
- 🛒 **Smart Cart:** Thêm/sửa/xóa món, tính tổng tiền và đặt hàng nhanh chóng.
- 🧾 **Order History:** Xem lại lịch sử các đơn hàng đã đặt.
- 💬 **Live Chat:** Nhắn tin trực tiếp với Admin để được hỗ trợ.

### 👨‍💼 Admin
- 🗂️ **Product Management:** Thêm mới, chỉnh sửa giá/ảnh, xóa đồ uống khỏi thực đơn.
- 📦 **Order Management:** Tiếp nhận đơn hàng mới, theo dõi trạng thái xử lý.
- 💬 **Support Center:** Phản hồi tin nhắn của khách hàng qua giao diện Chat Admin.

---

## 🎯 Use Case Diagram

Sơ đồ mô tả các chức năng cốt lõi của hệ thống:

1. **Tổng thể:** Quan hệ giữa Admin - Khách hàng - Hệ thống.
2. **Admin:** Quản lý sản phẩm & Xử lý đơn hàng.
3. **Customer:** Đặt hàng, Thanh toán & Chat.

*(Lưu ý: Upload ảnh vào thư mục `images/` trong project để hiển thị)*

---

## 📸 Screenshots

Một số hình ảnh thực tế của ứng dụng:

*(Lưu ý: Upload ảnh vào thư mục `screenshots/` trong project để hiển thị)*

---

## ⚙️ Installation & Setup

Để chạy dự án này trên máy cục bộ (Local Machine), vui lòng làm theo các bước sau:

### 1️⃣ Clone Project
Mở Terminal (hoặc Git Bash) và chạy lệnh:

```bash
git clone [https://github.com/JaThinh/COFFEEAPP_DACN.git](https://github.com/JaThinh/COFFEEAPP_DACN.git)
2️⃣ Mở trong Android Studio
Khởi động Android Studio.

Chọn File > Open.

Điều hướng đến thư mục COFFEEAPP_DACN vừa clone và nhấn OK.

Đợi Gradle build project (quá trình này có thể mất vài phút).

3️⃣ Cấu hình Firebase (Quan trọng ⚠️)
Dự án cần file cấu hình để kết nối với Firebase của riêng bạn:

Tạo project mới trên Firebase Console.

Kích hoạt Authentication (Email/Password), Realtime Database, và Storage.

Tải file google-services.json từ Firebase Console.

Copy file này vào thư mục app/ trong project Android Studio: COFFEEAPP_DACN/app/google-services.json

Nhấn Sync Project with Gradle Files.

💬 Contact
Mọi thắc mắc hoặc góp ý phát triển, vui lòng liên hệ:
📧 Email: [phamlegiathinh2004@gmail.com] 🔗 GitHub: JaThinh

© 2025 Coffee Shop App - Developed by JaThinh

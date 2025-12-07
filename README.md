# ☕ August 22

> Ứng dụng bán cà phê trực tuyến giúp khách hàng mua hàng, đặt đồ uống, xem lịch sử đơn hàng, chat với admin  
> Phát triển bằng **Android (Java/XML)**, sử dụng **Firebase Realtime Database** làm backend.

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
Ứng dụng bán cà phê hiện đại giúp người dùng:
- Duyệt và chọn đồ uống trực tiếp trên **app**.  
- Quản lý giỏ hàng, đặt hàng, và xem lại lịch sử mua hàng.  
- Trò chuyện với admin để được hỗ trợ và cập nhật trạng thái đơn.  
- Tất cả dữ liệu đều được đồng bộ **realtime** thông qua **Firebase**.

---

## 🏗️ Tech Stack

| Thành phần | Công nghệ / Mô tả |
|-------------|-------------------|
| **IDE** | 🧰 Android Studio |
| **Ngôn ngữ** | ☕ Java + XML |
| **Thiết kế UI/UX** | 🎨 Figma |
| **Cơ sở dữ liệu** | 💾 Firebase Realtime Database — lưu **sản phẩm**, **đơn hàng**, **tin nhắn**, **người dùng** |
| **Xác thực người dùng** | 🔐 Firebase Authentication — quản lý tài khoản **Admin** và **Khách hàng** |
| **Lưu trữ hình ảnh** | 🖼️ Firebase Storage — lưu ảnh sản phẩm, ảnh đại diện người dùng |
| **Thư viện giao diện** | 🧩 RecyclerView, Glide, Material Components |
| **Quản lý phiên bản** | 🗂️ Git & GitHub |
| **Hệ điều hành chạy app** | 📱 Android 7.0+ (API 24 trở lên) |

---

## 🚀 Features

### 👤 Khách hàng (Customer)
- 🔐 Đăng ký / Đăng nhập bằng Firebase Authentication  
- ☕ Xem danh sách sản phẩm trực tiếp trong app (Firebase Realtime Database)  
- 🛒 Giỏ hàng & Thanh toán: thêm, xóa, chỉnh sửa sản phẩm trong giỏ và hoàn tất đơn hàng  
- 🧾 Xem lịch sử đơn hàng: hiển thị danh sách đơn đã mua  
- 💬 Chat realtime với Admin để nhận hỗ trợ hoặc tư vấn sản phẩm  

### 👨‍💼 Admin
- 🗂️ Quản lý sản phẩm: thêm, sửa, xóa sản phẩm (Firebase Realtime Database + Storage)  
- 📦 Quản lý đơn hàng: theo dõi và xác nhận đơn hàng của khách  
- 💬 Chat hỗ trợ khách hàng qua hệ thống Firebase Chat Realtime  

---

## 🎯 Use Case Diagram

Ứng dụng bao gồm các sơ đồ mô tả các chức năng chính:

1. **Use Case Diagram tổng thể** – mô tả mối quan hệ giữa **Admin** và **Khách hàng** với hệ thống  
2. **Use Case: Quản lý sản phẩm (Admin)** – thêm, sửa, xóa, cập nhật sản phẩm  
3. **Use Case: Đặt hàng & Thanh toán (Customer)** – chọn sản phẩm, thêm giỏ hàng, thanh toán  
4. **Use Case: Lịch sử đơn hàng** – xem chi tiết đơn đã mua  
5. **Use Case: Chat realtime** – trò chuyện giữa **khách hàng** và **admin**  

📸 *Chèn hình sơ đồ *

![Use Case Tổng thể](images/usecase_main.png)
![Use Case Quản lý sản phẩm](images/usecase_admin.png)
![Use Case Chat Realtime](images/usecase_chat.png)

---

## 📸 Screenshots

📱 *Chèn ảnh giao diện app *

![Trang chủ](screenshots/home.png)
![Giỏ hàng](screenshots/cart.png)
![Lịch sử đơn hàng](screenshots/order_history.png)
![Chat Realtime](screenshots/chat.png)

---

## ⚙️ Installation & Setup

### 1️⃣ Clone project
```bash
git clone https://github.com/hoainamcode22/COFFEE.git

## 💬 Contact

📧 Email: codenamtap@gmail.com

🔗 GitHub: hoainamcode22

🌐 Portfolio: https://hoainam-portfolio-1.vercel.app

📱 LinkedIn: Nguyễn Hoài Nam

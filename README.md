# DOAN
Đồ án tốt nghiệp
Phần Backend của hệ thống được xây dựng theo kiến trúc RESTful API , sử dụng ngôn ngữ Java kết hợp với framework Spring Boot để cung cấp các dịch vụ xử lý nghiệp vụ cho ứng dụng.


🛠 Công nghệ sử dụngNgôn ngữ lập trình:
-Java (JDK 1.8).
-Framework chính: Spring Boot.
-Quản lý cơ sở dữ liệu: Spring Data JPA & Hibernate.
-Cơ sở dữ liệu: MySQL Server.
-Bảo mật: Spring Security (Xác thực và phân quyền).
-Thanh toán: Tích hợp cổng thanh toán VNPAY.
-Công cụ quản lý dự án: Maven.
-Công cụ kiểm thử API: Postman.


🛠 Các chức năng chính (Backend API)
Hệ thống cung cấp các API để phục vụ các nhóm chức năng chính:
-Hệ thống & Bảo mật: Xác thực người dùng (Auth), Đăng ký/Đăng nhập.
-Quản lý sản phẩm: Quản lý danh mục, thông tin sản phẩm và kho hàng.
-Giao dịch: Xử lý giỏ hàng, đặt hàng và tích hợp thanh toán VNPAY.
-Quản trị (Admin): Phê duyệt/Hủy đơn hàng.
-Quản lý thông tin người dùng.
-Thống kê doanh thu, sản phẩm bán chạy và tồn kho.



🛠Cài đặt và Chạy dự án
Yêu cầu hệ thống: 
-Cài đặt JDK 1.8 hoặc cao hơn.
-Cài đặt MySQL Server 8.0.
Cấu hình Cơ sở dữ liệu:
-Tạo một database mới trong MySQL.
-Cập nhật thông tin kết nối (username, password, url) trong file src/main/resources/application.properties.
Chạy ứng dụng:
-Sử dụng IDE (IntelliJ IDEA) để chạy file main của Spring Boot.
-Hoặc sử dụng terminal: mvn spring-boot:run.

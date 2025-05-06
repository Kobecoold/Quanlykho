# Hệ thống Quản lý Kho (Warehouse Management System)

## Yêu cầu hệ thống

- Java Development Kit (JDK) 17 hoặc cao hơn
- MySQL 8.0 hoặc cao hơn
- Maven 3.6 hoặc cao hơn

## Cài đặt

1. Clone repository:
```bash
git clone <repository-url>
cd quanlikho
```

2. Cấu hình database:
- Tạo database MySQL với tên "warehouse_db"
- Cập nhật thông tin kết nối database trong file `src/main/resources/application.properties`
- Chạy script khởi tạo database: `src/main/resources/db/init.sql`

3. Build project:
```bash
mvn clean install
```

4. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại địa chỉ: http://localhost:8080/api

## Tài khoản mặc định

- Username: admin
- Password: admin123

## Các chức năng chính

1. Quản lý người dùng
   - Đăng nhập/đăng xuất
   - Phân quyền: ADMIN, MANAGER, WAREHOUSE_STAFF
   - Quản lý thông tin người dùng

2. Quản lý sản phẩm
   - Thêm/sửa/xóa sản phẩm
   - Tìm kiếm sản phẩm
   - Theo dõi số lượng tồn kho

3. Quản lý kho
   - Nhập kho
   - Xuất kho
   - Kiểm kho
   - In phiếu nhập/xuất kho

4. Báo cáo thống kê
   - Báo cáo tồn kho
   - Báo cáo nhập/xuất kho
   - Thống kê theo thời gian

## Cấu trúc project

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── quanlikho/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── model/
│   │               ├── repository/
│   │               ├── service/
│   │               └── security/
│   └── resources/
│       ├── static/
│       ├── templates/
│       └── application.properties
``` 
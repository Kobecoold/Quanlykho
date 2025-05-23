package com.example.quanlikho.console;

import com.example.quanlikho.model.User;
import com.example.quanlikho.model.Role;
import com.example.quanlikho.model.Product;
import com.example.quanlikho.model.Warehouse;
import com.example.quanlikho.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import com.example.quanlikho.QuanlikhoApplication;
import com.example.quanlikho.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class Main {
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ===== ANSI COLOR CODES =====
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public static void main(String[] args) {
        // Khởi tạo Spring context, nạp toàn bộ bean đã viết
        ApplicationContext context = SpringApplication.run(QuanlikhoApplication.class, args);

        // Lấy các service đã viết
        UserService userService = context.getBean(UserService.class);
        ProductService productService = context.getBean(ProductService.class);
        InventoryTransactionService transactionService = context.getBean(InventoryTransactionService.class);
        RevenueService revenueService = context.getBean(RevenueService.class);
        WarehouseService warehouseService = context.getBean(WarehouseService.class);
        RoleRepository roleRepository = context.getBean(RoleRepository.class);

        // Tạo tài khoản ADMIN mặc định nếu chưa tồn tại
        try {
            Optional<User> adminUserOpt = userService.findByUsername("admin");
            if (adminUserOpt.isEmpty()) {
                System.out.println("\n" + ANSI_YELLOW + "Đang tạo tài khoản ADMIN mặc định..." + ANSI_RESET);
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin")); // Mã hóa mật khẩu
                adminUser.setFullName("Administrator");
                adminUser.setEmail("admin@example.com");
                adminUser.setPhone("0123456789");
                adminUser.setActive(true);

                // Tìm và gán role ADMIN
                Role adminRole = roleRepository.findByName("ADMIN");
                if (adminRole != null) {
                    adminUser.setRoles(new HashSet<>(Collections.singletonList(adminRole)));
                    userService.createUser(adminUser);
                    System.out.println(ANSI_GREEN + "✅ Tài khoản ADMIN mặc định (admin/admin) đã được tạo." + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "❌ Role 'ADMIN' không tồn tại. Không thể tạo tài khoản ADMIN mặc định." + ANSI_RESET);
                }
            }
        } catch (Exception e) {
            System.out.println(ANSI_RED + "❌ Lỗi khi tạo tài khoản ADMIN mặc định: " + e.getMessage() + ANSI_RESET);
        }

        // Vòng lặp menu chính
        while (true) {
            if (currentUser == null) {
                showLoginMenu(userService, roleRepository);
            } else {
                showMainMenu(userService, productService, transactionService, revenueService, warehouseService, roleRepository);
            }
        }
    }

    // ===== MENU ĐĂNG NHẬP (CÓ ĐĂNG KÝ) =====
    private static void showLoginMenu(UserService userService, RoleRepository roleRepository) {
        System.out.println("==== ĐĂNG NHẬP ====");
        System.out.println("1. Đăng nhập");
        System.out.println("2. Đăng ký tài khoản mới");
        System.out.print("Chọn chức năng: ");
        Integer choice = inputInt("");
        if (choice == null || choice == 0) return;
        if (choice == 1) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
                currentUser = userOpt.get();
                System.out.println("Đăng nhập thành công!");
            } else {
                System.out.println("Sai thông tin đăng nhập!");
            }
        } else if (choice == 2) {
            // Đăng ký tài khoản mới
            String username = inputString("Username");
            if (username == null) return;
            if (userService.findByUsername(username).isPresent()) {
                System.out.println("Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!");
                return;
            }
            String password = inputString("Password"); if (password == null) return;
            String fullName = inputString("Họ tên"); if (fullName == null) return;
            String email = inputString("Email"); if (email == null) return;
            String phone = inputString("Số điện thoại"); if (phone == null) return;
            boolean active = inputBoolean("Active (true/false)");
            String roleName;
            while (true) {
                roleName = inputString("Role (MANAGER/WAREHOUSE_STAFF)");
                if (roleName == null) return;
                roleName = roleName.toUpperCase();
                if (roleName.equals("ADMIN")) {
                    System.out.println("Không thể tạo tài khoản ADMIN mới!");
                } else if (!roleName.equals("MANAGER") && !roleName.equals("WAREHOUSE_STAFF")) {
                    System.out.println("Chỉ được chọn MANAGER hoặc WAREHOUSE_STAFF!");
                } else {
                    break;
                }
            }
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                System.out.println("Role không tồn tại!");
                return;
            }
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setActive(active);
            newUser.setRoles(new HashSet<>(Collections.singletonList(role)));
            try {
                userService.createUser(newUser);
                System.out.println("Tạo tài khoản thành công! Bạn có thể đăng nhập ngay.");
            } catch (Exception e) {
                System.out.println("Lỗi khi tạo tài khoản: " + e.getMessage());
            }
        }
    }

    // ===== MENU CHÍNH =====
    private static void showMainMenu(UserService userService, ProductService productService,
                                     InventoryTransactionService transactionService,
                                     RevenueService revenueService, WarehouseService warehouseService, RoleRepository roleRepository) {
        System.out.println("\n==== MENU CHÍNH ====");
        System.out.println("1. Quản lý tài khoản");
        System.out.println("2. Quản lý sản phẩm");
        System.out.println("3. Nhập kho / Xuất kho");
        System.out.println("4. Thống kê doanh thu");
        System.out.println("5. Quản lý kho");
        System.out.println("9. Đăng xuất");
        System.out.println("0. Thoát");
        System.out.print("Chọn chức năng: ");
        Integer choice = inputInt("");
        if (choice == null || choice == 0) return;
        switch (choice) {
            case 1:
                showAccountMenu(userService, roleRepository);
                break;
            case 2:
                showProductMenu(productService, warehouseService);
                break;
            case 3:
                showTransactionMenu(transactionService, productService, warehouseService);
                break;
            case 4:
                showRevenueMenu(revenueService);
                break;
            case 5:
                showWarehouseMenu(warehouseService);
                break;
            case 6:
                currentUser = null;
                System.out.println("Đã đăng xuất!");
                break;
            case 0:
                System.exit(0);
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }

    // ===== MENU QUẢN LÝ TÀI KHOẢN =====
    private static void showAccountMenu(UserService userService, RoleRepository roleRepository) {
        while (true) {
            System.out.println("\n==== QUẢN LÝ TÀI KHOẢN ====");
            System.out.println("1. Thêm tài khoản");
            System.out.println("2. Xem danh sách tài khoản");
            System.out.println("3. Xóa tài khoản");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");
            Integer choice = inputInt("");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1:
                    try {
                        String username = inputString("Username");
                        if (username == null) break;
                        if (userService.findByUsername(username).isPresent()) {
                            System.out.println("Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!");
                            break;
                        }
                        String password = inputString("Password"); if (password == null) break;
                        String fullName = inputString("Họ tên"); if (fullName == null) break;
                        String email = inputString("Email"); if (email == null) break;
                        String phone = inputString("Số điện thoại"); if (phone == null) break;
                        boolean active = inputBoolean("Active (true/false)");
                        String roleName;
                        while (true) {
                            roleName = inputString("Role (MANAGER/WAREHOUSE_STAFF)");
                            if (roleName == null) break;
                            roleName = roleName.toUpperCase();
                            if (roleName.equals("ADMIN")) {
                                System.out.println("Không thể tạo tài khoản ADMIN mới!");
                            } else if (!roleName.equals("MANAGER") && !roleName.equals("WAREHOUSE_STAFF")) {
                                System.out.println("Chỉ được chọn MANAGER hoặc WAREHOUSE_STAFF!");
                            } else {
                                break;
                            }
                        }
                        Role role = roleRepository.findByName(roleName);
                        if (role == null) {
                            System.out.println("Role không tồn tại!");
                            break;
                        }
                        User newUser = new User();
                        newUser.setUsername(username);
                        newUser.setPassword(passwordEncoder.encode(password));
                        newUser.setFullName(fullName);
                        newUser.setEmail(email);
                        newUser.setPhone(phone);
                        newUser.setActive(active);
                        newUser.setRoles(new HashSet<>(Collections.singletonList(role)));
                        userService.createUser(newUser);
                        System.out.println("Tạo tài khoản thành công!");
                    } catch (Exception e) {
                        System.out.println("Lỗi khi tạo tài khoản: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        List<User> users = userService.getAllUsers();
                        System.out.println("Danh sách tài khoản:");
                        for (User u : users) {
                            String adminMark = u.getUsername().equalsIgnoreCase("admin") ? " (ADMIN - không thể xóa/sửa)" : "";
                            System.out.println("ID: " + u.getId() + ", Username: " + u.getUsername() + ", Họ tên: " + u.getFullName() + ", Email: " + u.getEmail() + ", Phone: " + u.getPhone() + ", Active: " + u.isActive() + adminMark);
                        }
                    } catch (Exception e) {
                        System.out.println("Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        System.out.print("Nhập ID tài khoản cần xóa: ");
                        Long delId = Long.parseLong(scanner.nextLine());
                        Optional<User> delUser = userService.getUserById(delId);
                        if (delUser.isPresent() && delUser.get().getUsername().equalsIgnoreCase("admin")) {
                            System.out.println("Không thể xóa tài khoản ADMIN!");
                            break;
                        }
                        userService.deleteUser(delId);
                        System.out.println("Đã xóa tài khoản!");
                    } catch (Exception e) {
                        System.out.println("Lỗi khi xóa tài khoản: " + e.getMessage());
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    // ===== MENU QUẢN LÝ SẢN PHẨM =====
    private static void showProductMenu(ProductService productService, WarehouseService warehouseService) {
        while (true) {
            System.out.println(ANSI_CYAN + "┌───────────────────────────────┐" + ANSI_RESET);
            System.out.println(ANSI_BOLD + "│        QUẢN LÝ SẢN PHẨM       │" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "└───────────────────────────────┘" + ANSI_RESET);
            System.out.println(" 1. Thêm sản phẩm");
            System.out.println(" 2. Sửa sản phẩm");
            System.out.println(" 3. Xóa sản phẩm");
            System.out.println(" 4. Xem danh sách sản phẩm");
            System.out.println(" 0. Quay lại");
            System.out.print(ANSI_YELLOW + "Chọn chức năng: " + ANSI_RESET);
            Integer choice = inputInt("");
            if (choice == null || choice == 0) return;
            switch (choice) {
                case 1:
                    do {
                        try {
                            Product p = new Product();
                            String name = inputString("Tên sản phẩm : "); if (name == null) break;
                            p.setName(name);
                            String code = inputString("Mã sản phẩm : "); if (code == null) break;
                            p.setCode(code);
                            String sku = inputString("SKU :"); if (sku == null) break;
                            p.setSku(sku);
                            BigDecimal price = inputBigDecimal("Giá :"); if (price == null) break;
                            p.setPrice(price);
                            Integer qty = inputInt("Số lượng :"); if (qty == null) break;
                            p.setQuantity(qty);
                            String desc = inputString("Mô tả :"); if (desc == null) break;
                            p.setDescription(desc);
                            String unit = inputString("Đơn vị tính :"); if (unit == null) break;
                            p.setUnit(unit);
                            Integer minQty = inputInt("Số lượng tối thiểu :"); if (minQty == null) break;
                            p.setMinQuantity(minQty);
                            p.setActive(true);

                            // Thêm phần chọn kho
                            System.out.println("Chọn kho cho sản phẩm:");
                            List<Warehouse> warehouses = warehouseService.getAllWarehouses();
                            if (warehouses.isEmpty()) {
                                System.out.println(ANSI_RED + "❌ Chưa có kho nào được tạo. Vui lòng tạo kho trước khi thêm sản phẩm." + ANSI_RESET);
                                break;
                            }
                            for (int i = 0; i < warehouses.size(); i++) {
                                System.out.println((i + 1) + ". " + warehouses.get(i).getName());
                            }
                            Integer warehouseChoice = inputInt("Chọn số thứ tự kho");
                            if (warehouseChoice == null || warehouseChoice <= 0 || warehouseChoice > warehouses.size()) {
                                System.out.println(ANSI_RED + "❌ Lựa chọn kho không hợp lệ." + ANSI_RESET);
                                break;
                            }
                            Warehouse selectedWarehouse = warehouses.get(warehouseChoice - 1);
                            p.setWarehouse(selectedWarehouse);

                            productService.createProduct(p);
                            System.out.println(ANSI_GREEN + "✅ Thêm sản phẩm thành công!" + ANSI_RESET);
                        } catch (Exception e) {
                            System.out.println(ANSI_RED + "❌ Lỗi khi thêm sản phẩm: " + e.getMessage() + ANSI_RESET);
                        }
                    } while (askContinue("thêm sản phẩm :"));
                    break;
                case 2:
                    try {
                        System.out.print("Nhập ID sản phẩm cần sửa: ");
                        Long editId = Long.parseLong(scanner.nextLine());
                        Optional<Product> editOpt = productService.getProductById(editId);
                        if (editOpt.isEmpty()) {
                            System.out.println("Không tìm thấy sản phẩm!");
                            break;
                        }
                        Product editP = editOpt.get();
                        System.out.print("Tên mới: ");
                        editP.setName(scanner.nextLine());
                        System.out.print("Mã sản phẩm mới : ");
                        editP.setCode(scanner.nextLine());
                        System.out.print("SKU mới: ");
                        editP.setSku(scanner.nextLine());
                        System.out.print("Giá mới: ");
                        editP.setPrice(inputBigDecimal("Giá: "));
                        System.out.print("Số lượng mới: ");
                        editP.setQuantity(inputInt("Số lượng: "));
                        System.out.print("Mô tả mới: ");
                        editP.setDescription(inputString("Mô tả: "));
                        System.out.print("Đơn vị tính mới: ");
                        editP.setUnit(inputString("Đơn vị tính: "));
                        System.out.print("Số lượng tối thiểu mới: ");
                        editP.setMinQuantity(inputInt("Số lượng tối thiểu: "));
                        productService.updateProduct(editId, editP);
                        System.out.println("Cập nhật sản phẩm thành công!");
                    } catch (Exception e) {
                        System.out.println("Lỗi khi sửa sản phẩm: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        System.out.print("Nhập ID sản phẩm cần xóa: ");
                        Long delId = Long.parseLong(scanner.nextLine());
                        productService.deleteProduct(delId);
                        System.out.println("Đã xóa sản phẩm!");
                    } catch (Exception e) {
                        System.out.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
                    }
                    break;
                case 4:
                    try {
                        List<Product> products = productService.getAllProducts();
                        System.out.println(ANSI_CYAN + "───────────────────────────────────────────────" + ANSI_RESET);
                        System.out.println(ANSI_BOLD + "Danh sách sản phẩm:" + ANSI_RESET);
                        for (Product prod : products) {
                            System.out.println("ID: " + prod.getId() + ", Tên: " + prod.getName() + ", Code: " + prod.getCode() + ", SKU: " + prod.getSku() + ", Giá: " + prod.getPrice() + ", SL: " + prod.getQuantity());
                        }
                        System.out.println(ANSI_CYAN + "───────────────────────────────────────────────" + ANSI_RESET);
                    } catch (Exception e) {
                        System.out.println(ANSI_RED + "Lỗi khi lấy danh sách sản phẩm: " + e.getMessage() + ANSI_RESET);
                    }
                    break;
                default:
                    System.out.println(ANSI_YELLOW + "Lựa chọn không hợp lệ!" + ANSI_RESET);
            }
        }
    }

    // ===== MENU NHẬP/XUẤT KHO =====
    private static void showTransactionMenu(InventoryTransactionService transactionService, ProductService productService, WarehouseService warehouseService) {
        System.out.println("\n==== NHẬP/XUẤT KHO ====");
        System.out.println("1. Nhập kho");
        System.out.println("2. Xuất kho");
        System.out.println("3. Xem lịch sử giao dịch");
        System.out.println("0. Quay lại");
        System.out.print("Chọn chức năng: ");
        Integer choice = inputInt("");
        if (choice == null || choice == 0) return;
        switch (choice) {
            case 1:
                try {
                    System.out.print("ID sản phẩm: ");
                    Long prodId = Long.parseLong(scanner.nextLine());
                    Optional<Product> prodOpt = productService.getProductById(prodId);
                    if (prodOpt.isEmpty()) {
                        System.out.println("Không tìm thấy sản phẩm!");
                        break;
                    }
                    Product prod = prodOpt.get();
                    System.out.print("Số lượng nhập: ");
                    int qty = Integer.parseInt(scanner.nextLine());
                    com.example.quanlikho.model.InventoryTransaction importTrans = new com.example.quanlikho.model.InventoryTransaction();
                    importTrans.setProduct(prod);
                    importTrans.setTransactionType("IMPORT");
                    importTrans.setQuantity(qty);
                    importTrans.setCreatedBy(currentUser);
                    importTrans.setTransactionDate(java.time.LocalDateTime.now());
                    importTrans.setDocumentNumber(UUID.randomUUID().toString());

                    // Thêm phần chọn kho
                    System.out.println("Chọn kho cho phiếu nhập:");
                    List<Warehouse> warehouses = warehouseService.getAllWarehouses();
                    if (warehouses.isEmpty()) {
                        System.out.println(ANSI_RED + "❌ Chưa có kho nào được tạo. Vui lòng tạo kho trước khi nhập kho." + ANSI_RESET);
                        break;
                    }
                    for (int i = 0; i < warehouses.size(); i++) {
                        System.out.println((i + 1) + ". " + warehouses.get(i).getName());
                    }
                    Integer warehouseChoice = inputInt("Chọn số thứ tự kho");
                    if (warehouseChoice == null || warehouseChoice <= 0 || warehouseChoice > warehouses.size()) {
                        System.out.println(ANSI_RED + "❌ Lựa chọn kho không hợp lệ." + ANSI_RESET);
                        break;
                    }
                    Warehouse selectedWarehouse = warehouses.get(warehouseChoice - 1);
                    importTrans.setWarehouse(selectedWarehouse);

                    transactionService.createTransaction(importTrans);
                    System.out.println("Nhập kho thành công!");
                } catch (Exception e) {
                    System.out.println("Lỗi khi nhập kho: " + e.getMessage());
                }
                break;
            case 2:
                try {
                    System.out.print("ID sản phẩm: ");
                    Long prodId2 = Long.parseLong(scanner.nextLine());
                    Optional<Product> prodOpt2 = productService.getProductById(prodId2);
                    if (prodOpt2.isEmpty()) {
                        System.out.println("Không tìm thấy sản phẩm!");
                        break;
                    }
                    Product prod2 = prodOpt2.get();
                    System.out.print("Số lượng xuất: ");
                    int qty2 = Integer.parseInt(scanner.nextLine());
                    com.example.quanlikho.model.InventoryTransaction exportTrans = new com.example.quanlikho.model.InventoryTransaction();
                    exportTrans.setProduct(prod2);
                    exportTrans.setTransactionType("EXPORT");
                    exportTrans.setQuantity(qty2);
                    exportTrans.setCreatedBy(currentUser);
                    exportTrans.setTransactionDate(java.time.LocalDateTime.now());
                    exportTrans.setDocumentNumber(UUID.randomUUID().toString());

                    // Thêm phần chọn kho
                    System.out.println("Chọn kho cho phiếu xuất:");
                    List<Warehouse> warehouses = warehouseService.getAllWarehouses();
                    if (warehouses.isEmpty()) {
                        System.out.println(ANSI_RED + "❌ Chưa có kho nào được tạo. Vui lòng tạo kho trước khi xuất kho." + ANSI_RESET);
                        break;
                    }
                    for (int i = 0; i < warehouses.size(); i++) {
                        System.out.println((i + 1) + ". " + warehouses.get(i).getName());
                    }
                    Integer warehouseChoice = inputInt("Chọn số thứ tự kho");
                    if (warehouseChoice == null || warehouseChoice <= 0 || warehouseChoice > warehouses.size()) {
                        System.out.println(ANSI_RED + "❌ Lựa chọn kho không hợp lệ." + ANSI_RESET);
                        break;
                    }
                    Warehouse selectedWarehouse = warehouses.get(warehouseChoice - 1);
                    exportTrans.setWarehouse(selectedWarehouse);

                    transactionService.createTransaction(exportTrans);
                    System.out.println("Xuất kho thành công!");
                } catch (Exception e) {
                    System.out.println("Lỗi khi xuất kho: " + e.getMessage());
                }
                break;
            case 3:
                try {
                    List<com.example.quanlikho.model.InventoryTransaction> trans = transactionService.getAllTransactions();
                    System.out.println("Lịch sử giao dịch:");
                    for (com.example.quanlikho.model.InventoryTransaction t : trans) {
                        System.out.println("ID: " + t.getId() + ", Sản phẩm: " + t.getProduct().getName() + ", Loại: " + t.getTransactionType() + ", SL: " + t.getQuantity() + ", Ngày: " + t.getTransactionDate());
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi khi lấy lịch sử giao dịch: " + e.getMessage());
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }

    // ===== MENU THỐNG KÊ DOANH THU =====
    private static void showRevenueMenu(RevenueService revenueService) {
        System.out.println("\n==== THỐNG KÊ DOANH THU ====");
        System.out.println("1. Doanh thu ngày");
        System.out.println("2. Doanh thu tháng");
        System.out.println("3. Doanh thu năm");
        System.out.println("0. Quay lại");
        System.out.print("Chọn chức năng: ");
        Integer choice = inputInt("");
        if (choice == null || choice == 0) return;
        switch (choice) {
            case 1:
                // Nhập ngày, gọi revenueService.getDailyRevenue(...)
                System.out.print("Nhập ngày (yyyy-MM-dd): ");
                LocalDate date = LocalDate.parse(scanner.nextLine());
                BigDecimal daily = revenueService.getDailyRevenue(date);
                System.out.println("Doanh thu ngày: " + daily);
                break;
            case 2:
                // Nhập tháng/năm, gọi revenueService.getMonthlyRevenue(...)
                System.out.print("Nhập năm: ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Nhập tháng: ");
                int month = Integer.parseInt(scanner.nextLine());
                BigDecimal monthly = revenueService.getMonthlyRevenue(year, month);
                System.out.println("Doanh thu tháng: " + monthly);
                break;
            case 3:
                // Nhập năm, gọi revenueService.getYearlyRevenue(...)
                System.out.print("Nhập năm: ");
                int y = Integer.parseInt(scanner.nextLine());
                BigDecimal yearly = revenueService.getYearlyRevenue(y);
                System.out.println("Doanh thu năm: " + yearly);
                break;
            case 0:
                return;
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }

    // ===== MENU QUẢN LÝ KHO =====
    private static void showWarehouseMenu(WarehouseService warehouseService) {
        System.out.println("\n==== QUẢN LÝ KHO ====");
        System.out.println("1. Thêm kho");
        System.out.println("2. Sửa kho");
        System.out.println("3. Xóa kho");
        System.out.println("4. Xem danh sách kho");
        System.out.println("0. Quay lại");
        System.out.print("Chọn chức năng: ");
        Integer choice = inputInt("");
        if (choice == null || choice == 0) return;
        switch (choice) {
            case 1:
                try {
                    Warehouse w = new Warehouse();
                    System.out.print("Tên kho: ");
                    w.setName(scanner.nextLine());
                    System.out.print("Vị trí: ");
                    w.setLocation(scanner.nextLine());
                    System.out.print("Mô tả: ");
                    w.setDescription(scanner.nextLine());
                    System.out.print("Sức chứa: ");
                    w.setCapacity(Integer.parseInt(scanner.nextLine()));
                    warehouseService.createWarehouse(w);
                    System.out.println("Thêm kho thành công!");
                } catch (Exception e) {
                    System.out.println("Lỗi khi thêm kho: " + e.getMessage());
                }
                break;
            case 2:
                try {
                    System.out.print("Nhập ID kho cần sửa: ");
                    Long editId = Long.parseLong(scanner.nextLine());
                    Optional<Warehouse> editOpt = warehouseService.getWarehouseById(editId);
                    if (editOpt.isEmpty()) {
                        System.out.println("Không tìm thấy kho!");
                        break;
                    }
                    Warehouse editW = editOpt.get();
                    System.out.print("Tên mới: ");
                    editW.setName(scanner.nextLine());
                    System.out.print("Vị trí mới: ");
                    editW.setLocation(scanner.nextLine());
                    System.out.print("Mô tả mới: ");
                    editW.setDescription(scanner.nextLine());
                    System.out.print("Sức chứa mới: ");
                    editW.setCapacity(Integer.parseInt(scanner.nextLine()));
                    warehouseService.updateWarehouse(editId, editW);
                    System.out.println("Cập nhật kho thành công!");
                } catch (Exception e) {
                    System.out.println("Lỗi khi sửa kho: " + e.getMessage());
                }
                break;
            case 3:
                try {
                    System.out.print("Nhập ID kho cần xóa: ");
                    Long delId = Long.parseLong(scanner.nextLine());
                    warehouseService.deleteWarehouse(delId);
                    System.out.println("Đã xóa kho!");
                } catch (Exception e) {
                    System.out.println("Lỗi khi xóa kho: " + e.getMessage());
                }
                break;
            case 4:
                try {
                    List<Warehouse> warehouses = warehouseService.getAllWarehouses();
                    System.out.println("Danh sách kho:");
                    for (Warehouse wh : warehouses) {
                        System.out.println("ID: " + wh.getId() + ", Tên: " + wh.getName() + ", Vị trí: " + wh.getLocation() + ", Sức chứa: " + wh.getCapacity());
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi khi lấy danh sách kho: " + e.getMessage());
                }
                break;
            case 0:
                return;
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }

    // ===== HÀM NHẬP AN TOÀN =====
    private static Integer inputInt(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("r")) return null;
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Vui lòng nhập một số nguyên hợp lệ!");
            }
        }
    }

    private static BigDecimal inputBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("r")) return null;
            try {
                return new BigDecimal(input);
            } catch (Exception e) {
                System.out.println("Vui lòng nhập một số hợp lệ!");
            }
        }
    }

    private static String inputString(String prompt) {
        System.out.print(prompt + " ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("r")) return null;
        return input;
    }

    private static boolean inputBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("r")) throw new RuntimeException("Quay lại");
            if (input.equalsIgnoreCase("true")) return true;
            if (input.equalsIgnoreCase("false")) return false;
            System.out.println("Vui lòng nhập 'true' hoặc 'false'!");
        }
    }

    // ===== HÀM HỎI TIẾP TỤC =====
    private static boolean askContinue(String action) {
        System.out.print("Bạn có muốn " + action + " nữa không? (y/n): ");
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("y");
    }
} 
package SemesterProject;

import SemesterProject.User;
import SemesterProject.Login.Admin;
import SemesterProject.Login.Staff;
import SemesterProject.Login.UserRoles;
import SemesterProject.Part;
import SemesterProject.Sales.Sale;
import SemesterProject.Body.FrontLaminatedGlass;
import SemesterProject.Body.FrontGlass;
import SemesterProject.Body.RearGlass;
import SemesterProject.Body.DoorGlass;
import SemesterProject.Body.FrontBumper;
import SemesterProject.Body.RearBumper;
import SemesterProject.Supplier.Supplier;
import SemesterProject.Supplier.LocalSupplier;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/ims_project?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "your_mysql_password";

    // Default supplier for DB loaded parts
    private static final Supplier DEFAULT_SUPPLIER = new LocalSupplier("DB_LOADED", "N/A", "N/A", "N/A", "N/A");

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver Loaded.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Ensure the JAR is in the classpath.");
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // =================================================================
    // 1. USER MANAGEMENT (CRUD)
    // =================================================================

    public User findUserByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, full_name, contact_number, role, last_login FROM Users WHERE username = ?";
        User user = null;
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("user_id");
                    String password = rs.getString("password_hash");
                    String fullName = rs.getString("full_name");
                    String contactNumber = rs.getString("contact_number");
                    UserRoles role = UserRoles.valueOf(rs.getString("role").toUpperCase());
                    if (role == UserRoles.ADMIN) {
                        user = new Admin(userId, username, password);
                    } else if (role == UserRoles.STAFF) {
                        user = new Staff(userId, username, password, fullName, contactNumber);
                    }
                }
            }
        } catch (SQLException e) { System.err.println("SQL Error finding user: " + e.getMessage()); }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash, full_name, contact_number, role FROM Users";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password_hash");
                String fullName = rs.getString("full_name");
                String contactNumber = rs.getString("contact_number");
                UserRoles role = UserRoles.valueOf(rs.getString("role").toUpperCase());
                if (role == UserRoles.ADMIN) users.add(new Admin(userId, username, password));
                else if (role == UserRoles.STAFF) users.add(new Staff(userId, username, password, fullName, contactNumber));
            }
        } catch (SQLException e) { System.err.println("SQL Error retrieving all users: " + e.getMessage()); }
        return users;
    }

    public void addUser(User newUser) throws Exception {
        List<User> existingUsers = getAllUsers();
        int nextId = 1;
        if (!existingUsers.isEmpty()) {
            for (User u : existingUsers) {
                if (u.getUsername().equalsIgnoreCase(newUser.getUsername())) throw new UserAlreadyExistsException("Username already exists: " + newUser.getUsername());
                try { int idNum = Integer.parseInt(u.getUserId().substring(1)); if (idNum >= nextId) nextId = idNum + 1; } catch (NumberFormatException ignored) {}
            }
        }
        String newUserId = String.format("U%02d", nextId);
        String sql = "INSERT INTO Users (user_id, username, password_hash, full_name, contact_number, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUserId);
            pstmt.setString(2, newUser.getUsername());
            pstmt.setString(3, newUser.getPassword());
            pstmt.setString(6, newUser.getRole().name());
            pstmt.executeUpdate();
            System.out.println("User added successfully: " + newUser.getUsername() + " (ID: " + newUserId + ")");
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) throw new UserAlreadyExistsException("Username already exists in DB: " + newUser.getUsername());
            throw new Exception("SQL Error adding user: " + e.getMessage());
        }
    }

    public boolean removeUser(String username) throws UserNotFoundException {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) throw new UserNotFoundException("User not found: " + username);
            System.out.println("User removed successfully: " + username);
            return true;
        } catch (SQLException e) { System.err.println("SQL Error removing user: " + e.getMessage()); return false; }
    }

    public boolean updateUserDetails(String oldUsername, String newUsername, String newFullName) throws UserAlreadyExistsException {
        String sql = "UPDATE Users SET username = ?, full_name = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, newFullName);
            pstmt.setString(3, oldUsername);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) throw new UserAlreadyExistsException("New username '" + newUsername + "' already exists.");
            System.err.println("SQL Error updating user details: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE Users SET password_hash = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("SQL Error updating password: " + e.getMessage()); return false; }
    }

    public boolean updateLastLogin(String username) {
        String sql = "UPDATE Users SET last_login = NOW() WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("SQL Error updating last login: " + e.getMessage()); return false; }
    }

    // =================================================================
    // 2. PARTS (INVENTORY CRUD)
    // =================================================================

    private Part createPartInstance(String partId, String name, String model, int stock, int threshold, double price, String type) {
        try {
            switch (type) {
                case "FrontLaminatedGlass":
                    return new FrontLaminatedGlass(partId, name, model, stock, threshold, price, DEFAULT_SUPPLIER);
                case "FrontGlass":
                    return new FrontGlass(partId, name, model, stock, threshold, price, DEFAULT_SUPPLIER);
                case "RearGlass":
                    return new RearGlass(partId, name, model, stock, threshold, price, DEFAULT_SUPPLIER);
                case "DoorGlass":
                    return new DoorGlass(partId, name, model, stock, threshold, price, DEFAULT_SUPPLIER);
                case "FrontBumper":
                    return new FrontBumper(partId, name, model, stock, threshold, price, DEFAULT_SUPPLIER);
                case "RearBumper":
                    return new RearBumper(partId, name, model, stock, threshold, price, DEFAULT_SUPPLIER);
                default:
                    System.out.println("Warning: Unknown part type found in DB: " + type);
                    throw new RuntimeException("Unknown part type: " + type);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate part type " + type + ": " + e.getMessage(), e);
        }
    }

    public List<Part> getAllParts() {
        List<Part> parts = new ArrayList<>();
        String sql = "SELECT part_id, part_name, car_model, current_stock, min_threshold, unit_price, part_type FROM Parts";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String partId = rs.getString("part_id");
                String name = rs.getString("part_name");
                String model = rs.getString("car_model");
                int stock = rs.getInt("current_stock");
                int threshold = rs.getInt("min_threshold");
                double price = rs.getDouble("unit_price");
                String type = rs.getString("part_type");
                try {
                    Part part = createPartInstance(partId, name, model, stock, threshold, price, type);
                    parts.add(part);
                } catch (RuntimeException e) { System.err.println(e.getMessage()); }
            }
        } catch (SQLException e) { System.err.println("SQL Error retrieving parts: " + e.getMessage()); }
        return parts;
    }

    public void addPart(Part part) throws SQLException {
        String baseId = part.getName().toUpperCase().replaceAll("[^A-Z0-9]", "");
        String partId = baseId.substring(0, Math.min(baseId.length(), 10)) + "_" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        part.setPartId(partId);

        String sql = "INSERT INTO Parts (part_id, part_name, car_model, current_stock, min_threshold, unit_price, part_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, part.getPartId());
            pstmt.setString(2, part.getName());
            pstmt.setString(3, part.getCarModel());
            pstmt.setInt(4, part.getCurrentStock());
            pstmt.setInt(5, part.getMinThreshold());
            pstmt.setDouble(6, part.getUnitPrice());
            pstmt.setString(7, part.getClass().getSimpleName());
            pstmt.executeUpdate();
            System.out.println("Part saved: " + part.getName());
        } catch (SQLException e) { throw new SQLException("Error adding part to DB: " + e.getMessage(), e); }
    }

    public void updatePart(Part part) throws SQLException {
        String sql = "UPDATE Parts SET current_stock = ?, min_threshold = ?, unit_price = ? WHERE part_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, part.getCurrentStock());
            pstmt.setInt(2, part.getMinThreshold());
            pstmt.setDouble(3, part.getUnitPrice());
            pstmt.setString(4, part.getPartId());
            pstmt.executeUpdate();
        }
    }

    public void clearAllPartsData() throws SQLException {
        String sqlSales = "DELETE FROM Sales";
        String sqlParts = "DELETE FROM Parts";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlSales);
            stmt.executeUpdate(sqlParts);
            System.out.println("Parts and Sales tables cleared successfully.");
        }
    }

    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT p.part_name, s.quantity_sold, s.total_amount, s.sale_date FROM Sales s JOIN Parts p ON s.part_id = p.part_id";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String partName = rs.getString("part_name");
                int quantitySold = rs.getInt("quantity_sold");
                double totalCost = rs.getDouble("total_amount");
                Timestamp saleDate = rs.getTimestamp("sale_date");
                Sale sale = new Sale(partName, quantitySold, totalCost, saleDate);
                sales.add(sale);
            }
        } catch (SQLException e) { System.err.println("SQL Error retrieving sales: " + e.getMessage()); }
        return sales;
    }

    public void addSale(Sale sale, String partId, String userId) throws SQLException {
        String sql = "INSERT INTO Sales (part_id, quantity_sold, total_amount, sale_date, user_id) VALUES (?, ?, ?, NOW(), ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, partId);
            pstmt.setInt(2, sale.getQuantitySold());
            pstmt.setDouble(3, sale.getCost());
            pstmt.setString(4, userId);
            pstmt.executeUpdate();
        }
    }
}
package SemesterProject;

import SemesterProject.Login.Admin;
import SemesterProject.Login.Staff;
import SemesterProject.Login.UserRoles;
import SemesterProject.Sales.Sale;
import SemesterProject.Body.*;
import SemesterProject.Supplier.Supplier;
import SemesterProject.Supplier.LocalSupplier;
import SemesterProject.Exception.UserAlreadyExistsException;
import SemesterProject.Exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    // IN-MEMORY STORAGE
    private List<User> users;
    private List<Part> parts;
    private List<Sale> sales;

    public DatabaseManager() {
        this.users = new ArrayList<>();
        this.parts = new ArrayList<>();
        this.sales = new ArrayList<>();

        System.out.println("Simple In-Memory Database Initialized.");
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        try {
            addUser(new Admin("U01", "admin", "123"));
            addUser(new Staff("U02", "staff", "123", "John Doe", "1111"));
        } catch (Exception e) {
            System.err.println("Error creating default users: " + e.getMessage());
        }
    }

    // --- USER MANAGEMENT ---

    public User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User newUser) throws Exception {
        if (findUserByUsername(newUser.getUsername()) != null) {
            throw new UserAlreadyExistsException("Username already exists: " + newUser.getUsername());
        }
        // Simple ID generation
        if (newUser.getUserId() == null) {
            // In a real app we'd use a setter, but for this simpler version
            // we accept the object as is or we could regenerate it if we had a setUserId method.
            // Since we are keeping it simple, we just add it.
        }
        users.add(newUser);
        System.out.println("User added: " + newUser.getUsername());
    }

    public boolean removeUser(String username) throws UserNotFoundException {
        User u = findUserByUsername(username);
        if (u != null) {
            users.remove(u);
            return true;
        }
        throw new UserNotFoundException("User not found: " + username);
    }

    public boolean updateUserDetails(String oldUsername, String newUsername, String newFullName) throws UserAlreadyExistsException {
        User u = findUserByUsername(oldUsername);
        if (u == null) return false;

        // Check if new username is taken
        User check = findUserByUsername(newUsername);
        if (check != null && !check.getUsername().equalsIgnoreCase(oldUsername)) {
            throw new UserAlreadyExistsException("Username taken.");
        }

        u.setUsername(newUsername);
        // Note: Full Name update skipped for simplicity as User class doesn't have setFullName
        return true;
    }

    public boolean updatePassword(String username, String newPassword) {
        User u = findUserByUsername(username);
        if (u != null) {
            u.setPassword(newPassword); // Uses the new setter we added to User.java
            return true;
        }
        return false;
    }

    public boolean updateLastLogin(String username) { return true; }

    // --- PARTS ---

    public List<Part> getAllParts() { return new ArrayList<>(parts); }

    public void addPart(Part part) {
        if (part.getPartId() == null) {
            part.setPartId("P" + (parts.size() + 1));
        }
        parts.add(part);
    }

    public void updatePart(Part part) {
        // Since it's in-memory, the object is likely already updated by reference.
        // We can just print a confirmation.
        System.out.println("Part updated: " + part.getName());
    }

    public void clearAllPartsData() {
        parts.clear();
        sales.clear();
    }

    // --- SALES ---

    public List<Sale> getAllSales() { return new ArrayList<>(sales); }

    public void addSale(Sale sale, String partId, String userId) {
        sales.add(sale);
    }
}
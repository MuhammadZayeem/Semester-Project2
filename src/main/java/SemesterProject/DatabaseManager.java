package SemesterProject;

import SemesterProject.Login.Admin;
import SemesterProject.Login.Staff;
import SemesterProject.Sales.Sale;
import SemesterProject.Body.*;
import SemesterProject.Exception.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // IN-MEMORY STORAGE
    private List<User> users;
    private List<Part> parts;
    private List<Sale> sales;

    public DatabaseManager() throws UserCreationException {
        this.users = new ArrayList<>();
        this.parts = new ArrayList<>();
        this.sales = new ArrayList<>();
        initializeDefaultUsers();

    }

    private void initializeDefaultUsers() throws UserCreationException {
        try {
            addUser(new Admin("U01", "admin", "123"));
            addUser(new Staff("U02", "staff", "123"));
        } catch (Exception e) {
            throw new UserCreationException("System error: " + e.getMessage());
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

    // UPDATED: Throws UserAlreadyExistsException
    public void addUser(User newUser) throws UserAlreadyExistsException {
        if (findUserByUsername(newUser.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists in system: " + newUser.getUsername());
        }

        if (newUser.getUserId() == null) {
        }
        users.add(newUser);
        System.out.println("User added: " + newUser.getUsername());
    }

    // UPDATED: Throws UserNotFoundException
    public boolean removeUser(String username) throws UserNotFoundException {
        User u = findUserByUsername(username);
        if (u != null) {
            users.remove(u);
            return true;
        }
        throw new UserNotFoundException("Cannot remove. User not found: " + username);
    }

    // UPDATED: Throws UserAlreadyExistsException
    public boolean updateUserDetails(String oldUsername, String newUsername, String newFullName) throws UserAlreadyExistsException {
        User u = findUserByUsername(oldUsername);
        if (u == null) return false;

        // Check if new username is taken
        User check = findUserByUsername(newUsername);
        if (check != null && !check.getUsername().equalsIgnoreCase(oldUsername)) {
            throw new UserAlreadyExistsException("New username '" + newUsername + "' is already taken.");
        }

        u.setUsername(newUsername);
        return true;
    }

    public boolean updatePassword(String username, String newPassword) {
        User u = findUserByUsername(username);
        if (u != null) {
            u.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public boolean updateLastLogin(String username) { return true; }

    // --- PARTS ---

    public List<Part> getAllParts() { return new ArrayList<>(parts); }

    // UPDATED: Throws DuplicatePartException
    public void addPart(Part part) throws DuplicatePartException {
        // Check for duplicate part name
        for (Part p : parts) {
            if (p.getName().equalsIgnoreCase(part.getName())) {
                throw new DuplicatePartException("Part '" + part.getName() + "' already exists in inventory!");
            }
        }

        if (part.getPartId() == null) {
            part.setPartId("P" + (parts.size() + 1));
        }
        parts.add(part);
    }

    public void updatePart(Part part) {
        // In-memory update is implicit via reference
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
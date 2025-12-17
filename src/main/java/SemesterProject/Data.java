package SemesterProject;

import SemesterProject.Login.Admin;
import SemesterProject.Login.Staff;
import SemesterProject.Sales.Sale;
import SemesterProject.Exception.*;
import java.util.ArrayList;
import java.util.List;

public class Data {

    private List<User> users;
    private List<Part> parts;
    private List<Sale> sales;

    public Data() throws UserCreationException {
        this.users = new ArrayList<>();
        this.parts = new ArrayList<>();
        this.sales = new ArrayList<>();
        defaultUsers();

    }

    private void defaultUsers() throws UserCreationException {
        try {
            addUser(new Admin("U01", "admin", "123"));
            addUser(new Staff("U02", "staff", "123"));
        } catch (Exception e) {
            throw new UserCreationException("System error: " + e.getMessage());
        }
    }

    // -------------------------------------------------USER MANAGEMENT
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
    public void addUser(User newUser) throws UserAlreadyExistsException {
        if (findUserByUsername(newUser.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists in system: " + newUser.getUsername());
        }
        if (newUser.getUserId() == null) {
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
        throw new UserNotFoundException("Cannot remove. User not found: " + username);
    }

  /*  public boolean updateUserDetails(String oldUsername, String newUsername, String newFullName) throws UserAlreadyExistsException {
        User u = findUserByUsername(oldUsername);
        if (u == null) return false;
        User check = findUserByUsername(newUsername);
        if (check != null && !check.getUsername().equalsIgnoreCase(oldUsername)) {
            throw new UserAlreadyExistsException("New username '" + newUsername + "' is already taken.");
        }
        u.setUsername(newUsername);
        return true;
    }
*/
    public boolean updatePassword(String username, String newPassword) {
        User u = findUserByUsername(username);
        if (u != null) {
            u.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public boolean updateLastLogin(String username) { return true; }

    // -----------------------------------------------------------------PARTS

    public List<Part> getAllParts() { return new ArrayList<>(parts); }

    public void addPart(Part part) throws DuplicatePartException {
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

    // -----------------------------------------------------------SALES
    public List<Sale> getAllSales() {
        return new ArrayList<>(sales);
    }

    public void addSale(Sale sale) {

        sales.add(sale);
    }
}
package SemesterProject;
import SemesterProject.Login.UserRoles;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class User {

    private final String UserId;
    private String username;
    private String HashPassword;      // Safe practice: store hash, not raw password
    private String fullName;
    private String contactNumber;
    private final UserRoles role;
    private LocalDateTime lastLoginTime;
    private boolean resetApproved = false;

    public User(String UserId,String username,String SimplePassword,String fullName,String contactNumber,UserRoles role)
    {
        this.UserId=Objects.requireNonNull(UserId,"User ID cannot be null");
        this.username=Objects.requireNonNull(username,"Username cannot be null");
        this.fullName=Objects.requireNonNull(fullName,"Full name cannot be null");
        this.contactNumber=Objects.requireNonNull(contactNumber,"Contact number cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.HashPassword=EncryptPassword(SimplePassword);
    }
    protected String EncryptPassword(String Password) {
        return "hash-"+Password.hashCode();
    }
    public boolean PasswordValidation(String inputpassword) {
        return Objects.equals(HashPassword,EncryptPassword(inputpassword));
    }

    public void updatePassword(String newPassword) {
        this.HashPassword=EncryptPassword(newPassword);
    }

    public void updateContactNumber(String NewContact) {
        this.contactNumber = NewContact;
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setLastLogin() {
        this.lastLoginTime = LocalDateTime.now();
    }


    public abstract String[] getAllowedActions();

    public abstract void displayDashboardGreeting();

    public String getUserId() {
        return UserId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
    public UserRoles getRole() {
        return role;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    public String ShowLastLogin(){
        if(lastLoginTime!=null){
            return lastLoginTime.toString(); }
        else{
            return "Never Logged In";}
    }
    public boolean isResetApproved() {
        return resetApproved;
    }

    public void setResetApproved(boolean resetApproved) {
        this.resetApproved = resetApproved;
    }


    public void displayProfile() {
        System.out.println("\n================== USER PROFILE ==================");
        System.out.println("User ID    : "+UserId);
        System.out.println("Name       : "+fullName);
        System.out.println("Username   : "+username);
        System.out.println("Contact    : "+contactNumber);
        //System.out.println("Role       : "+//role);
        System.out.println("Last Login : "+ShowLastLogin());
        System.out.println("\n===================================================\n");
    }
}
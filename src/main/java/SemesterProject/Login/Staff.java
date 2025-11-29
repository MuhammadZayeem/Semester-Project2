package SemesterProject.Login;
import SemesterProject.User;

public class Staff extends User {

    public Staff(String userId,String username,String password,String fullName,String contactNumber) {
        super(userId, username, password, fullName, contactNumber,UserRoles.STAFF);
    }

    // ---------------- Abstract Method Implementation
    @Override
    public String[] getAllowedActions() {
        return new String[]{
                "View Inventory",
                "Update Part Usage",
                "View Demand List",
                "Generate Daily Report"
        };
    }

    @Override
    public void displayDashboardGreeting() {
        System.out.println("===========================================");
        System.out.println("Welcome Staff: " +getFullName());
        System.out.println("Last Login : " +ShowLastLogin());
        System.out.println("You have limited access to the system.");
        System.out.println("===========================================\n");
    }

    // ---------------- Staff Specific Functionalities

    // Update part usage in inventory
    public void markPartUsage(String PartName, int QuantityUsed) {
        System.out.println("Marked usage of part: " +PartName+ " | Quantity: "+QuantityUsed);
    }

    // View part usage report (daily)
    public void viewDailyUsageReport() {
        System.out.println("Generating today's usage report...");
    }

    // Staff can view their own profile
    public void viewOwnProfile() {
        System.out.println("\n================ STAFF PROFILE =================");
        System.out.println("Name       : "+getFullName());
        System.out.println("Username   : "+getUsername());
        System.out.println("Contact    : "+getContactNumber());
        System.out.println("Last Login : "+ShowLastLogin());
        System.out.println("===============================================\n");
    }

    // Show Staff Dashboard
    public void showStaffDashboard() {
        displayDashboardGreeting();
        System.out.println("Available Actions:");
        String[] actions = getAllowedActions();
        for (int i=0;i<actions.length;i++) {
            System.out.println((i+1)+". "+actions[i]);
        }
        System.out.println();
    }
    public void requestPasswordReset(LoginManager manager) {
        manager.requestPasswordReset(getUsername());
    }
}

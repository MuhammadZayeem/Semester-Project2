package SemesterProject.Supplier;

import SemesterProject.Part;

public class LocalSupplier extends Supplier {

    public LocalSupplier(String name, String contact, String email,
                         String address, String specialization) {

        super(name, contact, email, address, specialization);
    }

    @Override
    public void displaySupplierDetails() {
        System.out.println("--------- SUPPLIER DETAILS ---------");
        System.out.println("Name: " + getName());
        System.out.println("Contact: " + getContactNumber());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + getAddress());
        System.out.println("Specialization: " + getSpecialization());
        System.out.println("------------------------------------");
    }

    @Override
    public void addSuppliedPart(Part part) {
        super.addSuppliedPart(part);
    }
}

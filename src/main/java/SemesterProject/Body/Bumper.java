package SemesterProject.Body;

public abstract class Bumper extends BodyPart {
    //-----------------------------------------------------------Constructor
    public Bumper(String partId, String name, String carChasis, int quantity, int threshold, double price){
        super(partId, name, carChasis, quantity, threshold, price);
    }
}
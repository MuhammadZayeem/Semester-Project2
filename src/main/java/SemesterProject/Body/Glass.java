package SemesterProject.Body;

public abstract class Glass extends BodyPart {
    //--------------------------------------------------------Constructor
    public Glass(String partId, String name, String carChasis, int quantity, int threshold, double unitPrice) {
        super(partId, name, carChasis, quantity, threshold, unitPrice);
    }
}
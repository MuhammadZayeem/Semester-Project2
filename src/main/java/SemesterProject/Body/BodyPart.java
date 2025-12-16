package SemesterProject.Body;
import SemesterProject.Part;

public abstract class BodyPart extends Part {
    //--------------------------------------------------------------------------Constructor
    public BodyPart(String partId, String name, String carChasis, int quantity, int threshold, double unitPrice) {
        super(partId, name, "Body", carChasis, quantity, threshold, unitPrice);
    }
}
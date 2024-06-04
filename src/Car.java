import java.util.Random;

public class Car {
    private final int id;
    private int standId; // Идентификатор стенда

    public Car(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setStandId(int standId) {
        this.standId = standId;
    }

    public int getStandId() {
        return standId;
    }
}

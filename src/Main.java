import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static void main(String[] args) {
        int numStands = 3;
        Object lock = new Object();
        CarFactory.startingTime = System.currentTimeMillis();
        CarFactory.operatingTime = 12 * 5000;
        CarFactory.carBlockingQueue = new ArrayBlockingQueue<Car>(numStands);
        double mean = 1000, stddev = 200;
        CarFactory.QueueGenerator queueGenerator = new CarFactory.QueueGenerator(mean, stddev, numStands);
        queueGenerator.start();
        ArrayList<CarFactory.CarMaintenance> stands = new ArrayList<CarFactory.CarMaintenance>();
        mean = 3000;
        stddev = 500;
        for (int i = 0; i < numStands; i++) {
            stands.add(new CarFactory.CarMaintenance(mean, stddev, lock, i));
            stands.get(i).start();
        }

        for (Thread stand : stands) {
            try {
                stand.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Всего попыток попасть на осмотр: " +
                CarFactory.QueueGenerator.totalAttempts);
        System.out.println("Попали на осмотр: " +
                CarFactory.QueueGenerator.successfulAttempts);
        System.out.println("Относительная пропускная способность центра: " +
                (String.format("%.3f", CarFactory.QueueGenerator.calculateRelativeBandWidth()))  + "%");
        System.out.println("Абсолютная  пропускная способность центра: " +
                String.format("%.3f",CarFactory.QueueGenerator.calculateAbsoluteBandWidth())  + " в час");
    }
}



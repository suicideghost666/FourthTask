import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CarFactory {
    public static BlockingQueue<Car> carBlockingQueue;
    public static long startingTime;
    public static long operatingTime;
    public static int id = 1;
    static class QueueGenerator extends Thread {

        public static double totalAttempts = 0;
        public static double successfulAttempts = 0;
        public static int indexID = 0;
        public int numStands;
        private final double mean, stddev;
        public QueueGenerator(double mean, double stddev, int numStands) {;
            this.numStands = numStands;
            this.mean = mean;
            this.stddev = stddev;
        }

        @Override
        public void run() {
            long elapsedTime = 0;
            try {
                while (elapsedTime < operatingTime) {
                    Car car = new Car(id);

                    id++;
                    addCar(car);
                    int gauss = (int) new Random().nextGaussian(mean, stddev);
                    Thread.sleep(gauss);
                    elapsedTime = System.currentTimeMillis() - startingTime;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void addCar(Car car) {
            try {
                if (carBlockingQueue.offer(car, 1000, TimeUnit.MILLISECONDS)) {
                    successfulAttempts++;
                    car.setStandId(indexID);
                    indexID = (++indexID) % numStands;
                    System.out.println((CarMaintenance.calculateVirtualTime(System.currentTimeMillis() - startingTime)) + " Машина #" + car.getId() + " добавлена в очередь на осмотр" + ", номер стенда: " + car.getStandId());
                } else {
                    System.out.println((CarMaintenance.calculateVirtualTime(System.currentTimeMillis() - startingTime)) + " Сейчас нет свободных стендов для осмотра");
                }
                totalAttempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public static double calculateRelativeBandWidth() {
//            if (id == 0) return 0;
            return (successfulAttempts / id) * 100;
        }

        public static double calculateAbsoluteBandWidth() {
            if (totalAttempts == 0) return 0.0;
            return successfulAttempts / 12.;
        }
    }
    static class CarMaintenance extends Thread {

        private final Object lock;
        public final int standId;
        private final double mean, stddev;

        public CarMaintenance(double mean, double stddev, Object lock, int standId) {
            this.lock = lock;
            this.standId = standId;
            this.mean = mean;
            this.stddev = stddev;
        }


        @Override
        public void run() {
            Random rand = new Random();
            while ((System.currentTimeMillis() - startingTime) < operatingTime || (!carBlockingQueue.isEmpty())) {

                try {
                    Car car = carBlockingQueue.peek();
                    if (car != null && car.getStandId() == standId) {
                        synchronized (lock) {
                            System.out.println((calculateVirtualTime(System.currentTimeMillis() - startingTime)) + " Машина #" + car.getId() + " начала осмотр на стенде " + standId);
                            int gauss = (int) rand.nextGaussian(mean, stddev);
                            Thread.sleep(gauss);
                            System.out.println((calculateVirtualTime(System.currentTimeMillis() - startingTime)) + " Машина #" + car.getId()+ " закончила осмотр на стенде " + standId);
                            carBlockingQueue.poll();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        protected static String calculateVirtualTime(long time) {
            int hours = (int) (time / 3600);
            int minutes = (int) ((time % 3600) / 60);
            int remainingSeconds = (int) (time % 60);
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        }

    }
}

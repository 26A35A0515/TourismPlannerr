public class Main {

    public static void main(String[] args) {

        Database.initialize();

        try {

            TourismServer.start();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
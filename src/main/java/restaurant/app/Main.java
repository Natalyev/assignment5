package restaurant.app;

import restaurant.facade.RestaurantAdminFacade;
import restaurant.facade.RestaurantFacade;
import restaurant.facade.RestaurantOperationException;
import restaurant.strategy.Demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private static final Path DATA_DIR = Path.of("data");

    public static void main(String[] args) {
        AppFactory app = new AppFactory(DATA_DIR);
        RestaurantFacade facade = app.restaurantFacade();
        RestaurantAdminFacade admin = app.adminFacade();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                if (choice.equals("1")) {
                    runOrder(scanner, facade, false, false);
                } else if (choice.equals("2")) {
                    runOrder(scanner, facade, true, false);
                } else if (choice.equals("3")) {
                    runOrder(scanner, facade, false, true);
                } else if (choice.equals("4")) {
                    beginOnly(scanner, facade);
                } else if (choice.equals("5")) {
                    resume(scanner, facade);
                } else if (choice.equals("6")) {
                    inspect(scanner, admin);
                } else if (choice.equals("7")) {
                    manualCompensation(scanner, admin);
                } else if (choice.equals("8")) {
                    resetStore();
                    app = new AppFactory(DATA_DIR);
                    facade = app.restaurantFacade();
                    admin = app.adminFacade();
                    System.out.println("Store reset.");
                } else if (choice.equals("9")) {
                    Demo.main(new String[0]);
                } else if (choice.equals("0")) {
                    return;
                } else {
                    System.out.println("Unknown option.");
                }
            } catch (RestaurantOperationException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== Restaurant Kitchen Demo ===");
        System.out.println("1. Successful A -> B -> C order");
        System.out.println("2. Force B failure");
        System.out.println("3. Force C failure");
        System.out.println("4. Begin order only (simulate restart)");
        System.out.println("5. Resume saved order");
        System.out.println("6. Inspect saved state");
        System.out.println("7. Manual compensation");
        System.out.println("8. Reset persisted store");
        System.out.println("9. Strategy demo (queue and courier selection)");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private static void runOrder(
            Scanner scanner,
            RestaurantFacade facade,
            boolean failAtB,
            boolean failAtC
    ) {
        String item = readItem(scanner);
        int quantity = readQuantity(scanner);

        String orderId = facade.placeOrder(
                item,
                quantity,
                failAtB,
                failAtC
        );

        System.out.println("Order ID: " + orderId);
        System.out.println("Progress: " + facade.getProgress(orderId).getStatus());
    }

    private static void beginOnly(
            Scanner scanner,
            RestaurantFacade facade
    ) {
        String item = readItem(scanner);
        int quantity = readQuantity(scanner);

        String orderId = facade.beginOrder(item, quantity);

        System.out.println("Saved after subsystem A.");
        System.out.println("Order ID: " + orderId);
        System.out.println("Exit and restart the app, then choose option 5.");
    }

    private static void resume(
            Scanner scanner,
            RestaurantFacade facade
    ) {
        System.out.print("Order ID: ");
        String orderId = scanner.nextLine();

        facade.continueOrder(orderId, false, false);

        System.out.println("Progress: " + facade.getProgress(orderId).getStatus());
    }

    private static void inspect(
            Scanner scanner,
            RestaurantAdminFacade admin
    ) {
        System.out.print("Order ID: ");
        String orderId = scanner.nextLine();
        System.out.println(admin.inspect(orderId));
    }

    private static void manualCompensation(
            Scanner scanner,
            RestaurantAdminFacade admin
    ) {
        System.out.print("Order ID: ");
        String orderId = scanner.nextLine();
        admin.manualCompensate(orderId);
        System.out.println("Compensation completed.");
    }

    private static String readItem(Scanner scanner) {
        System.out.print("Item: ");
        return scanner.nextLine();
    }

    private static int readQuantity(Scanner scanner) {
        System.out.print("Quantity: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private static void resetStore() {
        String[] names = {
                "orders.csv",
                "kitchen.csv",
                "finalization.csv",
                "progress.csv"
        };

        try {
            Files.createDirectories(DATA_DIR);

            for (String name : names) {
                Files.deleteIfExists(DATA_DIR.resolve(name));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot reset store", e);
        }
    }
}

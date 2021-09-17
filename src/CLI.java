import Logic.*;
import Logic.NetworkNotifications.NetworkNotification;
import Logic.NetworkNotifications.SyncMe;
import Services.Constants;
import Services.Network.ConnectionHandler;
import Services.Network.NetworkAdapter;
import Services.Network.NetworkDemon;
import Services.Utils;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class CLI {

    public static void main(String[] args){
        Menu menu = new Menu();
        int p1 = menu.addProduct(new Product("p1", "p1 is good", null));
        int p2 = menu.addProduct(new Product("p2", "p2 is bad", null));
        int p3 = menu.addProduct(new Product("p3", "p2 is aah", null));
        int p4 = menu.addProduct(new Product("p4", "p2 is yummy", null));

        Constants.CONFIG_DIR = "/home/tamer/IdeaProjects/OrderTakerBackend/files/r1";
        Constants.LOG_FILE = "/home/tamer/IdeaProjects/OrderTakerBackend/files/l1";

//        Restaurant tmpRes = (Restaurant) Utils.readRestaurant();
        Restaurant tmpRes = null;
        if(tmpRes == null){
            tmpRes = new Restaurant();
            tmpRes.addTable(new Table(1));
            tmpRes.addTable(new Table(2));
            tmpRes.addTable(new Table(3));
            Utils.writeConfig(tmpRes);
        }

        final Restaurant restaurant = tmpRes;
        final int[] tables_numbers = restaurant.getTables();

        Waitress John = new Waitress("John", restaurant) {
            @Override
            public void onUDPNotification(InetAddress address, NetworkNotification notification) {
                Utils.writeToLog("[UDP] " + notification.toString());
            }

            @Override
            public void onTCPNotification(ConnectionHandler handler, NetworkNotification notification) {
//                System.out.println("TCP : " + notification.toString());
            }
        };
        // Set network adapter
        NetworkAdapter.init(new NetworkAdapter() {
            @Override
            public void onConnectionEstablished(ConnectionHandler connectionHandler) {

            }
        });
        NetworkAdapter.getInstance().start();
        John.setNetworkAdapter(NetworkAdapter.getInstance());
        NetworkAdapter.getInstance().register(John);

        NetworkDemon demon = new NetworkDemon(John);
        demon.start();


        Utils.writeToLog("Started OrderTaker");

        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println("1 - Open Table");
            System.out.println("2 - Order Item");
            System.out.println("3 - Remove Item");
            System.out.println("4 - Submit Order");
            System.out.println("5 - Edit Table");
            System.out.println("6 - Close Table");
            System.out.println("7 - Cancel Table");
            System.out.println("8 - Show Menu");
            System.out.println("9 - Print Table");
            System.out.println("10 - Print Order History");
            System.out.println("90 - Exit");
            System.out.print("Choose an action : ");
            String choice = scanner.next();

            if(choice.equals("1")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                John.openTable(Integer.parseInt(choice));
                System.out.println("Successfully opened table " + choice);
            }else if(choice.equals("2")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                int table = Integer.parseInt(choice);
                System.out.print("Product index (refer to menu): ");
                choice   = scanner.next();
                Product p = menu.getProduct(Integer.parseInt(choice));
                System.out.print("Quantity: ");
                choice = scanner.next();
                int q = Integer.parseInt(choice);
                System.out.print("notes: ");
                choice = scanner.next();
                String notes = choice;

                John.orderItem(table, p, q, notes);
                System.out.println("Successfully ordered " + p.getName() );
            }else if(choice.equals("3")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                int table = Integer.parseInt(choice);
                System.out.print("Order Item Number(print table to get) : ");
                choice = scanner.next();
                int itemNumber = Integer.parseInt(choice);

                John.removeItem(table, itemNumber);
                System.out.println("Item Removed Successfully !");
            }else if(choice.equals("4")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                int table = Integer.parseInt(choice);
                John.submitOrder(table);
                System.out.println("Submitted Order Successfully !");
            }else if(choice.equals("5")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                int table = Integer.parseInt(choice);
                System.out.print("Order Item Number(print table to get) : ");
                choice = scanner.next();
                int itemNumber = Integer.parseInt(choice);
                System.out.print("Product index (refer to menu): ");
                choice = scanner.next();
                Product p = menu.getProduct(Integer.parseInt(choice));
                System.out.print("Quantity: ");
                choice = scanner.next();
                int q = Integer.parseInt(choice);
                System.out.print("notes: ");
                choice = scanner.next();
                String notes = choice;

                John.editOrder(table, itemNumber, p, q, notes);
                System.out.println("Edited successfully");
            }else if(choice.equals("6")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                int table = Integer.parseInt(choice);
                John.closeOrder(table);
                System.out.println("Successfully closed table " + table);
            }else if(choice.equals("7")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                int table = Integer.parseInt(choice);
                John.cancelOrder(table);
                System.out.println("Successfully canceled table " + table);
            }else if(choice.equals("8")){
                System.out.println(Utils.MapToString(menu.getProducts()));
            }else if(choice.equals("9")){
                System.out.print("Table Number : ");
                choice = scanner.next();
                System.out.println(John.getRestaurant().getTable(Integer.parseInt(choice)));
            }else if(choice.equals("10")){
                System.out.println(John.getRestaurant().getOrderHistory());
            }else if(choice.equals("90")){
                System.exit(0);
            }else {
                System.out.println("Choose from the list above");
            }
        }


    }

    /**
     * scenario 1:
     *  a family enters the restaurant, set down on table 1 and order :
     *   5 of p1,
     *   2 of p2,
     *   3 of p3 without tomato.
     *
     *   they finish there meal, pay and go home
     * **/
    public static void scenario1(Menu menu, Restaurant restaurant, Waitress waitress) {

        int p1 = menu.addProduct(new Product("p1", "p1 is good", null));
        int p2 = menu.addProduct(new Product("p2", "p2 is bad", null));
        int p3 = menu.addProduct(new Product("p3", "p3 is hot", null));

        waitress.orderItem(1, menu.getProducts().get(p1), 5, "");
        waitress.orderItem(1, menu.getProducts().get(p2), 2, "");
        waitress.orderItem(1, menu.getProducts().get(p3), 3, "without tomato");
        waitress.submitOrder(1);
        waitress.closeOrder(1);
    }

    /**
     * scenario 2:
     *  a family enters the restaurant, set down on table 1 and order :
     *   5 of p1,
     *   2 of p2,
     *
     *   then, they requested to edit p2 to be 7 (quantity) and e3mlo m3 7areef
     *
     *   they finish there meal, pay and go home
     * **/
    public static void scenario2(Menu menu, Restaurant restaurant, Waitress waitress) {

        int p1 = menu.addProduct(new Product("p1", "p1 is good", null));
        int p2 = menu.addProduct(new Product("p2", "p2 is bad", null));

        int i1 = waitress.orderItem(1, menu.getProducts().get(p1), 5, "");
        int i2 = waitress.orderItem(1, menu.getProducts().get(p2), 2, "");
        waitress.submitOrder(1);

        waitress.editOrder(1, i2, menu.getProducts().get(p2), 7, "m3 7areef");
        waitress.submitOrder(1);

        waitress.closeOrder(1);
    }

    /**
     * scenario 3:
     *  a family enters the restaurant, set down on table 1 and order :
     *   5 of p1,
     *   2 of p2,
     *
     *   then, they requested to edit p2 to be 7 (quantity) and e3mlo m3 7areef
     *   and add 4 of p3
     *
     *   then they requested to remove p2
     *
     *   they finish there meal, pay and go home
     * **/
    public static void scenario3(Menu menu, Restaurant restaurant, Waitress waitress){

        int p1 = menu.addProduct(new Product("p1", "p1 is good", null));
        int p2 = menu.addProduct(new Product("p2", "p2 is bad", null));
        int p3 = menu.addProduct(new Product("p3", "p3 is hot", null));


        int i1 = waitress.orderItem(1, menu.getProducts().get(p1), 5, "");
        int i2 = waitress.orderItem(1, menu.getProducts().get(p2), 2, "");
        waitress.submitOrder(1);

        waitress.editOrder(1, i2, menu.getProducts().get(p2), 7, "m3 7areef");
        waitress.orderItem(1, menu.getProducts().get(p3), 4, "" );
        waitress.submitOrder(1);

        waitress.removeItem(1, i2);
        waitress.submitOrder(1);

        waitress.closeOrder(1);
    }


}


class Menu {

    private int productCounter;
    private HashMap<Integer, Product> products;

    public Menu(){
        this.products = new LinkedHashMap<>();
    }

    public Menu(LinkedList<Product> products){
        this.products = new LinkedHashMap<>();
        for (Product p: products) {
            this.products.put((productCounter++), p);
        }
    }

    public HashMap<Integer, Product> getProducts() {
        return products;
    }


    public int addProduct(Product p){
        int newProductIndex = (productCounter++);
        this.products.put(newProductIndex, p);
        return newProductIndex;

    }

    public Product getProduct(int product){
        return this.products.get(product);
    }
}
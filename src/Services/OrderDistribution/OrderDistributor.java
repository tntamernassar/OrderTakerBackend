package Services.OrderDistribution;

import Logic.Order;
import Services.Utils;

public class OrderDistributor {

    public static void distribute(OrderDistributionRequest orderDistributionRequest){
        System.out.println("************************KITCHEN************************");
        System.out.println("Table : " + orderDistributionRequest.getTableNumber());
        System.out.println(Utils.MapToString(orderDistributionRequest.getOrder().getOrderItems()));
        System.out.println("************************KITCHEN************************\n\n\n");

        Utils.writeToLog("Sent an order distribution to the kitchen for table " + orderDistributionRequest.getTableNumber() );

    }

    public static void distributeEdit(Order oldOrder, OrderDistributionRequest newOrderDistributionRequest){
        System.out.println("************************KITCHEN - EDIT************************");
        System.out.println("Table : " + newOrderDistributionRequest.getTableNumber());
        System.out.println("WAS : " + Utils.MapToString(oldOrder.getOrderItems()));
        System.out.println("NOW : " + Utils.MapToString(newOrderDistributionRequest.getOrder().getOrderItems()));
        System.out.println("************************KITCHEN - EDIT************************\n\n\n");

        Utils.writeToLog("Sent an edit order distribution to the kitchen  for table " + newOrderDistributionRequest.getTableNumber());

    }

}

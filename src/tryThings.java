import Logic.Restaurant;
import Logic.Table;
import Services.FileManager;

import java.util.LinkedList;

public class tryThings {

    public static void main(String[] args){
        LinkedList<Integer> l = new LinkedList<>();
        l.push(1);
        l.push(2);
        l.push(3);
        System.out.println(l.pollLast());
        System.out.println(l.pollLast());
        System.out.println(l.pollLast());
    }

}

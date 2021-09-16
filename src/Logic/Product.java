package Logic;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {

    private static int ID = 0;
    private int id;
    private String name;
    private String description;
    private String[] images;

    public Product(String name, String description, String[] images){
        this.id = ID++;
        this.name = name;
        this.description = description;
        this.images = images;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

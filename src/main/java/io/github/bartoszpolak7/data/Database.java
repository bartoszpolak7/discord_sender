package io.github.bartoszpolak7.data;

import io.github.bartoszpolak7.model.Category;
import io.github.bartoszpolak7.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Database {

    private final List<Category> categories = List.of(
            new Category(1, "Dania"),
            new Category(2, "Napitki")
    );

    private final List<Product> products = List.of(
            new Product(1, "Cienka zupa szczawiowa", 10.99, 1),
            new Product(2, "Szaszłyk z drobiu trolla", 20.99, 1),
            new Product(3, "Tur nadziewany mięsaczem (zestaw rodzinny)", 60.99, 1),
            new Product(3, "Wywar z okolumpa", 10.99, 2),
            new Product(3, "Białe uszy koboldzie posypane parmezanem", 10.99, 1),
            new Product(3, "Piwo", 5.99, 2),
            new Product(3, "Wino ze Złotej Przystani", 300.0, 2)
    );

    public List<Category> getCategories() {
        return categories;
    }

    public List<Product> getProducts() {
        return products;
    }
}

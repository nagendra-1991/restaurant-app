
package com.example.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
@CrossOrigin
public class RestaurantApplication {

    private final AtomicLong orderCounter = new AtomicLong(1000);

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }

    public record MenuItem(
            int id, String name, String description, double price) {}

    public record OrderRequest(String customerName, String itemName, int quantity) {}

    @GetMapping("/api/menu")
    public List<MenuItem> getMenu() {
        return List.of(
            new MenuItem(1, "Veg Burger", "Fresh vegetable burger", 120),
            new MenuItem(2, "Pizza", "Cheese and tomato pizza", 250),
            new MenuItem(3, "Biryani", "Delicious chicken biryani", 220),
            new MenuItem(4, "Masala Dosa", "Crispy dosa with chutney", 90),
            new MenuItem(5, "French Fries", "Crispy golden fries", 100),
            new MenuItem(6, "Cold Coffee", "Chilled creamy coffee", 80)
        );
    }

    @PostMapping("/api/orders")
    public Map<String, Object> placeOrder(@RequestBody OrderRequest request) {
        if (request.customerName() == null
                || request.customerName().isBlank()
                || request.itemName() == null
                || request.quantity() < 1
                || request.quantity() > 100) {
            throw new IllegalArgumentException("Invalid order details");
        }

        MenuItem item = getMenu().stream()
                .filter(m -> m.name().equalsIgnoreCase(request.itemName()))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Menu item not found"));

        long orderId = orderCounter.incrementAndGet();
        double total = item.price() * request.quantity();

        return Map.of(
            "orderId", orderId,
            "customerName", request.customerName(),
            "itemName", item.name(),
            "quantity", request.quantity(),
            "total", total,
            "message", "Order placed successfully!"
        );
    }
}


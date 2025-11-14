package store.order;

import java.util.List;

public class OrderParser {

    public static OrderOut to(Order order) {
        return order == null ? null :
            OrderOut.builder()
                .id(order.id())
                .date(order.createdAt())
                .total(order.total())
                .items(order.items() == null ? List.of() :
                    order.items().stream()
                        .map(OrderParser::toItemOut)
                        .toList())
                .build();
    }

    public static List<OrderSummaryOut> toSummary(List<Order> orders) {
        return orders == null ? List.of() :
            orders.stream()
                .map(order -> OrderSummaryOut.builder()
                    .id(order.id())
                    .date(order.createdAt())
                    .total(order.total())
                    .build())
                .toList();
    }

    private static OrderItemOut toItemOut(OrderItem item) {
        return OrderItemOut.builder()
            .id(item.id())
            .quantity(item.quantity())
            .total(item.total())
            .product(item.product() == null ? null :
                OrderItemProductOut.builder()
                    .id(item.product().id())
                    .name(item.product().name())
                    .unit(item.product().unit())
                    .price(item.product().price())
                    .build())
            .build();
    }
}


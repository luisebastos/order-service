package store.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;
import store.product.ProductController;
import store.product.ProductOut;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductController productController;

    public Order create(String accountId, OrderIn in) {
        validateAccount(accountId);
        if (in == null || in.items() == null || in.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order items are mandatory!");
        }

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemIn itemIn : in.items()) {
            items.add(buildItem(itemIn));
        }

        BigDecimal total = items.stream()
            .map(OrderItem::total)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
            .accountId(accountId)
            .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
            .items(items)
            .total(total)
            .build();

        OrderModel saved = orderRepository.save(new OrderModel(order));
        return saved.to();
    }

    public List<Order> findAll(String accountId) {
        validateAccount(accountId);
        return orderRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
            .map(OrderModel::to)
            .toList();
    }

    public Order findById(String accountId, String id) {
        validateAccount(accountId);
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order id is mandatory!");
        }
        UUID uuid = parseOrderId(id);
        return orderRepository.findByIdAndAccountId(uuid, accountId)
            .map(OrderModel::to)
            .orElse(null);
    }

    private void validateAccount(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is required!");
        }
    }

    private OrderItem buildItem(OrderItemIn itemIn) {
        if (itemIn == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order item is mandatory!");
        }
        if (itemIn.idProduct() == null || itemIn.idProduct().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product id is mandatory!");
        }
        if (itemIn.quantity() == null || itemIn.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero!");
        }

        ProductOut product = fetchProduct(itemIn.idProduct());
        BigDecimal quantity = BigDecimal.valueOf(itemIn.quantity());
        BigDecimal total = product.price()
            .multiply(quantity)
            .setScale(2, RoundingMode.HALF_UP);

        return OrderItem.builder()
            .product(OrderItemProduct.builder()
                .id(product.id())
                .name(product.name())
                .unit(product.unit())
                .price(product.price())
                .build())
            .quantity(itemIn.quantity())
            .total(total)
            .build();
    }

    private ProductOut fetchProduct(String idProduct) {
        try {
            ResponseEntity<ProductOut> response = productController.findById(idProduct);
            ProductOut out = response.getBody();
            if (out == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Product service returned empty response!");
            }
            return out;
        } catch (FeignException.NotFound e) {
            logger.debug("Product not found: {}", idProduct, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found: " + idProduct);
        } catch (FeignException e) {
            logger.error("Error fetching product {}: {}", idProduct, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to fetch product " + idProduct);
        }
    }

    private UUID parseOrderId(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order id is invalid!");
        }
    }
}


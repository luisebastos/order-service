package store.order;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "purchase_order_item")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_unit", nullable = false, length = 50)
    private String productUnit;

    @Column(name = "product_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    public OrderItemModel(OrderItem item) {
        this.id = item.id() == null ? null : UUID.fromString(item.id());
        if (item.product() != null) {
            this.productId = item.product().id();
            this.productName = item.product().name();
            this.productUnit = item.product().unit();
            this.productPrice = item.product().price();
        }
        this.quantity = item.quantity();
        this.total = item.total();
    }

    public OrderItem to() {
        return OrderItem.builder()
            .id(this.id == null ? null : this.id.toString())
            .product(OrderItemProduct.builder()
                .id(this.productId)
                .name(this.productName)
                .unit(this.productUnit)
                .price(this.productPrice)
                .build())
            .quantity(this.quantity)
            .total(this.total)
            .build();
    }
}


package store.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<OrderItemModel> items = new ArrayList<>();

    public OrderModel(Order order) {
        this.id = order.id() == null ? null : UUID.fromString(order.id());
        this.accountId = order.accountId();
        this.createdAt = order.createdAt();
        this.total = order.total();
        if (order.items() != null) {
            order.items().forEach(item -> {
                OrderItemModel model = new OrderItemModel(item)
                    .order(this);
                this.items.add(model);
            });
        }
    }

    public Order to() {
        return Order.builder()
            .id(this.id == null ? null : this.id.toString())
            .accountId(this.accountId)
            .createdAt(this.createdAt)
            .total(this.total)
            .items(this.items == null ? List.of() : this.items.stream()
                .map(OrderItemModel::to)
                .toList())
            .build();
    }
}


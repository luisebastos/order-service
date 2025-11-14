package store.order;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    private String id;
    private OrderItemProduct product;
    private Integer quantity;
    private BigDecimal total;
}


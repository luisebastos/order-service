package store.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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
public class Order {

    private String id;
    private String accountId;
    private OffsetDateTime createdAt;
    private BigDecimal total;
    private List<OrderItem> items;
}


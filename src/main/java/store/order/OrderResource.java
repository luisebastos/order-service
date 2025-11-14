package store.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class OrderResource implements OrderController {

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<OrderOut> create(String accountId, OrderIn in) {
        Order created = orderService.create(accountId, in);

        return ResponseEntity
            .created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.id())
                    .toUri()
            ).body(OrderParser.to(created));
    }

    @Override
    public ResponseEntity<List<OrderSummaryOut>> findAll(String accountId) {
        return ResponseEntity.ok(
            OrderParser.toSummary(orderService.findAll(accountId))
        );
    }

    @Override
    public ResponseEntity<OrderOut> findById(String accountId, String id) {
        Order found = orderService.findById(accountId, id);
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(OrderParser.to(found));
    }
}


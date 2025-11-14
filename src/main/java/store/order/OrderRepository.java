package store.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, UUID> {

    List<OrderModel> findByAccountIdOrderByCreatedAtDesc(String accountId);

    Optional<OrderModel> findByIdAndAccountId(UUID id, String accountId);
}


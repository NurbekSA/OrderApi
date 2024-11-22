package amanzat.com.persistence.repository;

import amanzat.com.persistence.model.entity.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<OrderModel, String> {
    public Optional<OrderModel> findByUserId(String id);
    public Optional<OrderModel> findByBoxId(String id);
//    @Query("SELECT o FROM Order o WHERE o.bookingDateTime < :currentTimestamp AND o.status <> 'EXPIRED'")
//    List<OrderModel> findExpiredOrders(long currentTimestamp);
//    @Query("SELECT o FROM Order o WHERE o.status = 'EXPIRED'")
//    List<OrderModel> findOrderWithStatusExpired();
//    @Query("SELECT o FROM Order o WHERE o.status = 'CREATED'")
//    List<OrderModel> findCreatedOrders(long currentTimestamp);

}

package lt.paymentprocessing.repository;

import lt.paymentprocessing.model.Payment;
import lt.paymentprocessing.model.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p.id FROM Payment p" +
            " WHERE p.status = :status" +
            " AND (:amountFrom IS NULL OR p.amount >= :amountFrom)" +
            " AND (:amountTill IS NULL OR p.amount <= :amountTill)")
    List<Long> findIdsByStatusAndAmountBetween(PaymentStatusEnum status, BigDecimal amountFrom, BigDecimal amountTill);

}

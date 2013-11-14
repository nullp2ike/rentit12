package cs.ut.repository;
import java.util.Date;
import java.util.List;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.Plant;
import cs.ut.domain.PurchaseOrder;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RooJpaRepository(domainType = PurchaseOrder.class)
public interface PurchaseOrderRepository {
	@Query("SELECT po FROM PurchaseOrder AS po WHERE po.status = :status")
	
	@Transactional(readOnly = true)
	List<PurchaseOrder> findByStatus(@Param("status") HireRequestStatus status);
}

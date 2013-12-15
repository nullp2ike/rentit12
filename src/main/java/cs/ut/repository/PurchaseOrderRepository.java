package cs.ut.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.PurchaseOrder;
import cs.ut.domain.PurchaseOrderUpdate;

@RooJpaRepository(domainType = PurchaseOrder.class)
public interface PurchaseOrderRepository {
	@Query("SELECT po FROM PurchaseOrder AS po WHERE po.status = :status")
	
	@Transactional(readOnly = true)
	List<PurchaseOrder> findByStatus(@Param("status") HireRequestStatus status);
	
	@Query("SELECT po from PurchaseOrder AS po WHERE po.status = :status AND invoice_sent = false")
	
	@Transactional(readOnly = true)
	List<PurchaseOrder> findForSendingInvoice(@Param("status") HireRequestStatus status);
	
	@Query("SELECT pou FROM PurchaseOrderUpdate AS pou WHERE pou.status = :status")
	
	@Transactional(readOnly = true)
	List<PurchaseOrderUpdate> findUpdatesByStatus(@Param("status") HireRequestStatus status);
	
	
}

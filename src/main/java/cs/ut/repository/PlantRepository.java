package cs.ut.repository;
import java.util.Date;
import java.util.List;

import cs.ut.domain.Plant;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RooJpaRepository(domainType = Plant.class)
public interface PlantRepository {
	
	@Query("SELECT p FROM Plant AS p WHERE p.id NOT IN (SELECT po.plant FROM PurchaseOrder po WHERE po.startDate >= :startDate or po.endDate <= :endDate)")
	
	@Transactional(readOnly = true)
	List<Plant> findByDateRange(@Param("startDate") Date startD, @Param("endDate") Date endD);
	
}

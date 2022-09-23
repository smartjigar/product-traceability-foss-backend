package net.catenax.traceability.investigation.infrastructure.adapters.jpa;

import net.catenax.traceability.investigation.domain.model.InvestigationStatus;
import net.catenax.traceability.investigation.domain.model.InvestigationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, String> {
	List<NotificationEntity> findByInvestigations(InvestigationsEntity investigationsEntity);

	Page<NotificationEntity> findByInvestigationsIsDeletedFalseAndStatusInAndInvestigationsType(Pageable pageable, List<InvestigationStatus> status, InvestigationType type);

	Page<NotificationEntity> findByInvestigationsIsDeletedFalseAndInvestigationsType(Pageable pageable, InvestigationType type);
}

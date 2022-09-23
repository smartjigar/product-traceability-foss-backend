package net.catenax.traceability.investigation.infrastructure.adapters.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestigationsRepository extends JpaRepository<InvestigationsEntity, String> {
	List<InvestigationsEntity> findByIsDeletedFalse();

	InvestigationsEntity findByIdAndIsDeletedFalse(String id);
}

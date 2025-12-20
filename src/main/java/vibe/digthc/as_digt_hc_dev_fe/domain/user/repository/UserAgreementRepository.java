package vibe.digthc.as_digt_hc_dev_fe.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserAgreement;

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}


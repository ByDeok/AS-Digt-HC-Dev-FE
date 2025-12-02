package vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.PortalConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortalConnectionRepository extends JpaRepository<PortalConnection, UUID> {

    /**
     * 사용자의 특정 포털 조회
     */
    @Query("SELECT pc FROM PortalConnection pc WHERE pc.user.id = :userId AND pc.id = :id")
    Optional<PortalConnection> findByUserIdAndId(@Param("userId") UUID userId, @Param("id") UUID id);

    /**
     * 사용자의 모든 포털 조회
     */
    @Query("SELECT pc FROM PortalConnection pc WHERE pc.user.id = :userId")
    List<PortalConnection> findAllByUserId(@Param("userId") UUID userId);

    /**
     * 사용자의 특정 타입 포털 조회
     */
    @Query("SELECT pc FROM PortalConnection pc WHERE pc.user.id = :userId AND pc.portalType = :portalType")
    Optional<PortalConnection> findByUserIdAndPortalType(@Param("userId") UUID userId, @Param("portalType") String portalType);

    /**
     * 사용자의 활성 포털 조회
     */
    @Query("SELECT pc FROM PortalConnection pc WHERE pc.user.id = :userId AND pc.status = 'ACTIVE'")
    List<PortalConnection> findActiveConnections(@Param("userId") UUID userId);
}


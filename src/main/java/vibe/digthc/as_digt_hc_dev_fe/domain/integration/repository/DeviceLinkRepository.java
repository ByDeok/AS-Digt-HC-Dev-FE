package vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.DeviceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceLinkRepository extends JpaRepository<DeviceLink, UUID> {

    /**
     * 사용자의 특정 디바이스 조회
     */
    @Query("SELECT dl FROM DeviceLink dl WHERE dl.user.id = :userId AND dl.id = :id")
    Optional<DeviceLink> findByUserIdAndId(@Param("userId") UUID userId, @Param("id") UUID id);

    /**
     * 사용자의 모든 디바이스 조회
     */
    @Query("SELECT dl FROM DeviceLink dl WHERE dl.user.id = :userId")
    List<DeviceLink> findAllByUserId(@Param("userId") UUID userId);

    /**
     * 사용자의 특정 벤더 디바이스 조회
     */
    @Query("SELECT dl FROM DeviceLink dl WHERE dl.user.id = :userId AND dl.vendor = :vendor")
    Optional<DeviceLink> findByUserIdAndVendor(@Param("userId") UUID userId, @Param("vendor") String vendor);

    /**
     * 사용자의 활성 디바이스 조회
     */
    @Query("SELECT dl FROM DeviceLink dl WHERE dl.user.id = :userId AND dl.status = 'ACTIVE'")
    List<DeviceLink> findActiveDevices(@Param("userId") UUID userId);

    /**
     * 토큰 갱신이 필요한 디바이스 조회
     */
    @Query("SELECT dl FROM DeviceLink dl WHERE dl.status = 'ACTIVE' " +
           "AND dl.tokenExpiresAt < :threshold")
    List<DeviceLink> findDevicesNeedingTokenRefresh(@Param("threshold") LocalDateTime threshold);

    /**
     * 동기화 대상 디바이스 조회
     */
    @Query("SELECT dl FROM DeviceLink dl WHERE dl.status = 'ACTIVE' " +
           "AND (dl.lastSyncAt IS NULL OR dl.lastSyncAt < :since)")
    List<DeviceLink> findDevicesNeedingSync(@Param("since") LocalDateTime since);
}


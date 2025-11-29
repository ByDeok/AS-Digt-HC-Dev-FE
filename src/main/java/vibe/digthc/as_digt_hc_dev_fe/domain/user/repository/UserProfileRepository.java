package vibe.digthc.as_digt_hc_dev_fe.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.UserProfile;

import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUser(User user);
}


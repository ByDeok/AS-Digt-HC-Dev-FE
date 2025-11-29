설계된 ERD와 CLD를 바탕으로 작성한 **JPA Entity 및 Repository 구현 코드**입니다.

요청하신 대로 **Lombok**을 활용하여 보일러플레이트 코드를 줄였고, **BaseTimeEntity**를 통한 Auditing, 그리고 **DB 제약조건(@Column)**을 명확히 명시했습니다.

### 1. Base Entity (Auditing)
모든 엔티티의 공통 조상이 되는 클래스입니다.

```java
package com.pollosseum.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

---

### 2. User Entity (Authentication Core)
인증의 핵심이 되는 `users` 테이블 매핑입니다. **UUID**를 PK로 사용합니다.

```java
package com.pollosseum.domain.user.entity;

import com.pollosseum.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위해 PROTECTED 허용
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255) // 소셜 로그인 시 비밀번호 null 가능성 고려 (비즈니스 로직에 따라 nullable false 처리)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider authProvider; // EMAIL, GOOGLE, KAKAO

    @Column(length = 255)
    private String providerId; // 소셜 로그인 식별자

    // 양방향 매핑 (선택사항: User에서 Profile 접근이 빈번할 경우 추가)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @Builder
    public User(String email, String password, String name, Role role, AuthProvider authProvider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.authProvider = authProvider;
        this.providerId = providerId;
    }
}
```

---

### 3. UserProfile Entity (Extension)
ERD 설계에 따라 **User와 1:1 관계**를 가지며, User의 ID를 PK이자 FK로 사용하는 식별 관계(`@MapsId`) 패턴을 적용할 수도 있으나, 여기서는 확장성을 위해 일반적인 `@OneToOne`으로 구현했습니다.

```java
package com.pollosseum.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @MapsId // User의 PK를 이 테이블의 PK로 공유 (1:1 식별 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Builder
    public UserProfile(User user, String phoneNumber, String profileImageUrl) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }
}
```

---

### 4. UserAgreement Entity (Audit)
약관 동의 내역입니다. 사용자가 약관에 동의한 시점을 기록합니다.

```java
package com.pollosseum.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_agreements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean termsService; // 서비스 이용약관

    @Column(nullable = false)
    private boolean privacyPolicy; // 개인정보 처리방침

    @Column(nullable = false)
    private boolean marketingConsent; // 마케팅 수신 동의

    @Column(nullable = false)
    private LocalDateTime agreedAt;

    @Builder
    public UserAgreement(User user, boolean termsService, boolean privacyPolicy, boolean marketingConsent) {
        this.user = user;
        this.termsService = termsService;
        this.privacyPolicy = privacyPolicy;
        this.marketingConsent = marketingConsent;
        this.agreedAt = LocalDateTime.now();
    }
}
```

---

### 5. User Repository Interface
이메일 중복 검사를 위한 쿼리 메소드를 포함합니다.

```java
package com.pollosseum.domain.user.repository;

import com.pollosseum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * 이메일 중복 검사
     */
    boolean existsByEmail(String email);

    /**
     * 로그인 시 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);
}
```

### 💡 구현 시 체크 포인트
1.  **`@NoArgsConstructor(access = AccessLevel.PROTECTED)`**: JPA 스펙상 기본 생성자가 필요하지만, 무분별한 객체 생성을 막기 위해 `protected`로 제한했습니다.
2.  **`@Builder`**: 생성자 대신 빌더 패턴을 사용하여 객체 생성의 가독성을 높였습니다.
3.  **`@Enumerated(EnumType.STRING)`**: Enum 저장 시 순서(ORDINAL)가 아닌 이름(STRING)으로 저장하여, Enum 순서 변경 시 데이터 정합성 문제를 방지했습니다.
4.  **`@MapsId` (UserProfile)**: User와 UserProfile은 생명주기가 같거나 밀접하므로, User의 ID를 그대로 PK로 사용하여 조인 성능을 최적화할 수 있는 구조입니다.
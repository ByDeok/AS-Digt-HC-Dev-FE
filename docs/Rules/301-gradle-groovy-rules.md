# Gradle Groovy 구성 규칙

## 필수 구성
```groovy
plugins {
    id 'org.springframework.boot' version '3.4.5'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'java'
}

group = 'com.pollosseum'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    // Test Dependencies
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}

test {
    useJUnitPlatform()
}

// 성능 및 빌드 설정
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
```

## 일반 작업
```groovy
bootRun {
    args = ["--spring.profiles.active=dev"]
}

tasks.named('test') {
    useJUnitPlatform()
}
```

## 참고:
- Java 및 Spring 가이드라인은 [300-java-spring-cursor-rules.md](300-java-spring-cursor-rules.md) 참조
- 기술 스택 세부사항은 [002-tech-stack.md](002-tech-stack.md) 참조

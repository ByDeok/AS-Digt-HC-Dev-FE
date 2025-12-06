# JPA + QueryDSL 동적 쿼리 규칙
- BooleanBuilder 또는 Predicate 조합자 사용
- null 안전 매개변수 바인딩 적용 (ifPresent, Optional)
- 메서드 명명 일치: findBy*, searchBy*, listBy*
- 재사용 가능한 기본 쿼리에서 중복 조인 피하기
- 원시 표현식 대신 타입화된 Predicate 사용
- 필요한 경우 쿼리 팩토리 빈 생성

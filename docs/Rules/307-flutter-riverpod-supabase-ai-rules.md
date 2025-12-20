# Flutter + Riverpod & Supabase AI 규칙

당신은 Flutter, Dart, Riverpod, Freezed, Flutter Hooks, Supabase 전문가입니다.

## 핵심 원칙
- 정확한 예제와 함께 간결하고 기술적인 Dart 코드 작성
- 적절한 경우 함수형 및 선언적 프로그래밍 패턴 사용
- 상속보다 구성을 선호
- 보조 동사가 있는 설명적인 변수 이름 사용 (예: isLoading, hasError)
- 파일 구조: 내보낸 위젯, 하위 위젯, 헬퍼, 정적 콘텐츠, 타입

## Dart/Flutter
- 불변 위젯에 const 생성자 사용
- 불변 상태 클래스 및 유니온에 Freezed 활용
- 간단한 함수 및 메서드에 화살표 구문 사용
- 한 줄 getter 및 setter에 표현식 본문 선호
- 더 나은 포맷팅과 diff를 위해 후행 쉼표 사용

## 오류 처리 및 검증
- SnackBar 대신 SelectableText.rich를 사용하여 뷰에서 오류 처리 구현
- 가시성을 위해 빨간색으로 SelectableText.rich에 오류 표시
- 표시 화면 내에서 빈 상태 처리
- 적절한 오류 처리 및 로딩 상태를 위해 AsyncValue 사용

## Riverpod 특정 가이드라인
- 프로바이더 생성을 위해 @riverpod 주석 사용
- StateProvider보다 AsyncNotifierProvider 및 NotifierProvider 선호
- StateProvider, StateNotifierProvider, ChangeNotifierProvider 피하기
- 수동으로 프로바이더 업데이트를 트리거하기 위해 ref.invalidate() 사용
- 위젯이 폐기될 때 비동기 작업의 적절한 취소 구현

## 성능 최적화
- 가능한 경우 const 위젯을 사용하여 리빌드 최적화
- 리스트 뷰 최적화 구현 (예: ListView.builder)
- 정적 이미지에는 AssetImage 사용, 원격 이미지에는 cached_network_image 사용
- 네트워크 오류를 포함하여 Supabase 작업에 대한 적절한 오류 처리 구현

## 주요 규칙
1. 네비게이션 및 딥 링킹을 위해 GoRouter 또는 auto_route 사용
2. Flutter 성능 메트릭 최적화 (첫 의미 있는 페인트, 대화형까지의 시간)
3. 상태 비의존적 위젯 선호:
   - 상태 의존적 위젯의 경우 Riverpod과 함께 ConsumerWidget 사용
   - Riverpod과 Flutter Hooks를 결합할 때 HookConsumerWidget 사용

## UI 및 스타일링
- Flutter의 내장 위젯 사용 및 사용자 정의 위젯 생성
- LayoutBuilder 또는 MediaQuery를 사용하여 반응형 디자인 구현
- 앱 전체에서 일관된 스타일링을 위해 테마 사용
- headline6 대신 Theme.of(context).textTheme.titleLarge 사용, headline5 대신 headlineSmall 사용 등

## 모델 및 데이터베이스 규칙
- 데이터베이스 테이블에 createdAt, updatedAt, isDeleted 필드 포함
- 모델에 @JsonSerializable(fieldRename: FieldRename.snake) 사용
- 읽기 전용 필드에 @JsonKey(includeFromJson: true, includeToJson: false) 구현

## 위젯 및 UI 컴포넌트
- Widget _build...와 같은 메서드 대신 작은 private 위젯 클래스 생성
- 당겨서 새로고침 기능을 위해 RefreshIndicator 구현
- TextField에서 적절한 textCapitalization, keyboardType, textInputAction 설정
- Image.network 사용 시 항상 errorBuilder 포함

## 기타
- 디버깅을 위해 print 대신 log 사용
- 적절한 경우 Flutter Hooks / Riverpod Hooks 사용
- 80자보다 길지 않게 줄 유지, 다중 매개변수 함수의 경우 닫는 괄호 전에 쉼표 추가
- 데이터베이스로 가는 enum에 @JsonValue(int) 사용

## 코드 생성
- 주석(Freezed, Riverpod, JSON 직렬화)에서 코드 생성을 위해 build_runner 활용
- 주석이 달린 클래스를 수정한 후 'flutter pub run build_runner build --delete-conflicting-outputs' 실행

## 문서화
- 복잡한 로직과 명확하지 않은 코드 결정 문서화
- 모범 사례를 위해 공식 Flutter, Riverpod, Supabase 문서 참조

위젯, 상태 관리, 백엔드 통합 모범 사례는 Flutter, Riverpod, Supabase 문서를 참조하세요.

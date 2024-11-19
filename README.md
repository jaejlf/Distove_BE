# MSA 기반 실시간 메신저 서비스, 디스코드
![image](https://user-images.githubusercontent.com/78673570/219935915-f0481c82-55a5-43dd-88ca-3065649f1c06.png)

## 💬 프로젝트 소개
음성/화상 및 채팅 서비스를 제공하는 [디스코드](https://discord.com/)를 클론 코딩한 프로젝트입니다.

<br>

핵심 기능은 다음과 같습니다.
1. 실시간 음성/화상 및 채팅
2. 사용자의 상태(온라인/오프라인/자리비움) 변화에 따른 실시간 활동 상태 표시
3. 주제 기반 채널, 멤버별 역할/권한 설정 등 다양한 사용자 커스터마이징

<br><br>

## 🛠 기술 스택
- `Spring Boot(Java)` 를 사용하여 API 를 개발하였고, 데이터베이스로는 `MySQL` 과 `MongoDB` 를 사용하였습니다.
- 실시간 통신을 위해 `Web Socket(with STOMP)` 과 `Web RTC` 를 사용하였습니다.
- `MSA` 기반으로 프로젝트를 진행하였고, 각 마이크로서비스 간의 통신을 위해 `Open Feign` 을 사용하였습니다.
- `LinkedBlockingQueue` 자료구조를 통해 `Producer-Consumer` 패턴을 구현하여 비동기 이벤트 처리를 하였습니다.
- `SonarQube` 를 통해 정적 코드 분석, `Jmeter` 를 사용하여 성능 테스트를 진행하였습니다.

<br><br>

## 👩‍💻 개발 내용
- 실시간 채팅
  - 메시지 발행/수정/삭제
  - 스레드 메시지
  - 이모티콘으로 메시지에 반응하기
  - 안 읽은 메시지 알림 및 개수 표시
- 커뮤니티
  - '서버-카테고리-채널' 구조의 커뮤니티 관리
  - 멤버별 역할/권한 설정
- 실시간 음성/화상 통화
- 실시간 사용자 활동 상태 표시

<br><br>

## 🧩 서비스 아키텍처
![image](https://user-images.githubusercontent.com/78673570/219940883-d2e620ec-2c91-4102-aa6d-a0ab7566f6e0.png)
- Auth Service : 사용자 인증 서비스
- Chat Service : 실시간 텍스트 채팅 서비스
- Community Service : 서버-카테고리-채널 및 멤버 역할 관리 서비스
- Voice Service : 실시간 음성/화상 통화 서비스
- Presence Service :  활동 상태 관리 서비스

<br><br>

## 📑 문서
- (private) source code
- (private) docs
- (private) ERD Diagram

<br><br>

## 🔍 화면 구성
![image](https://github.com/jaejlf/Distove_BE/assets/78673570/0caf7c5b-9850-4fbc-aa9c-545c520fbd5d)


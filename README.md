# slam-talk-BackEnd
본 웹 어플리케이션은 농구장 시설 위치를 카카오 지도에 등록하여 원하는 장소에서 사람들이 팀원이나 상대팀을 구할 수 있고 채팅을 통해 농구장 시설이나 매칭 관련 의견과 대화를 나눌 수 있는 웹앱 입니다. 또한 자유게시판, 대관양도, 중고거래 등 관심 있는 사람들이 모여 형성하는 커뮤니티도 존재합니다.

이 프로젝트는 구름톤 트레이닝 풀스택 2회차 과정 파이널 프로젝트에서 다수의 투표로 인기상을 수상 하였습니다.

구현 스크린샷은 맨 밑을 참고해주시기 바랍니다.

프로젝트 기간 : 2024.1.11 ~ 2024.2.22

본인의 예상 기여도(백엔드 내) : 25%

원본 레포지토리 주소 : https://github.com/SlamTalk/slam-talk-backend

# 프로젝트 내 담당 파트(메이트 찾기, 상대팀 매칭)
원하는 장소에서 함께 농구를 할 팀원을 구하는 기능인 '메이트 찾기'와 3vs3 또는 5vs5 등 원하는 형태의 팀 구성으로 함께 붙을 상대팀을 구하는 '상대팀 매칭' 기능을 구현함.

저의 담당 디렉토리:

src/main/java/sync/slamtalk/team

src/main/java/sync/slamtalk/mate

개발 환경 : Java 17, SpringBoot 3.2.1

사용 스텍 : Data JPA, QueryDSL, h2, MySQL




## 매칭 로직 설명
![application_algorithm](https://github.com/red481/slam-talk-backend/assets/72694104/46ac5bb1-5f4e-42f1-af87-5cea459c1350)

### 매칭 로직 설계 시 고려한 점
어떤 행위나 상태로 들어가기 전 진입 조건을 최소한으로 한정 짓는 것이 예외 상황을 최소화하여 버그나 예외 처리를 해야 할 경우의 수를 줄일 수 있다고 생각해서 위와 같이 모집 글의 상태에 따라 이루어 질 수 있는 신청 상태를 한정 지음.

신청자가 신청 또는 취소하거나 모집자가 수락 또는 거절할 때 글의 상태가 고용 중(Recruiting)일때만 로직이 실행되도록 하면 글이 삭제 되었거나 모집 완료 되었을 때 생길 예외 상황을 고려하지 않아도 된다는 장점이 있다.

매칭 모집 글은 모집중('RECRUITING')과 모집완료('COMPLETED')의 상태를 가짐.

신청자는 대기중('WAITING'), 취소('CANCELED'), 승낙('ACCEPTED'), 거절('REJECTED')의 상태를 가짐.

<b>모집중</b>('RECRUITING') : 글 작성자가 모집 글을 게시하는 순간부터 모집완료 버튼을 누르기 전까지의 상태를 의미함. 이 상태에선 모든 사람이 해당 글에 신청할 수 있고 취소할 수 있으며 글 작성자가 승낙, 또는 거절을 할 수 있음.

<b>모집완료</b>('COMPLETED') : 모집이 완료된 글을 의미함. 모집 완료를 활성화 하면 ACCEPTED 상태인 신청자들을 제외하고 나머지 신청자('REJECTED' OR 'WAITING')들을 신청자 목록에서 삭제하며(soft delete) COMPLETED 상태로 글의 상태를 전환함.

<b>취소</b>('CANCELED') : 모집 글을 삭제하면 취소 상태로 넘어간다.


<b>대기중</b>('WAITING') : 신청자가 해당 모집 글에 지원 신청하여 신청자 목록에 등록된 상태. 

<b>취소</b>('CANCELED') : 신청자가 취소를 하면 신청자 목록에서 제거되고 다시 신청할 수 있음. WAITING 상태인 신청자만 선택할 수 있음.

<b>승낙</b>('ACCEPTED') : 해당 글 작성자가 대기중인 신청자를 승낙하면 신청자는 승낙 상태('ACCEPTED')에 놓임. WAITING 상태인 신청자만 이 상태로 바뀔 수 있음.

<b>거절</b>('REJECTED') : 해당 글 작성자가 대기중인 신청자를 거절하면 신청자는 거절 상태('REJECTED')에 놓임. WAITING 상태인 신청자만 이 상태로 바뀔 수 있음.

# 이번 프로젝트를 진행하면서 고민했던 부분
## 사용자 경험을 고려한 데이터 가공
![image](https://github.com/red481/slam-talk-backend/assets/72694104/6321e484-1376-4e02-a313-563e4d0211b6)
- 위 코드는 메이트찾기의 모집 글에서 모집하거나 모집된 포지션만 리스트에 데이터를 담아 전송하는 메서드 입니다.
- Mapper 클래스를 만들어 프론트엔드와 사용자의 관점에 맞게 데이터를 처리하여 Dto에 담아 프론트엔드로 전달 하도록 하였습니다.
- src/main/java/sync/slamtalk/mate/mapper/EntityToDtoMapper.java 참조

## 동적 쿼리를 위한 다양한 조건을 코드로 구현
![image](https://github.com/red481/slam-talk-backend/assets/72694104/2ed7d73a-24dc-41f2-8f52-9fb093ba9c1d)
- 게시물 목록 조회 할때 필터링 기능(지역, 실력, 포지션, 약속 시간 경과 여부 등)을 구현하기 위해 동적 쿼리를 학습해야 할 필요성을 느꼈습니다.
- 위의 코드는 여러 조건 중 하나인 약속 시간 경과 여부를 알아내는 메서드입니다. 겉으로 보기엔 간단해보이지만 여러 시행착오 끝에 작성한 코드 입니다.
- src/main/java/sync/slamtalk/mate/repository/QueryRepository.java 참조

## N + 1 문제 등을 고려한 쿼리 작성
![image](https://github.com/red481/slam-talk-backend/assets/72694104/f9521a94-849e-453a-a6bc-addb41ee30f8)
- 처음엔 위와 같이 Data JPA 메서드명을 활용한 쿼리를 짰습니다. 쿼리 짜는 것이 편하다는 장점이 있지만 N + 1 문제가 발생 하였습니다.
![image](https://github.com/red481/slam-talk-backend/assets/72694104/07a8c457-dbc8-40a3-aceb-da93fc23b996)
- 위의 문제를 해결하기 위해 엔티티 그래프를 활용한 방법을 도입 하였습니다. 엔티티 그래프를 통해 다른 부분은 기존에 설정된 로딩 전략을 그대로 따르거나 지연 로딩을 하도록 하면서 원하는 부분은 즉시 로딩으로 가져올 수 있도록 하는 기술을 발휘 하였습니다.
- 엔티티 그래프를 공부하면서 정리한 블로그 글은 다음의 링크로 들어가시면 확인할 수 있습니다. (https://dev-n-life.tistory.com/76)
![image](https://github.com/red481/slam-talk-backend/assets/72694104/9e308bb9-a2a7-4283-9145-ecd27e8ce059)
- 위의 목록 조회 관련 메서드에선 쿼리DSL을 활용할때 N+1문제를 해결하기 위해 고민을 거듭하여 짠 코드 입니다.
- 100개의 글이 있다면 101개의 쿼리를 날려야 한다는 다소 부담이 존재하지만 이렇게 코드를 짬으로써  최악의 N + 1 문제를 방지할 수 있었습니다.

## 다양한 예외 상황을 고려한 코드 작성, 그리고 도메인 주도 설계 지향
![image](https://github.com/red481/slam-talk-backend/assets/72694104/913df316-71da-4fc5-a2cc-35a81e218f40)
- 위의 코드는 메이트찾기(팀원 모집) 서비스 중 모집 완료 기능을 구현한 서비스 계층 코드 입니다.
- api 요청자가 글의 작성자와 일치하는 지 확인하는 등 다양한 예외 상황을 꼼꼼히 처리 하였습니다.
- 그와 동시에 도메인 관련 비즈니스 로직은 도메인 내에서 이루어져야 한다는 도메인 주도 설계 지향적으로 코드를 짜려고 노력하였습니다.

## 다양한 예외 처리를 위한 에러 코드 작성
![image](https://github.com/red481/slam-talk-backend/assets/72694104/9f06bb73-cd88-4b43-b4db-a05737c05596)
- 위와 같이 임의로 작성한 BaseException과 커스텀 에러 코드를 이용하여 다양한 예외 발생 시 처리하도록 하였습니다.
- 이렇게 던져진 예외들은 GlobalRestControllerAdvice에서 처리 됩니다.
- 이렇게 필요에 의해 만들어진 에러 코드들은 조금 더 간소화 하고 통일하여 프론트엔드에서 처리하기 용이하게 해야 하는 과제가 남아 있습니다.

# ERD 다이어그램

![스크린샷 2024-02-25 180837](https://github.com/red481/slam-talk-backend/assets/72694104/74e7be6e-bab6-4092-a786-a91948590d0d)

초반에 데이터베이스 모델링 했을땐 다대다 매핑을 고려한 매핑 테이블 등 여러 테이블이 존재하였으나 조인 연산을 줄이는 방향으로 정하여 설계하다보니 하나의 테이블에 많은 정보가 담겼습니다.

이런 데이터베이스 설계는 조인 연산을 줄일 수 있다는 장점도 있지만 불필요한 정보까지 조회해야 하는 자원 낭비 문제가 생길수도 있습니다.

향후 데이터베이스를 설계할 때는 조인 연산 횟수나 테이블 참조 빈도 등 다양한 상황을 고려하여 설계할 계획입니다.

# API 별 기능 설명

## 1.메이트찾기
<b>메이트찾기 글 등록하기</b> (api url : Post. /api/mate/register) :

모집 글을 등록하는 요청입니다.

<b>메이트찾기 글 조회하기</b> (api url : Get. /api/mate/read/{post_id}) :

작성이 완료된 글을 조회하는 요청입니다. 

<b>메이트찾기 글 수정하기</b> (api url : Patch. /api/mate/{post_id}) :

작성한 글을 수정 하는 요청입니다.

<b>메이트찾기 글 삭제하기</b> (api url : Delete. /api/mate/{post_id}) :

해당 글을 삭제하는 요청입니다.

<b>메이트찾기 글 목록 조회</b> (api url : Get. /api/mate/list) :

메이트찾기에 올라온 글들의 목록을 생성 및 최근 등록일자 순으로 조회합니다. 한번에 10개의 글을 불러옵니다.

<b>목록 조회 시 필터링 기능</b> (api url : Get. /api/mate/list?) :

위의 목록을 조회할 때 다음의 필터링 조건을 붙여 목록을 가져올 수 있습니다.

- position : GUARD, FORWARD, CENTER 중 하나를 정해 해당 포지션의 빈 자리가 남은 글들을 불러옵니다.(ex. list?position=GUARD)

- location : 해당 지역에서 모이는 글들을 불러옵니다.(ex. list?location=서울)

- skillLevel : BEGINNER, LOW, MIDDLE, HIGH 중 하나를 정해 해당 실력을 모집하는 글들을 불러옵니다.(ex. list?skillLevel=BEGINNER)

- includingExpired : 약속 시작 시간을 경과 했는지 여부에 따라 글들을 불러옵니다.(기본값은 false. 시간이 경과하지 않은 글들만 불러옴. ex. list?includingExpired=true)

- cursorTime : 커서 페이징을 위한 쿼리 파라미터. 응답으로 보낼 커서 페이징 목록 JSON 객체 내 nextCursor의 값을 목록의 다음 페이지를 불러올때 cursorTime의 값으로 지정합니다.(ex. list?cursorTime=2024-02-26T05:07:35.709673)



<b>신청자 신청하기</b>(api url : Post. api/mate/{post_id}/participants/register):

해당 글에 지원하고 싶은 사람이 신청을 요청합니다.

<b>신청자 취소하기</b>(api url : Patch. api/mate/{post_id}/participants/{participant_table_id}?applyStatus=CANCELED):

해당 글에 신청하여 대기중인 신청자가 신청을 취소하는 요청입니다.

<b>신청자 승낙하기</b>(api url : Patch. api/mate/{post_id}/participants/{participant_table_id}?applyStatus=ACCEPTED):

해당 글의 작성자가 대기중인 신청자를 승낙합니다.

<b>신청자 거절하기</b>(api url : Patch. api/mate/{post_id}/participants/{participant_table_id}?applyStatus=REJECTED):

해당 글의 작성자가 대기중인 신청자를 거절합니다.

<b>메이트찾기 글 모집완료 하기</b>(api url : Patch. api/mate/{post_id}/complete):

해당 글의 작성자가 모집완료 요청을 서버에 보내명 1명 이상의 ACCEPTED 상태인 신청자들을 채팅방으로 연결하고 해당 글의 상태는 모집완료('COMPLETED')로 바뀝니다. 다른 신청자들은 신청자 목록에서 지워집니다.


## 2.상대팀 매칭
<b>상대팀 매칭 글 등록하기</b>(api url : Post. api/match/register):

상대팀 매칭 모집 글을 작성하여 등록합니다.

<b>상대팀 매칭 글 조회하기</b>(api url : Get. api/match/read/{post_id}):

등록한 글을 조회하는 기능입니다.

<b>상대팀 매칭 글 수정하기</b>(api url : Patch. api/match/{post_id}):

등록한 글을 수정하는 기능입니다. 

<b>상대팀 매칭 글 삭제하기</b>(api url : Delete. api/match/{post_id}):

등록한 글을 삭제하는 기능입니다. 글의 모집 상태가 CANCELED로 전환됩니다.

<b>상대팀 매칭 글 목록 조회</b>(api url : Patch. api/match/list):

상대팀 매칭에 올라온 글들의 목록을 생성 및 최근 등록일자 순으로 조회합니다. 한번에 10개의 글을 불러옵니다.

<b>목록 조회 시 필터링 기능</b>(api url : Patch. api/match/list?):

위의 목록을 조회할 때 다음의 필터링 조건을 붙여 목록을 가져올 수 있습니다.

- nov : 3vs3 또는 5vs5 등 대결 팀 인원 수를 조건으로 하여 글을 불러옵니다.(ex. list?nov=3)

- location : 해당 지역에서 모이는 글들을 불러옵니다.(ex. list?location=서울)

- skillLevel : BEGINNER, LOW, MIDDLE, HIGH 중 하나를 정해 해당 실력을 모집하는 글들을 불러옵니다.(ex. list?skillLevel=BEGINNER)

- includingExpired : 약속 시작 시간을 경과 했는지 여부에 따라 글들을 불러옵니다.(기본값은 false. 시간이 경과하지 않은 글들만 불러옴. ex. list?includingExpired=true)

- cursorTime : 커서 페이징을 위한 쿼리 파라미터. 응답으로 보낼 커서 페이징 목록 JSON 객체 내 nextCursor의 값을 목록의 다음 페이지를 불러올때 cursorTime의 값으로 지정합니다.(ex. list?cursorTime=2024-02-26T05:07:35.709673)


<b>신청자 신청하기</b>(api url : Post. api/match/{post_id}/apply):

해당 팀 모집 글에 유저가 신청하는 요청입니다.

<b>신청자 취소하기</b>(api url : Patch. api/match/{post_id}/apply/{teamApplicant_id}?applyStatus=CANCELED):

신청자 목록에 등재된 신청자가 취소를 하는 요청입니다. 똑같은 모집 글에 다시 지원이 가능합니다.

<b>신청자 승낙하기</b>(api url : Patch. api/match/{post_id}/apply/{teamApplicant_id}?applyStatus=ACCEPTED)):

신청자 목록에 존재하는 해당 신청자를 수락(ACCEPTED) 합니다. 이 이후엔 다른 신청자를 수락 할수 없습니다.

<b>신청자 거절하기</b>(api url : Patch. api/match/{post_id}/apply/{teamApplicant_id}?applyStatus=REJECTED)):

WAITING 상태인 신청자를 거절 합니다. 해당 신청자는 거절된 상태로 남아 다시 지원 할 수 없습니다.

<b>상대팀매칭 글 모집완료 하기</b>(api url : Patch. api/match/{post_id}/complete):

모집이 확정된 신청자를 상대로 확정하고 글을 모집완료 상태로 전환합니다.

# 회고
- 팀원과 노션과 슬랙, 젭 등을 활용하여 원활한 소통 및 힘들어도 꾸준히 활동하면서 많은 것을 배울 수 있었습니다.
- 현재 동시성 및 리액티브 프로그래밍을 공부하고 있는데 학습 후에 동시성을 고려한 웹 어플리케이션을 만들 계획입니다.
- 객체 타입의 변수의 null 안정성을 확보 하도록 코드를 짜기 위해 신경을 더 쓰고 유저의 테이블 ID를 UUID로 설계 하는 등 이번 프로젝트를 통해서 느낀 개선점을 차기 프로젝트 또는 실무에 보탬이 되도록 반영하고 싶습니다.
- 도메인 객체에서 다른 계층이나 도메인에 의존하지 않기 위해 여러 도메인을 활용한 기능 구현은 도메인 서비스 클래스를 따로 만들어서 그 곳에서 이루어지도록 코드를 작성할 계획입니다.

   
# 구현 스크린샷

![스크린샷 2024-02-25 151559](https://github.com/red481/slam-talk-backend/assets/72694104/c645ae40-bf13-4a35-b162-d953c5cc2376)
![스크린샷 2024-02-21 234845](https://github.com/red481/slam-talk-backend/assets/72694104/23d0728b-1cd7-47d9-8291-6975ad52d8f8)
![스크린샷 2024-02-25 151709](https://github.com/red481/slam-talk-backend/assets/72694104/ddbc39d9-f369-42af-9a6a-f73d57ab21a8)

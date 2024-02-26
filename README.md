# slam-talk-BackEnd
본 웹 어플리케이션은 농구장 시설 위치를 카카오 지도에 등록하여 원하는 장소에서 사람들이 팀원이나 상대팀을 구할 수 있고 채팅을 통해 농구장 시설이나 매칭 관련 의견과 대화를 나눌 수 있는 웹앱 입니다. 또한 자유게시판, 대관양도, 중고거래 등 관심 있는 사람들이 모여 형성하는 커뮤니티도 존재합니다.

# 프로젝트 내 담당 파트(메이트 찾기, 상대팀 매칭)
원하는 장소에서 함께 농구를 할 팀원을 구하는 기능인 '메이트 찾기'와 3vs3 또는 5vs5 등 원하는 형태의 팀 구성으로 함께 붙을 상대팀을 구하는 '상대팀 매칭' 기능을 구현함.

개발 환경 : Java 17, SpringBoot 3.2.1

사용 스텍 : Data JPA, QueryDSL, h2, MySQL




### 매칭 로직 설명
![application_algorithm](https://github.com/red481/slam-talk-backend/assets/72694104/46ac5bb1-5f4e-42f1-af87-5cea459c1350)

매칭 모집 글은 모집중('RECRUITING')과 모집완료('COMPLETED')의 상태를 가짐.

신청자는 대기중('WAITING'), 취소('CANCELED'), 승낙('ACCEPTED'), 거절('REJECTED')의 상태를 가짐.

<b>모집중</b>('RECRUITING') : 글 작성자가 모집 글을 게시하는 순간부터 모집완료 버튼을 누르기 전까지의 상태를 의미함. 이 상태에선 모든 사람이 해당 글에 신청할 수 있고 취소할 수 있으며 글 작성자가 승낙, 또는 거절을 할 수 있음.

<b>모집완료</b>('COMPLETED') : 모집이 완료된 글을 의미함. 모집 완료를 활성화 하면 ACCEPTED 상태인 신청자들을 제외하고 나머지 신청자('REJECTED' OR 'WAITING')들을 신청자 목록에서 삭제하며(soft delete) COMPLETED 상태로 글의 상태를 전환함.



<b>대기중</b>('WAITING') : 신청자가 해당 모집 글에 지원 신청하여 신청자 목록에 등록된 상태. 

<b>취소</b>('CANCELED') : 신청자가 취소를 하면 신청자 목록에서 제거되고 다시 신청할 수 있음. WAITING 상태인 신청자만 선택할 수 있음.

<b>승낙</b>('ACCEPTED') : 해당 글 작성자가 대기중인 신청자를 승낙하면 신청자는 승낙 상태('ACCEPTED')에 놓임. WAITING 상태인 신청자만 이 상태로 바뀔 수 있음.

<b>거절</b>('REJECTED') : 해당 글 작성자가 대기중인 신청자를 거절하면 신청자는 거절 상태('REJECTED')에 놓임. WAITING 상태인 신청자만 이 상태로 바뀔 수 있음.


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

<b>상대팀 매칭 글 조회하기</b>(api url : Get. api/match/read/{post_id}):

<b>상대팀 매칭 글 수정하기</b>(api url : Patch. api/match/{post_id}):

<b>상대팀 매칭 글 삭제하기</b>(api url : Delete. api/match/{post_id}):

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

<b>신청자 취소하기</b>(api url : Patch. api/match/{post_id}/apply/{teamApplicant_id}?applyStatus=CANCELED):

<b>신청자 승낙하기</b>(api url : Patch. api/match/{post_id}/apply/{teamApplicant_id}?applyStatus=ACCEPTED)):

<b>신청자 거절하기</b>(api url : Patch. api/match/{post_id}/apply/{teamApplicant_id}?applyStatus=REJECTED)):

<b>상대팀매칭 글 모집완료 하기</b>(api url : Patch. api/match/{post_id}/complete):


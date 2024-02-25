# slam-talk-BackEnd
본 웹 어플리케이션은 농구장 시설 위치를 카카오 지도에 등록하여 원하는 장소에서 사람들이 팀원이나 상대팀을 구할 수 있고 채팅을 통해 농구장 시설이나 매칭 관련 의견과 대화를 나눌 수 있는 웹앱.

# 프로젝트 내 담당 파트(메이트 찾기, 상대팀 매칭)
원하는 장소에서 함께 농구를 할 팀원을 구하는 기능인 '메이트 찾기'와 3vs3 또는 5vs5 등 원하는 형태의 팀 구성으로 함께 붙을 상대팀을 구하는 '상대팀 매칭' 기능을 구현함.

사용 스텍 : Data JPA, QueryDSL, h2, MySQL


### 매칭 로직 설명
![application_algorithm](https://github.com/red481/slam-talk-backend/assets/72694104/bf81f520-0b4e-490e-98ee-f5d40b17a53f)

매칭 모집 글은 모집중('RECRUITING')과 모집완료('COMPLETED')의 상태를 가짐.

신청자는 대기중('WAITING'), 취소('CANCELED'), 승낙('ACCEPTED'), 거절('REJECTED')의 상태를 가짐.

모집중('RECRUITING') : 글 작성자가 모집 글을 게시하는 순간부터 모집완료 버튼을 누르기 전까지의 상태를 의미함. 이 상태에선 모든 사람이 해당 글에 신청할 수 있고 취소할 수 있으며 글 작성자가 승낙, 또는 거절을 할 수 있음.

모집완료('COMPLETED') : 모집이 완료된 글을 의미함. 모집 완료를 활성화 하면 ACCEPTED 상태인 신청자들을 제외하고 나머지 신청자('REJECTED' OR 'WAITING')들을 신청자 목록에서 삭제하며(soft delete) COMPLETED 상태로 글의 상태를 전환함.



대기중('WAITING') : 신청자가 해당 모집 글에 지원 신청하여 신청자 목록에 등록된 상태. 

취소('CANCELED') : 신청자가 취소를 하면 신청자 목록에서 제거되고 다시 신청할 수 있음. WAITING 상태인 신청자만 선택할 수 있음.

승낙('ACCEPTED') : 해당 글 작성자가 대기중인 신청자를 승낙하면 신청자는 승낙 상태('ACCEPTED')에 놓임. WAITING 상태인 신청자만 이 상태로 바뀔 수 있음.

거절('REJECTED') : 해당 글 작성자가 대기중인 신청자를 거절하면 신청자는 거절 상태('REJECTED')에 놓임. WAITING 상태인 신청자만 이 상태로 바뀔 수 있음.


## 1.메이트찾기
메이트찾기 글 등록하기 (api url : Post /api/mate/register) :


메이트찾기 글 조회하기 (api url : Get /api/mate/read/{post_id}) :

메이트찾기 글 수정하기 (api url : Patch /api/mate/{post_id}) :

메이트찾기 글 삭제하기 (api url : delete /api/mate/{post_id}) :

메이트찾기 글 목록 조회 (api url : Get /api/mate/list) :

목록 조회 시 필터링 기능 (api url : Get /api/mate/list?) :

신청자 신청하기
신청자 취소하기
신청자 승낙하기
신청자 거절하기
메이트찾기 글 모집완료 하기

## 2.상대팀 매칭
상대팀 매칭 글 등록하기
상대팀 매칭 글 조회하기
상대팀 매칭 글 수정하기
상대팀 매칭 글 삭제하기
상대팀 매칭 글 목록 조회
목록 조회 시 필터링 기능
신청자 신청하기
신청자 취소하기
신청자 승낙하기
신청자 거절하기
상대팀매칭 글 모집완료 하기


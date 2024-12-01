# 📖Marimo
학생들이 독서를 보다 즐겁고 몰입감 있게 체험할 수 있는 참여형 독서 교육 플랫폼

## 📜 1. 프로젝트 소개
<div align="center">
  <img width="500" alt="image" src="https://github.com/user-attachments/assets/9225d8fc-837d-45e0-845d-cfac5a55de71">
</div>

온작품읽기는 초등학교 정규 교육 과정으로, 책을 읽고 여러 참여형 활동을 함께 하며 책 내용을 더 깊게 이해하도록 돕는 수업입니다.</br>
온작품읽기를 학교 모바일 기기를 활용하여 수업에 활용할 수 있도록 메타버스 플랫폼을 구현했습니다. AI기술을 활용하여 선생님은 간단하게 수업을 준비하고 활동 결과를 확인할 수 있습니다. 학생은 자신이 그린 아바타로 창의적이고 몰입감있는 활동을 할 수 있습니다.

### 📺 1-1. 마리모 소개 영상(이미지 클릭!)
<div align="center">
  <a href="https://youtu.be/iIQx2r_dEj0">
    <img src="https://github.com/user-attachments/assets/9b98fbdc-9513-4d6b-9a89-fd384a069fff" alt="마리모 썸네일" width="700"/>
  </a>
</div>

## 👥 2. 팀원 소개

|                                               [XR]용도원                                               |                                               [XR]공혜지                                               |                                               [XR]박효근                                                |                                               [TA]박소현                                                |                                               [AI]박예지                                                |                                               [AI]손수연                                                |                                               [BE]정현민                                                |
|:-----------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------:|
| <img width="107" alt="용도원 마리모" src="https://github.com/user-attachments/assets/6afc1604-2baa-4608-bb70-96e62daef287"> | <img width="106" alt="공혜지 마리모" src="https://github.com/user-attachments/assets/69087b21-926c-48a5-8732-ca203392b683"> | <img width="112" alt="박효근 마리모" src="https://github.com/user-attachments/assets/96016f29-ed42-4fdd-a9da-50b4c32bc922"> | <img width="104" alt="박소현 마리모" src="https://github.com/user-attachments/assets/d5d58006-b7b7-499b-99db-59a91a2f9b1d"> | <img width="106" alt="박예지 마리모" src="https://github.com/user-attachments/assets/7dbbeaaa-cfc4-4fb1-ada5-0922d9d8ab6f"> | <img width="106" alt="손수연 마리모" src="https://github.com/user-attachments/assets/2242a533-a60c-47c2-b79d-74a993252ba2"> | <img width="107" alt="정현민 마리모" src="https://github.com/user-attachments/assets/73fc7216-0dda-418a-900f-2dbcf7a5cfbb"> |
|                             [@anditsoon](https://github.com/anditsoon)                              |                           [@hzkkong](https://github.com/hzkkong)                            |                             [@RootPHG](https://github.com/RootPHG)                             |                              [@goongoontroli](https://github.com/goongoontroli)                         |                             [@yeji79](https://github.com/yeji79)                             |                         [@giraffeleg](https://github.com/giraffeleg)                         |                               [@JungHyeonmin](https://github.com/JungHyeonmin)                               |

## 👨‍💻 3. 프로젝트 환경

### 🛠️ 3-1. 기술 스택
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/jpa-59666C?style=for-the-badge&logo=hibernate&logoColor=white">
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
</div>

### 🚀 3-2. 배포
<div align=center> 
  <img src="https://img.shields.io/badge/amazonaw ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/amazonaw rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
  <img src="https://img.shields.io/badge/amazonaw s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/GitHub Action-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
</div>

### 🤝 3-3. 협업 툴
<div align=center> 
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
  <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
</div>

## 🎯 4. 담당 역할
- AI와 Unity 간의 통신 구현
- 선생님 수업 자료 제작, 수업 결과 도메인 구현
- GitHub Action을 이용한 CI/CD 구현
- AWS EC2, Docker를 이용한 배포 서버와 DB 구축 및 관리
- API 사용의 편의성을 위한 Swagger docs를 이용한 API 문서화
  
## 🌐 5. 프로젝트 아키텍처
### 🏗️ 5-1. Service Architecture
<div align="center">
  <img src="https://github.com/user-attachments/assets/8f84817b-ebbc-4770-a746-1043f7043453" alt="전체 아키텍처" width="900"/>
</div>

### 5-2. 아키텍처 [상세](https://github.com/MTVS3rd-MariMo/Marimo-BE.wiki.git)

### 5-3. Context Map [상세](https://github.com/MTVS3rd-MariMo/Marimo-BE.wiki.git)

### 5-4. Aggregate
<div align="center">
  <img src="https://github.com/user-attachments/assets/cae38d98-acf9-4f01-80c1-93df41d5bc26" alt="도메인 주도 설계" width="700"/>
</div>

## ✨ 6. 기능 소개
### 🔑 6-1. 회원가입, 로그인
- 선생님, 학생을 선택한 뒤 학교, 학년, 반, 학생 번호, 이름, 비밀번호로 회원가입한다.
- 이름, 비밀번호로 로그인한다.
### 📚 6-2. 온작품읽기 자료 제작, 수정, 삭제
- 선생님이 책 제목, 저자, pdf를 등록하면 pdf를 AI서버로 전송한다. 내용을 기반으로 AI가 열린 질문 2개, 퀴즈 8개, 등장인물 4명, 단체사진 배경 1개를 생성한다.
  - 선생님은 생성된 온작품읽기 자료를 확인하고 열린 질문 2개, 퀴즈 2개를 선택하고 수정한 뒤, 저장한다.
- 선생님은 기존에 생성한 온작품읽기 자료를 수정, 삭제할 수 있다.
### 🏫 6-3. 수업 생성
- 저장된 온작품읽기 자료를 이용해서 온작품읽기 월드를 생성한다.
### 👩‍🎨 6-4. 아바타 생성
- 학생이 그린 아바타를 AI 서버로 전송하고 AI 서버에서 아바타 이미지에 맞는 애니메이션 2개와 크기가 수정된 아바타 이미지를 받는다.
  - 서버에서 zip파일 저장 후 압축 해제한 뒤 이미지와 애니메이션을 하나의 폴더로 저장한다.
- 수업에 참가한 다른 학생의 아바타 이미지와 애니메이션을 다운받는다.
### ❓ 6-5. 온작품읽기 활동 1 : 열린 질문
- 열린 질문에 답변을 저장한다.
### 🎭 6-6. 온작품읽기 활동 2 : 핫시팅
- 각 역할 몰입하여 자기소개를 작성한 뒤 저장한다.
- 자기소개를 바탕으로 학생들의 질의응답을 진행한다.
  - 각 질의응답을 wav파일로 AI서버로 전송하면 STT을 이용하여 서버에서 자기소개에 맞는 질의응답 저장
### 📷 6-7. 온작품읽기 활동 3 : 단체사진
- 단체사진을 저장한다.
### 🖼️ 6-8. 학생 단체사진 조회
- 참가한 수업을 조회하고 수업에서 찍은 단체사진을 확인한다.
### 🗃️ 6-9. 선생님 수업 기록 조회
- 참가한 수업을 조회하고 학생 활동기록을 조회한다.

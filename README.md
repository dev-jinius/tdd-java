# TDD - Java 기능 구현

## 요구 사항

- [PATCH] `/point/{id}/charge` : 포인트를 충전한다.
- [PATCH] `/point/{id}/use` : 포인트를 사용한다.
- [GET] `/point/{id}` : 포인트를 조회한다.
- [GET] `/point/{id}/histories` : 포인트 내역을 조회한다.
- 잔고가 부족할 경우, 포인트 사용은 실패하여야 합니다.
- 동시에 여러 건의 포인트 충전, 이용 요청이 들어올 경우 순차적으로 처리되어야 합니다.

## TDD 설계

### 1. 기능 분석

- _`특정 유저의 포인트를 조회하는 기능`_
- _`특정 유저의 포인트 충전/이용 내역을 조회하는 기능`_
- _`특정 유저의 포인트를 충전하는 기능`_
- _`특정 유저의 포인트를 사용하는 기능`_

### 2. 프로젝트 구조 설계

- src.main.java.io.hhplus.tdd.point
  - `Controller`
    - **PointController.java**
  - `Entity`
    - **UserPoint.java**
    - **PointHistory.java**
  - `Enum`
    - **TransactionType.java**
- src.main.java.io.hhplus.tdd.database
  - `DB`
    - **UserPointTable**
    - **PointHistoryTable**
- src.main.java.io.hhplus.tdd.exception
  - **TddCustomException.java**
- src.main.java.io.hhplus.tdd.point.service
  - `Service`
    - **PointService.java**
    - **ChargeService.java**
    - **HistoryService.java**
    - **UseService.java**
- src.main.java.io.hhplus.tdd.point.dto

  - `DTO`
    - **UserPointDto.java**
    - **PointHistoryDto.java**

- src.test.java.io.hhplus.tdd.point.service
  - `Test`
    - **PointServiceTest.java**
    - **ChargeServiceTest.java**
    - **HistoryServiceTest.java**
    - **UseServiceTest.java**

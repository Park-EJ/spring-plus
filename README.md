# SPRING PLUS

## EC2
![EC2](https://github.com/user-attachments/assets/5110a8fc-1c8d-4539-8aee-cc175adfcf92)

## RDS
![RDS](https://github.com/user-attachments/assets/b52f31a4-a7b0-4bda-98e6-d5a1cde07e92)

## healthCheck API
![healthCheck](https://github.com/user-attachments/assets/1dc691d8-38e7-4c55-917f-88cb690dd04f)

## 트러블 슈팅 
- AOP User ID null
![AOP userId null](https://github.com/user-attachments/assets/fff8de43-44dc-4661-9864-ccfa7ba2f49e)

● 배경
: AdminAccessLoggingAspect - logBeforeChangeUserRole 메서드 실행 시 로그에 User ID : null 발생

● 원인
: Spring Security 설정 후 HttpServletRequest에서 getAttribute("userId")로 정보를 가져오지 못함

● 해결방안 1
: SecurityContextHolder를 활용하여 userId를 로그에 반환하도록 수정한다.
AdminAccessLoggingAspect에서 HttpServletRequest request.getAttribute("userId")를 사용하여 userId를 가져오고 있지만, 이는 JwtAuthenticationFilter에서 request.setAttribute("userId", userId)를 설정하지 않으면 null이 된다.
따라서, Spring Security로 변경 후에는 SecurityContextHolder를 활용하여 userId를 가져오는 방식으로 수정한다.

● 해결방안 2
: Spring Security로 변경 후 UserAdminController에서 @AuthenticationPrincipal AuthUser authUser을 파라미터로 받는다. 그리고 authUser를 통해서 userId를 조회한다.

String userId = String.valueOf(authUser.getId());


## Spring Security 관련 추가 학습 내용
(1) 코드 동작 순서
- 사용자가 로그인 후 JWT를 발급받음
- 사용자는 JWT를 포함하여 요청을 보냄 (Authorization: Bearer <TOKEN>)
- Spring Security의 JwtAuthenticationFilter가 요청을 가로챔
- JWT를 검증하고, 사용자 정보를 SecurityContextHolder에 저장
- 요청이 컨트롤러로 전달되고, @AuthenticationPrincipal 등을 사용하여 인증된 사용자 정보를 활용

(2) 권한 설정(ADMIN)
- 1번 방법) SecurityConfig에서 /admin/** 경로를 ADMIN 전용으로 설정
.requestMatchers("/admin/**").hasAuthority(UserRole.Authority.ADMIN) 

- 2번 방법) UserAdminService에서 @PreAuthorize 또는 @Secured 사용
.@PreAuthorize("hasAuthority('ROLE_ADMIN')")



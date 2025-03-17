package org.example.expert.config;

import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthUser authUser;

    // Spring Security가 사용자 정보를 관리할 수 있도록 함(즉, 인증 정보 관리)
    // JwtAuthenticationToken을 생성하여 사용자 정보를 담는 Authentication 객체(3번 과정 중)
    // 인증된 사용자의 ROLE_USER, ROLE_ADMIN 등의 권한(authorities)을 제공
    public JwtAuthenticationToken(AuthUser authUser) {
        super(authUser.getAuthorities());
        this.authUser = authUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authUser;
    }
}

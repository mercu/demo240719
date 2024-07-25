package com.mercu.demo240719.config.security;

import com.mercu.demo240719.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//Spring Security "UserDetails"의 구현 클래스. 사용자의 정보를 나타냄
//User 객체의 정보로 사용자 인증 및 인가(권한부여)처리
@Getter
public class MyUserDetails implements UserDetails {
    private final User user;
    private Collection<? extends GrantedAuthority> authorities; //사용자 권한 목록을 담은 GrantedAuthority의 컬렉션

    //생성자 직접 작성, 필드초기화
    public MyUserDetails(User user) {
        this.user = user;
        this.authorities = defaultAuthorities();
    }

    //기본 권한 설정

    //제네릭 타입 사용으로 Collection 인터페이스 확장해서 특정 타입 개채를 담을 수 있도록 정의
    private Collection<? extends GrantedAuthority> defaultAuthorities() {
        //제네릭 타입 제한, GrantedAuthority, GrantedAutority 하위 클래스를 포함하게 Collection 지정
        List<String> roles = new ArrayList<>(); //roll 초기화. 사용자 권한을 문자열로 담기 위함.
        roles.add("ROLE_USER"); //기본 권한으로 ROLE_USER 설정
        //이후 role 을 나눈다면 코드 추가해서 사용 가능

        return roles.stream() //stream으로 반환
                .map(SimpleGrantedAuthority::new) //문자열을 SimpleGrantedAuthority(GrantedAuthority 인터페이스 구현체) 객체로 매핑
                .collect(Collectors.toList()); //매핑된 객체들을 다시 리스트로 수집해서 반환.
    }

    //정적 팩토리 메소드. 사용자 ID, PW, 권한(roll)을 인자로 받아 MyUserDetails 객체 생성
    public static UserDetails create(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        //User를 builder 사용해서 생성하고 MyUserDetails 객체 초기화
        //UserDetails : SpringSecurity의 인터페이스. 사용자 정보 제공 메소드 정의
        MyUserDetails myUserDetails = new MyUserDetails(User.builder()
                .name(username)
                .password(password)
                .build()); //User Entity 빌더 호출. User 객체 생성
        myUserDetails.authorities = authorities; //생성된 MyUserDetails 객체의 authorites 필드를 위에서 메소드 인자로 받은 authorites로 설정
        return myUserDetails;
    }


    @Override
    //사용자 권한 목록 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    //사용자 패스워드 반환
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    //userId 반환
    public String getUsername() {
        return user.getName();
        //UserId가 userName과 동일
    }


    //사용자 계정 상태 나타냄. 모든 계정 활성화 상태로 설정.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
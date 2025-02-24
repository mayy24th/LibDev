package com.example.LibDev.auth.service;

import com.example.LibDev.auth.dto.CustomOAuth2User;
import com.example.LibDev.auth.dto.OAuth2UserDto;
import com.example.LibDev.auth.dto.oauth2.NaverUserInfo;
import com.example.LibDev.auth.dto.oauth2.OAuth2UserInfo;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.entity.type.Role;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;

        if(registrationId.equals("naver")) {
            log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        } else {
            //추후 nullPointException 처리 해야함
            return null;
        }

        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        String phone = oAuth2UserInfo.getPhone();

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .password("OAuth2")
                    .phone(phone)
                    .role(Role.USER)
                    .borrowAvailable(true)
                    .penaltyExpiration(null)
                    .withdraw(false)
                    .build();

            userRepository.save(newUser);
            return new CustomOAuth2User(
                    OAuth2UserDto.builder()
                            .email(newUser.getEmail())
                            .name(newUser.getName())
                            .role(newUser.getRole())
                    .build());
        }

        return new CustomOAuth2User(
                OAuth2UserDto.builder()
                        .email(user.get().getEmail())
                        .name(user.get().getName())
                        .role(user.get().getRole())
                        .build()
        );
    }

}

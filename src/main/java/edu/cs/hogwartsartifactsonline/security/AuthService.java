package edu.cs.hogwartsartifactsonline.security;

import edu.cs.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import edu.cs.hogwartsartifactsonline.hogwartsuser.MyUserPrincipal;
import edu.cs.hogwartsartifactsonline.hogwartsuser.converter.UserToUserDtoConverter;
import edu.cs.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
//        create user info
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

//        create a JWT
        String token = this.jwtProvider.createToken(authentication);
        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}


/**
 * headers -
 * eyJhbGciOiJSUzI1NiJ9.
 *
 * 5 claim set payload eyJpc3MiOiJzZWxmIiwic3ViIjoiam9obiIsImV4cCI6MTc0MzQxMTIzOCwiaWF0IjoxNzQzNDA0MDM4LCJhdXRob3JpdGllcyI6IlJPTEVfYWRtaW4gUk9MRV91c2VyIn0.
 * signature
 * KFu_nvdb7-W5xBwHAXHxBYvCrxj0q2g8l9yochLw7MByFB5h6QBxbwQPZ1gINIM9D4H8g_YddnYlkrL8ikuHl3FrbWMZliEbs5frct9FNgl4Bjx3Ow3Jwi6I6DTSfTjE2fx9P_9AGAa-vtTn2Wn94hZ0V-HZzaPPqaZ9NmdAI-foetoP5ozJmDakL4aSLasEx_sWWCxk8QBqaHGo28pC9CqmM1mg8RTFec0Dp-B2grCYHdxza3uSQadWnTTjyM9o5vpJU3SVGMA3BgWDARFEWMxWK9NIR94ypdQ5erDiEYW09v_W1cEpRgjX7DK-3xsyK0SHa6cJieFxVFU8I8r0xA
 * */
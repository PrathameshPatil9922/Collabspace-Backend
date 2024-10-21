package com.CollabSpace.authService.controller;


import com.CollabSpace.authService.dtos.*;
import com.CollabSpace.authService.entities.User;
import com.CollabSpace.authService.security.JwtHelper;
import com.CollabSpace.authService.service.RefreshTokenService;
import com.CollabSpace.authService.service.UserService;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    //method to generate token:

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper modelMapper;

  //  @Value("${app.google.client_id}")
   // private String googleClientId;

  //  @Value("${app.google.default_password}")
   // private String googleProviderDefaultPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/regenerate-token")
    public ResponseEntity<JwtResponse> regenerateToken(@RequestBody RefreshTokenRequest request) {

        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(request.getRefreshToken());
        RefreshTokenDto refreshTokenDto1 = refreshTokenService.verifyRefreshToken(refreshTokenDto);
        UserDto user = refreshTokenService.getUser(refreshTokenDto1);
        String jwtToken = jwtHelper.generateToken(modelMapper.map(user, User.class));

        // apki choice refresh purana new bana lo
        JwtResponse response = JwtResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshTokenDto)
                .user(user)
                .build();
        return ResponseEntity.ok(response);


    }


    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        logger.info("Username {} ,  Password {}", request.getEmail(), request.getPassword());

        this.doAuthenticate(request.getEmail(), request.getPassword());

        User user = (User) userDetailsService.loadUserByUsername(request.getEmail());

        ///.. generate token...
        String token = jwtHelper.generateToken(user);
        //send karna hai response

        // Refresh Token

        RefreshTokenDto refreshToken = refreshTokenService.createRefreshToken(user.getEmail());


        JwtResponse jwtResponse = JwtResponse
                .builder()
                .token(token)
                .user(modelMapper.map(user, UserDto.class))
                .refreshToken(refreshToken)
                .build();


        return ResponseEntity.ok(jwtResponse);


    }

    private void doAuthenticate(String email, String password) {

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authentication);

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid Username and Password !!");
        }

    }

    //handle  login with google

    //    {idToken}
   /* @PostMapping("/login-with-google")
    public ResponseEntity<JwtResponse> handleGoogleLogin(@RequestBody GoogleLoginRequest loginRequest) throws GeneralSecurityException, IOException {
        logger.info("Id  Token : {}", loginRequest.getIdToken());

//        token verify

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory()).setAudience(List.of(googleClientId)).build();


        GoogleIdToken googleIdToken = verifier.verify(loginRequest.getIdToken());

        if (googleIdToken != null) {
            //token verified

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            String email = payload.getEmail();
            String userName = payload.getSubject();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            logger.info("Name {}", name);
            logger.info("Email {}", email);
            logger.info("Picture {}", pictureUrl);
            logger.info("Username {}", userName);


            UserDto userDto = new UserDto();
            userDto.setName(name);
            userDto.setEmail(email);
            userDto.setImageName(pictureUrl);
            userDto.setPassword(googleProviderDefaultPassword);
            userDto.setAbout("user is created using google ");
            userDto.setProvider(Providers.GOOGLE);
            //

            UserDto user = null;
            try {

                logger.info("user is loaded from database");
                user = userService.getUserByEmail(userDto.getEmail());

                // logic implement
                //provider
                logger.info(user.getProvider().toString());
                if (user.getProvider().equals(userDto.getProvider())) {
                    //continue
                } else {
                    throw new BadCredentialsException("Your email is already registered !! Try to login with username and password ");
                }


            } catch (ResourceNotFoundException ex) {
                logger.info("This time user created: because this is new user ");
                user = userService.createUser(userDto);
            }


            //
            this.doAuthenticate(user.getEmail(), userDto.getPassword());


            User user1 = modelMapper.map(user, User.class);


            String token = jwtHelper.generateToken(user1);
            //send karna hai response

            JwtResponse jwtResponse = JwtResponse.builder().token(token).user(user).build();

            return ResponseEntity.ok(jwtResponse);


        } else {
            logger.info("Token is invalid !!");
            throw new BadApiRequestException("Invalid Google User  !!");
        }
    }*/
}



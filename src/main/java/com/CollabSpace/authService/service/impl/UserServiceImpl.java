package com.CollabSpace.authService.service.impl;

import com.CollabSpace.authService.config.AppConstants;
import com.CollabSpace.authService.dtos.UserDto;
import com.CollabSpace.authService.entities.Role;
import com.CollabSpace.authService.entities.User;
import com.CollabSpace.authService.exception.ResourceNotFoundException;
import com.CollabSpace.authService.repositories.RoleRepository;
import com.CollabSpace.authService.repositories.UserRepository;
import com.CollabSpace.authService.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

  //  @Value("${user.profile.image.path}")
   // private String imagePath;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDto createUser(UserDto userDto) {
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);

        // dto->entity
        User user = dtoToEntity(userDto);
        //password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //get the normal role

        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setName("ROLE_" + AppConstants.ROLE_NORMAL);

        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(role);

        user.setRoles(List.of(roleNormal));
        User savedUser = userRepository.save(user);
        //entity -> dto
        UserDto newDto = entityToDto(savedUser);
        return newDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));
        user.setName(userDto.getName());
        //email update
        user.setInterests(userDto.getInterests());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
       // user.setImageName(userDto.getImageName());

        // assign normal role to user
        //by detail jo bhi api se user banega usko ham  log normal user banayenge

        //save data
        User updatedUser = userRepository.save(user);
        UserDto updatedDto = entityToDto(updatedUser);
        return updatedDto;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));


        //delete user profile image
        //images/user/abc.png
       /* String fullPath = imagePath + user.getImageName();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            logger.info("User image not found in folder");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        //delete user
        userRepository.delete(user);
    }


    private UserDto entityToDto(User savedUser) {

       /* UserDto userDto = UserDto.builder()
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .about(savedUser.getAbout())
                .gender(savedUser.getGender())
                .imageName(savedUser.getImageName())
                .build();*/

        return mapper.map(savedUser,UserDto.class);

    }

    private User dtoToEntity(UserDto userDto) {
      /*  User user = User.builder()
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .about(userDto.getAbout())
                .gender(userDto.getGender())
                .imageName(userDto.getImageName())
                .build();*/

        return mapper.map(userDto,User.class);
    }
}

package com.event.hab.profile.service;

import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.common.castomException.ProfileNotFoundException;
import com.event.hab.common.castomException.UserNotFoundException;
import com.event.hab.common.securityUtils.SecurityUtils;
import com.event.hab.profile.DTO.UpdateProfileRequest;
import com.event.hab.profile.DTO.UserProfileDetailsDTO;
import com.event.hab.profile.UserProfileMapper;
import com.event.hab.profile.model.UserProfile;
import com.event.hab.profile.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    final UserProfileRepository userProfileRepository;
    final UserProfileMapper userProfileMapper;
    final UserRepository userRepository;
    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userRepository = userRepository;
    }

    public void createProfileForUser(User user){
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setAllowMessages(true);
        userProfileRepository.save(userProfile);
    }

    public UserProfileDetailsDTO getProfile(long userId) {
       UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);
        return userProfileMapper.toDetailsDto(userProfile);
    }

    public UserProfileDetailsDTO getMyProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

         return userProfileMapper
                 .toDetailsDto(userProfileRepository
                         .findByUserId(user.getId())
                         .orElseThrow(ProfileNotFoundException::new));
    }

    public UserProfileDetailsDTO updateMyProfile(UpdateProfileRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        UserProfile userProfile =userProfileRepository.findByUserId(user.getId()).orElseThrow(ProfileNotFoundException::new);
        userProfileMapper.updateProfileFromDto(request,userProfile);
        userProfileRepository.save(userProfile);
        return userProfileMapper.toDetailsDto(userProfile);
    }
}

package com.event.hab.profile.controller;

import com.event.hab.profile.DTO.UserProfileDetailsDTO;
import com.event.hab.profile.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserProfileController {
    final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile/{userId}")
    public UserProfileDetailsDTO getProfileDetails(@PathVariable long userId){
        return userProfileService.getProfile(userId);
    }

    @GetMapping("/profile/me")
    public UserProfileDetailsDTO getMyProfile(){
        return userProfileService.getMyProfile();
    }

    @PutMapping("/profile/me")
    public UserProfileDetailsDTO updateMyProfile(){
        return userProfileService.updateMyProfile();
    }
}

package com.event.hab.profile.controller;

import com.event.hab.profile.DTO.UpdateProfileRequest;
import com.event.hab.profile.DTO.UserProfileDetailsDTO;
import com.event.hab.profile.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserProfileController {
    final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profiles/me")
    public UserProfileDetailsDTO getMyProfile(){
        return userProfileService.getMyProfile();
    }

    @PutMapping("/profiles/me")
    public UserProfileDetailsDTO updateMyProfile(@RequestBody UpdateProfileRequest request){
        return userProfileService.updateMyProfile(request);
    }

    @GetMapping("/profiles/{userId}")
    public UserProfileDetailsDTO getProfileDetails(@PathVariable Long userId){
        return userProfileService.getProfile(userId);
    }


}

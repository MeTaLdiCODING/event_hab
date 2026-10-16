    package com.event.hab.profile.DTO;
    import jakarta.validation.constraints.Size;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UpdateProfileRequest {
        @Size(max = 500)
        private String bio;
        @Size(max = 2000)
        private String avatarUrl;
        private Boolean allowMessages;
    }

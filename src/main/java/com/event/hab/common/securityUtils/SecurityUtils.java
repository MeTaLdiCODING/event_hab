package com.event.hab.common.securityUtils;
import com.event.hab.common.castomException.AuthenticationRequiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    public static String getCurrentUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationRequiredException();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            throw new AuthenticationRequiredException();
        }
        return userDetails.getUsername();
//крч дальше места в ивсервисе переэтовать с этой хренью а потом глобал обработчик чик пик чик
    }
}

package de.freeschool.api.controller;

import de.freeschool.api.exception.Fail;
import de.freeschool.api.exception.MessageException;
import de.freeschool.api.models.UserEntity;
import de.freeschool.api.models.type.RoleType;
import de.freeschool.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MainController {


    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserRepository userRepository;

    /**
     * Returns the URL of a different endpoint on the current API
     *
     * @param endpointName name of the other endpoint
     * @return
     */
    protected static String getApiURL(String endpointName) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String currentURL = attributes.getRequest().getRequestURL().toString();
        currentURL.lastIndexOf("/");
        currentURL = currentURL.substring(0, currentURL.lastIndexOf("/"));
        return currentURL + "/" + endpointName;
    }

    String message(String identifier) {
        // Retrieve message string. Use the identifier as fallback if it cannot be found.
        // (this behavior is used, e.g., for the maintenance mode message)
        return messageSource.getMessage(identifier, null, identifier, LocaleContextHolder.getLocale());
    }

    String message(String identifier, Object[] vars) {
        return messageSource.getMessage(identifier, vars, LocaleContextHolder.getLocale());
    }


    protected void responseFailMessage(String identifier) {
        throw new MessageException(message(identifier, null));
    }

    protected void responseFailMessage(Fail fail) {
        throw new MessageException(message(fail.getIdentifier(), fail.getVars()));
    }

    /**
     * useable: responseFailMessage("account.getExchanges.notOwner", new Object[] {accountId});
     *
     * @param identifier
     * @param vars
     */
    protected void responseFailMessage(String identifier, Object[] vars) {
        throw new MessageException(message(identifier, vars));
    }


    protected UserEntity getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        if (principal.equals("anonymousUser")) {
            throw new MessageException(message("user.notLoggedIn"));
        }
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    protected boolean isAdmin() {
        return getUser().getRoles().stream().anyMatch(role -> role.getName().equals(RoleType.ADMIN));
    }


}

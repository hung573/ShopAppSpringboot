/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.components;

import ShopApp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

/**
 *
 * @author mac
 */

@RequiredArgsConstructor
@Component
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    
    public String getLocalizedMessage(String messageKey, Object... params){//spread operator
        HttpServletRequest httpServletRequest = WebUtils.getCurrentRequest();
        Locale locale = localeResolver.resolveLocale(httpServletRequest);
        return messageSource.getMessage(messageKey,params, locale);
    }
}

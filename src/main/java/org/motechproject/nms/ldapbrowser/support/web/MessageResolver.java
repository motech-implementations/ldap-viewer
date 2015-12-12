package org.motechproject.nms.ldapbrowser.support.web;

import org.springframework.context.MessageSource;
import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;

import java.util.Locale;

public class MessageResolver implements IMessageResolver {

    private MessageSource messageSource;

    @Override
    public String getName() {
        return "SPRING 2.5 RESOLVER";
    }

    @Override
    public Integer getOrder() {
        return 0;
    }

    @Override
    public MessageResolution resolveMessage(Arguments arguments, String key, Object[] messageParameters) {
        Locale locale = arguments.getContext().getLocale();
        String msg = messageSource.getMessage(key, messageParameters, locale);
        return new MessageResolution(msg);
    }

    @Override
    public void initialize() {
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}

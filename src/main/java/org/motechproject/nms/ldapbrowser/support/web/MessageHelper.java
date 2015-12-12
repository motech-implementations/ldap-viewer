package org.motechproject.nms.ldapbrowser.support.web;

import org.thymeleaf.context.WebContext;

import static org.motechproject.nms.ldapbrowser.support.web.Message.MESSAGE_ATTRIBUTE;

public final class MessageHelper {

    private MessageHelper() {
    }

    public static void addSuccessAttribute(WebContext webCtx, String message, Object... args) {
        addAttribute(webCtx, message, Message.Type.SUCCESS, args);
    }

    public static void addErrorAttribute(WebContext webCtx, String message, Object... args) {
        addAttribute(webCtx, message, Message.Type.DANGER, args);
    }

    public static void addInfoAttribute(WebContext webCtx, String message, Object... args) {
        addAttribute(webCtx, message, Message.Type.INFO, args);
    }

    public static void addWarningAttribute(WebContext webCtx, String message, Object... args) {
        addAttribute(webCtx, message, Message.Type.WARNING, args);
    }

    private static void addAttribute(WebContext webCtx, String message, Message.Type type, Object... args) {
        webCtx.setVariable(MESSAGE_ATTRIBUTE, new Message(message, type, args));
    }
}

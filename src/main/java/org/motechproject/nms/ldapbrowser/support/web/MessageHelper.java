package org.motechproject.nms.ldapbrowser.support.web;

import java.util.Map;

import static org.motechproject.nms.ldapbrowser.support.web.Message.MESSAGE_ATTRIBUTE;

public final class MessageHelper {

    private MessageHelper() {
    }

    public static void addSuccessAttribute(Map<String, Object> model, String message, Object... args) {
        addAttribute(model, message, Message.Type.SUCCESS, args);
    }

    public static void addErrorAttribute(Map<String, Object> model, String message, Object... args) {
        addAttribute(model, message, Message.Type.DANGER, args);
    }

    public static void addInfoAttribute(Map<String, Object> model, String message, Object... args) {
        addAttribute(model, message, Message.Type.INFO, args);
    }

    public static void addWarningAttribute(Map<String, Object> model, String message, Object... args) {
        addAttribute(model, message, Message.Type.WARNING, args);
    }

    private static void addAttribute(Map<String, Object> model, String message, Message.Type type, Object... args) {
        model.put(MESSAGE_ATTRIBUTE, new Message(message, type, args));
    }
}

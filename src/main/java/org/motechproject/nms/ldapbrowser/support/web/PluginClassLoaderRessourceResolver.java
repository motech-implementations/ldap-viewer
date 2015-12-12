package org.motechproject.nms.ldapbrowser.support.web;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

import java.io.InputStream;

public class PluginClassLoaderRessourceResolver implements IResourceResolver {

    @Override
    public String getName() {
        return "PLUGIN_CLASSLOADER";
    }

    @Override
    public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters,
                                           String resourceName) {
        Validate.notNull(resourceName, "Resource name cannot be null");
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
}

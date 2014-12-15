package org.springframework.data.orient.commons.web;

import org.springframework.core.MethodParameter;
import org.springframework.data.orient.commons.repository.DefaultSource;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.orient.commons.repository.SourceType;
import org.springframework.data.orient.commons.repository.annotation.Source;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Extracts source information from web requests and thus allows injecting {@link Source} instances into controller
 * methods. Request properties to be parsed can be configured.
 *
 * @author Dzmitry_Naskou
 */
public class OrientSourceHandlerArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_TYPE_PARAMETER = "type";
    
    private static final String DEFAULT_NAME_PARAMETER = "name";
    
    private static final String DEFAULT_PREFIX = "source.";
    
    /** The type parameter name. */
    private String typeParameter = DEFAULT_TYPE_PARAMETER;
    
    /** The name parameter name. */
    private String nameParameter = DEFAULT_NAME_PARAMETER;
    
    /** The prefix. */
    private String prefix =  DEFAULT_PREFIX;
    
    /* (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return OrientSource.class.equals(parameter.getParameterType());
    }

    /* (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String typeString = webRequest.getParameter(getParameterNameToUse(typeParameter, parameter));
        String nameString = webRequest.getParameter(getParameterNameToUse(nameParameter, parameter));
        
        String name = nameString == null ? getDefaultSourceNameFrom(parameter) : nameString;
        SourceType type = typeString == null ? getDefaultSourceTypeFrom(parameter) : SourceType.valueOf(typeString.toUpperCase());

        return new DefaultSource(type, name);
    }

    /**
     * Gets the type parameter name.
     *
     * @return the type parameter
     */
    public String getTypeParameter() {
        return typeParameter;
    }

    /**
     * Sets the type parameter name.
     *
     * @param typeParameter the new type parameter
     */
    public void setTypeParameter(String typeParameter) {
        this.typeParameter = typeParameter;
    }

    /**
     * Gets the name parameter name.
     *
     * @return the name parameter
     */
    public String getNameParameter() {
        return nameParameter;
    }

    /**
     * Sets the name parameter name.
     *
     * @param nameParameter the new name parameter
     */
    public void setNameParameter(String nameParameter) {
        this.nameParameter = nameParameter;
    }

    /**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix the new prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * Returns the name of the request parameter to find the {@link org.springframework.data.orient.commons.repository.OrientSource} information in.
     *
     * @param source the source
     * @param parameter the parameter
     * @return the parameter name to use
     */
    protected String getParameterNameToUse(String source, MethodParameter parameter) {
        return new StringBuilder(prefix).append(source).toString();
    }
    
    /**
     * Gets the default source type from {@link Source}.
     *
     * @param parameter the parameter
     * @return the default source type
     */
    private static SourceType getDefaultSourceTypeFrom(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Source.class).type();
    }
    
    /**
     * Gets the default source name from {@link Source}.
     *
     * @param parameter the parameter
     * @return the default source name
     */
    private static String getDefaultSourceNameFrom(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Source.class).value();
    }
}

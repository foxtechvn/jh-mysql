package com.ft.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";
    
    private Constants() {
    }
    
    public final static String MAPPING_REQUEST_UPLOAD ="/upload";
    public final static String MAPPING_REQUEST_GET ="/api/public/uploads";
    public final static String MAPPING_REQUEST_MANAGER ="/manager";
    public final static String MAPPING_REQUEST_IMAGE_JPG ="/image/jpg";
    public final static String MAPPING_REQUEST_IMAGE_PNG ="/image/png";
    public final static String MAPPING_REQUEST_FILE ="/file";
    
    public static final String UPLOAD_PATH = "/srv/uploads";
}

package dku.server.global.constant;

import java.util.List;

public class Constant {

    public static final String LOCAL_SERVER_URL = "http://localhost:8080";

    public static final String LOCAL_CLIENT_URL = "http://localhost:5173";
    public static final String LOCAL_CLIENT_URL_BUILD = "http://localhost:4173/";

    public static final List<String> ALLOWED_CLIENT_URLS = List.of(
            LOCAL_SERVER_URL,
            LOCAL_CLIENT_URL,
            LOCAL_CLIENT_URL_BUILD
    );
}

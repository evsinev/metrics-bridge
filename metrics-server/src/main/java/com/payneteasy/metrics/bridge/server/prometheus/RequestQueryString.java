package com.payneteasy.metrics.bridge.server.prometheus;

import com.payneteasy.metrics.bridge.server.util.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RequestQueryString {

    private final Map<String, String> params;

    public RequestQueryString(Map<String, String> aMap) {
        params = aMap;
    }

    public static RequestQueryString parseQueryString(String aRequestUri) throws PrometheusException {
        Map<String, String> map = new HashMap<>();
        int start = aRequestUri.indexOf('?');
        if(start < 0 ) {
            throw new PrometheusException(402, "No query parameters", "Request uri has no '?' symbol " + aRequestUri);
        }

        String queryString = aRequestUri.substring(start + 1);
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            StringTokenizer keyValueTokenizer = new StringTokenizer(st.nextToken(), "=");
            if(!keyValueTokenizer.hasMoreTokens()) {
                throw new PrometheusException(402, "Wrong query string", "No key in the " + queryString);
            }
            String key = keyValueTokenizer.nextToken();
            if(!keyValueTokenizer.hasMoreTokens()) {
                throw new PrometheusException(402, "Wrong query string", "No value in the " + queryString + " for " + key) ;
            }
            String value = keyValueTokenizer.nextToken();

            map.put(urlDecode(key), urlDecode(value));
        }
        return new RequestQueryString(map);
    }

    private static String urlDecode(String aText) throws PrometheusException {
        try {
            return URLDecoder.decode(aText, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new PrometheusException(402, "Wrong query string", "Unsupported encoding utf-8", e) ;
        }
    }

    public String getRequiredString(String aKey) throws PrometheusException {
        String value = params.get(aKey);
        if(Strings.isEmpty(value)) {
            throw new PrometheusException(402, "No value for " + aKey, "No value for " + aKey + " in the " + params) ;
        }
        return value;
    }

    public int getRequiredInt(String aKey) throws PrometheusException {
        String value = getRequiredString(aKey);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new PrometheusException(402, "Wrong target port", "Cannot parse port " + value, e) ;
        }
    }
}

package com.kw.myproject.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kw.myproject.model.HTTPRequest;
import com.kw.myproject.model.HTTPResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

@Component
public class RequestResponseLoggingFilter implements Filter {
    private final static Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private final static Logger HTTP_LOGGER = LoggerFactory.getLogger("HTTP");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


            HttpServletRequest req = (HttpServletRequest) request;
            CachedHttpServletRequest cachedReq = new CachedHttpServletRequest(req);

            HttpServletResponse res = (HttpServletResponse) response;
            ContentCachingResponseWrapper cachedRes = new ContentCachingResponseWrapper(res);

            HTTPRequest HTTPRequest = new HTTPRequest();
            HTTPRequest.setMethod(cachedReq.getMethod());
            HTTPRequest.setURL(String.valueOf(cachedReq.getRequestURL()));
            Enumeration<String> requestHeaderNames = cachedReq.getHeaderNames();
            if (requestHeaderNames != null) {
                HashMap requestHeadersHM = new HashMap();
                while (requestHeaderNames.hasMoreElements()) {
                    requestHeadersHM.put(requestHeaderNames.nextElement(), cachedReq.getHeader(requestHeaderNames.nextElement()));
                }
                HTTPRequest.setHeaders(objectMapper.writeValueAsString(requestHeadersHM));
            }
            String requestBody = IOUtils.toString(cachedReq.getInputStream(), String.valueOf(StandardCharsets.UTF_8));
            HTTPRequest.setBody(requestBody != null ? requestBody.replaceAll("[\\r\\n]", "") : "");
            String requestModelJSON = objectMapper.writeValueAsString(HTTPRequest);
            HTTP_LOGGER.info("Logging Request: {} ", requestModelJSON);

            filterChain.doFilter(cachedReq, cachedRes);

            ContentCachingResponseWrapper contentCachingResponseWrapper = WebUtils.getNativeResponse(cachedRes, ContentCachingResponseWrapper.class);
            if (contentCachingResponseWrapper != null) {
                byte[] buf = contentCachingResponseWrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    contentCachingResponseWrapper.copyBodyToResponse();
                    HTTPResponse HTTPResponse = new HTTPResponse();
                    HTTPResponse.setContentType(cachedRes.getContentType());
                    HTTPResponse.setStatus(String.valueOf(cachedRes.getStatus()));
                    Collection<String> responseHeaderNames = cachedRes.getHeaderNames();
                    if (responseHeaderNames != null) {
                        HashMap responseHeaderHM = new HashMap();
                        for (String headerName : responseHeaderNames) {
                            responseHeaderHM.put(headerName, cachedRes.getHeader(headerName));
                        }
                        HTTPResponse.setHeaders(objectMapper.writeValueAsString(responseHeaderHM));
                    }
                    String payload = new String(buf, 0, buf.length, contentCachingResponseWrapper.getCharacterEncoding());
                    HTTPResponse.setBody(payload);
                    String responseModelJSON = objectMapper.writeValueAsString(HTTPResponse);
                    HTTP_LOGGER.info("Logging Response: {} ", responseModelJSON);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.error(e.getMessage());

            filterChain.doFilter(request, response);
        }
    }

}

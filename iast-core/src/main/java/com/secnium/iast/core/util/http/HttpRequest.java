package com.secnium.iast.core.util.http;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * @author dongzhiyong@huoxian.cn
 */
public class HttpRequest {
    private String method;
    private String protocol;
    private String scheme;
    private String version;
    private String uri;
    private String url;
    private String queryString;
    private String remoteHost;
    private String remoteAddr;
    private String serverName;
    private int serverPort = -1;
    private boolean secure;

    private Enumeration headerNames;
    private String headerValues;
    private String contextPath;
    private byte[] cachedBody;
    private String cachedBodyStr;
    private HashMap<String, Object> properties;
    private HttpServletRequest servletRequest;

    public HttpRequest(Object httpServletRequest) {
        if (httpServletRequest instanceof HttpServletRequest) {
            servletRequest = (HttpServletRequest) httpServletRequest;
        }
    }

    /**
     * 获取web目录,Weblogic 默认以war包部署的时候不能用getRealPath,xxx.getResource("/")获取
     * 的是当前应用所在的类路径，截取到WEB-INF之后的路径就是当前应用的web根目录了
     * <p>
     * fixme: 调用getSession时，触发set-cookie，导致无法准确的获取用户cookie的生命周期
     *
     * @return
     */
    public String getDocumentRootPath() {
        try {
            ServletContext servletContext = servletRequest.getSession().getServletContext();
            String webRoot = servletContext.getRealPath("/");
            int majorVersion = servletContext.getMajorVersion();

            if (webRoot == null) {
                try {
                    // 检测Servlet版本,Servlet3.0之前ServletContext没有getClassLoader方法
                    if (majorVersion > 2) {
                        ClassLoader classLoader = servletContext.getClass().getClassLoader();
                        URL url = classLoader.getResource("/");
                        if (null != url) {
                            webRoot = url.getPath();
                        }
                    } else {
                        webRoot = servletContext.getResource("/").toString();
                    }
                } catch (Exception ignored) {
                    ;
                }

                ClassLoader requestClassLoader = servletRequest.getClass().getClassLoader();
                if (webRoot == null && requestClassLoader != null) {
                    // getResource("/")可能会获取不到Resource
                    URL url = requestClassLoader.getResource("/");
                    if (url != null) {
                        webRoot = url.getPath();
                    }
                    if (StringUtils.isEmpty(webRoot)) {
                        url = requestClassLoader.getResource("");
                        if (url != null) {
                            webRoot = url.getPath();
                        }
                    }
                }

                if (webRoot != null && webRoot.contains("WEB-INF")) {
                    webRoot = webRoot.substring(0, webRoot.lastIndexOf("WEB-INF"));
                }
            }

            return webRoot;
        } catch (Exception e) {
            ;
        }

        // 如果上面的方法仍无法获取Web目录，以防万一返回一个当前文件路径
        return servletRequest.getContextPath();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getProtocol() {
        if (null == protocol && null != servletRequest) {
            protocol = servletRequest.getProtocol();
        }
        return protocol;
    }

    public String getScheme() {
        if (scheme == null && null != servletRequest) {
            scheme = servletRequest.getScheme();
        }
        return scheme;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequestURI() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRequestURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrl(StringBuffer url) {
        this.setUrl(url.toString());
    }

    public String getQueryString() {
        if (this.queryString == null && null != servletRequest) {
            this.queryString = servletRequest.getQueryString();
        }
        return queryString;
    }

    public String getRemoteIp() {
        if (remoteAddr == null) {
            remoteAddr = servletRequest.getRemoteAddr();
        }
        return remoteAddr;
    }

    public String getContextPath() {
        if (null == contextPath) {
            setContextPath();
        }
        return contextPath;
    }

    public void setContextPath() {
        this.contextPath = getDocumentRootPath();
        if (this.contextPath == null || "/".equals(this.contextPath)) {
            this.contextPath = "ROOT";
        } else {
            String[] pathTokens = this.contextPath.split("/");
            this.contextPath = pathTokens[pathTokens.length - 1];
        }
    }

    public byte[] getCachedBody() {
        return this.cachedBody;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void setHeaderNames(Enumeration headerNames) {
        this.headerNames = headerNames;
    }

    private void setHeaderNames(Class cls, Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = cls.getMethod("getHeaderNames");
        method.setAccessible(true);
        Object headers = method.invoke(obj);
        if (headers instanceof Enumeration) {
            setHeaderNames((Enumeration) headers);
        }
    }

    private String getHeader(String name) {
        return servletRequest.getHeader(name);
    }

    public String getCookieValue() {
        return this.getHeader("Cookie");
    }

    public String getTraceId() {
        return this.getHeader("x-trace-id");
    }

    public String getHeadersValue() {
        if (this.headerValues == null) {
            if (this.headerNames == null) {
                this.headerNames = servletRequest.getHeaderNames();
            }
            StringBuilder stringBuilder = new StringBuilder();
            while (this.headerNames.hasMoreElements()) {
                String name = (String) this.headerNames.nextElement();
                String value = this.getHeader(name);
                stringBuilder.append(name).append(":").append(value).append("\n");
            }

            this.headerValues = stringBuilder.toString();
        }
        return headerValues;
    }

    public String getRemoteHost() {
        if (remoteHost == null) {
            remoteHost = servletRequest.getRemoteHost();
        }
        return remoteHost;
    }


    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemoteAddr() {
        if (remoteAddr == null) {
            remoteAddr = servletRequest.getRemoteAddr();
        }
        return remoteAddr;
    }

    public String getServerName() {
        if (null == serverName && null != servletRequest) {
            serverName = servletRequest.getServerName();
        }
        return serverName;
    }

    public int getServerPort() {
        if (-1 == serverPort && null != servletRequest) {
            serverPort = servletRequest.getServerPort();
        }
        return serverPort;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isNotRepeatRequest() {
        return null == getTraceId();
    }

    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = servletRequest.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }
}

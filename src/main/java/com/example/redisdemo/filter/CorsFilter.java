package com.example.redisdemo.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {

    public static final String DEFAULT_ALLOW_ORIGIN = "*";
    private Logger logger = LoggerFactory.getLogger(CorsFilter.class);
    private String allowOrigin;

    public CorsFilter(String allowOrigin) {
        this.allowOrigin = (String) StringUtils.defaultIfBlank(allowOrigin, "*");
        this.logger.info("启用cors支持, allow origin:[{}]", this.allowOrigin);
    }

    public CorsFilter() {
        this("*");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", this.allowOrigin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}

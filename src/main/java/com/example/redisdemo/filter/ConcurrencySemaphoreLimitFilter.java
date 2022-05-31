//package com.example.redisdemo.filter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.TimeUnit;
//
///**
// * Filter第一种使用方式：@WebFilter和启动类加上@ServletCompomentScan
// */
//@WebFilter(filterName = "ConcurrencySemaphoreLimitFilter")
//public class ConcurrencySemaphoreLimitFilter implements Filter {
//
//
//    private final Logger logger = LoggerFactory.getLogger(ConcurrencySemaphoreLimitFilter.class);
//    /**
//     * 资源数
//     */
//    private   int permits = 10;
//    private Semaphore semaphore;
//
//
//    public ConcurrencySemaphoreLimitFilter(int permits) {
//        this.permits = permits;
//        this.semaphore = new Semaphore(this.permits);
//    }
//
//    public ConcurrencySemaphoreLimitFilter() {
//        this(10);
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        try {
//            if (!this.semaphore.tryAcquire(5L, TimeUnit.SECONDS)) {
//                this.logger.info("未能获取到信号量许可, 重试");
//                Thread.sleep(2000);
//                HttpServletResponse httpServletResponse = (HttpServletResponse) res;
//                httpServletResponse.sendError(403, "请求频率超过限制:" + permits);
//
//            } else {
//                this.logger.warn("获取到信号量许可,继续访问后端服务");
//                chain.doFilter(req, res);
//            }
//
//        } catch (InterruptedException var8) {
//            this.logger.warn("获取信号量许可失败", var8);
//            return;
//        } finally {
//            this.semaphore.release();
//        }
//
//    }
//
//    @Override
//    public void destroy() {
//    }
//}

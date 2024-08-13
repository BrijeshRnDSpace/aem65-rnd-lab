package com.aem.rnd.lab.core.servlets;


import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;

import javax.servlet.*;
import java.io.IOException;


@Component
@Slf4j
@SlingServletFilter(
        scope = {SlingServletFilterScope.REQUEST},
        //suffix_pattern = "/suffix/foo",
        resourceTypes = {"rnd_lab/components/page"},
        pattern = "/content/.*",
        extensions = {"html", "txt", "json"},
        selectors = {"foo", "bar"},
        methods = {"GET", "HEAD"}
)
public class PracticeSlingServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        log.error(" Now our custom PracticeSlingServletFilter Called ");
        chain.doFilter(request, slingResponse);
    }

    @Override
    public void destroy() {

    }
}

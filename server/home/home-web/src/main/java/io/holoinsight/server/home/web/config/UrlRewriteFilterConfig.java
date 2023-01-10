/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 *
 * @author jsy1001de
 * @version 1.0: UrlRewriteFilterConfig.java, v 0.1 2022年02月22日 7:20 下午 jinsong.yjs Exp $
 */
@Component
@Order(2)
public class UrlRewriteFilterConfig extends UrlRewriteFilter {
    private static final String URL_REWRITE = "classpath:/urlrewrite.xml";

    @Value(URL_REWRITE)
    private Resource resource;

    protected void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
        try {
            Conf conf = new Conf(filterConfig.getServletContext(), resource.getInputStream(), resource.getFilename(),
                    "@@traceability@@");
            checkConf(conf);
        } catch (IOException ex) {
            throw new ServletException("Unable to load URL rewrite configuration file from " + URL_REWRITE, ex);
        }
    }
}
package com.zhuxi.Filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import java.io.IOException;
import java.util.Arrays;


public class XSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
         filterChain.doFilter(new XssSafeRequestWrapper((HttpServletRequest) servletRequest),servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

   @Slf4j
   class XssSafeRequestWrapper extends HttpServletRequestWrapper{
        public XssSafeRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
      public String getParameter(String name){
            return cleanXSS(super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String values){
            String[] parameterValues = super.getParameterValues(values);
            if(parameterValues == null){
                return null;
            }
            return Arrays.stream(parameterValues).map(this::cleanXSS).toArray(String[]::new);
        }


        private String cleanXSS(String value){
            if (value == null){
                return null;
            }

            String cleaned = Jsoup.clean(
                    value,
                    Safelist.basicWithImages()
                            .addTags("div","span")
                            .addAttributes("a","target","rel")
                            .addEnforcedAttribute("a","rel","noopener")
                            .addProtocols("a","hef","http","https")
                            .addProtocols("img","src","http","https")
            );

            cleaned = cleaned.replaceAll("[\u0000-\u001F]", "");

            if(value.equals(cleaned)){
                log.debug("XSS is triggered: before-Value{}  after-Value{}", value, cleaned);
            }
            return cleaned;
        }

}

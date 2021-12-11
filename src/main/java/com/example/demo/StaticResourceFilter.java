package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 对任何路径都进行匹配的一个Filter，主要目的是直接返回静态资源
 * 此Filter工作在Controller之前
 */
@WebFilter(urlPatterns = {"/**"})
@Order(0)
@Component
public class StaticResourceFilter implements Filter {
    final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public static String getFilename(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("user-agent");
        // 针对以IE或者Edge为内核的浏览器
        if (userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Edge")) {
            filename = URLEncoder.encode(filename, "UTF-8");
        } else {
            // 非IE浏览器的处理
            filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }

        return filename;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String path = httpServletRequest.getServletPath();
        // 如果是静态文件，就直接在此处加载并返回
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".woff2") || path.endsWith(".woff") || path.endsWith(".ttf")) {
            // 获取文件名，并设置编码
            String filename = new String(path.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            // 获取文件的mime类型
            String mimeType = servletRequest.getServletContext().getMimeType(filename);

            // 设置文件的mime类型
            httpServletResponse.setContentType(mimeType);

            // 文件名编码
            String newFilename = getFilename(httpServletRequest, filename);

            // 设置下载头信息
            httpServletResponse.setHeader("content-disposition", "attachment;filename=" + newFilename);

            // 对拷流
//            InputStream is = getClass().getResourceAsStream("/static/" + filename);
            ClassPathResource resource = new ClassPathResource("/static" + filename); // 打包之后，需要从jar包里面取数据，故用这个
            InputStream is = resource.getInputStream();

            ServletOutputStream os = httpServletResponse.getOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = Objects.requireNonNull(is).read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }

            // 关闭资源
            os.close();
            is.close();
        }
        // 如果不是静态文件，则继续下去，交给Controller
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}

package com.zx.common.crawl.trategy;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zx.common.crawl.request.CrawlerRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author ZhaoXu
 * @date 2022/9/22 23:29
 */
public class HtmlUnitCrawlStrategy implements CrawlStrategy {
    @Override
    public Document loadPage(CrawlerRequest request) {
        if (request == null || request.getUrl() == null) {
            return null;
        }
        try (WebClient webClient = new WebClient(BrowserVersion.FIREFOX)) {
            // 请求设置
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(true);

            WebRequest webRequest = new WebRequest(new URL(request.getUrl()));
            // 请求参数
            List<NameValuePair> params = new ArrayList<>();
            Optional.ofNullable(request.getParams()).orElse(Collections.emptyMap()).forEach((k, v) -> params.add(new NameValuePair(k, v)));
            webRequest.setRequestParameters(params);

            // cookie
            Optional.ofNullable(request.getCookies()).orElse(Collections.emptyMap()).forEach((k, v) -> webClient.getCookieManager().addCookie(new Cookie("", k, v)));

            // 请求头
            Optional.ofNullable(request.getHeaders()).orElse(Collections.emptyMap()).forEach(webRequest::setAdditionalHeader);

            if (request.getUserAgent() != null) {
                webRequest.setAdditionalHeader("User-Agent", request.getUserAgent());
            }
            if (request.getReferrer() != null) {
                webRequest.setAdditionalHeader("Referer", request.getReferrer());
            }

            // 代理
            if (request.getProxy() != null) {
                InetSocketAddress address = (InetSocketAddress) request.getProxy().address();
                boolean isSocks = request.getProxy().type() == Proxy.Type.SOCKS;
                String proxyScheme = null;
                webClient.getOptions().setProxyConfig(new ProxyConfig(address.getHostName(), address.getPort(), proxyScheme, isSocks));
            }
            webRequest.setHttpMethod(request.getPost() ? HttpMethod.POST : HttpMethod.GET);
            HtmlPage page = webClient.getPage(webRequest);

            webClient.waitForBackgroundJavaScriptStartingBefore(request.getTimeoutMillis());
            webClient.waitForBackgroundJavaScript(request.getTimeoutMillis());

            String pageAsXml = page.asXml();
            if (pageAsXml != null) {
                return Jsoup.parse(pageAsXml);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

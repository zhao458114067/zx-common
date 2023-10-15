package com.zx.common.crawl.trategy;

import com.zx.common.crawl.request.CrawlerRequest;
import org.jsoup.nodes.Document;

/**
 * @author ZhaoXu
 * @date 2022/9/22 23:23
 */
public interface CrawlStrategy {
    Document loadPage(CrawlerRequest request);
}

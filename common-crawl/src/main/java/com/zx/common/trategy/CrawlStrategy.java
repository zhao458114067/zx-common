package com.zx.common.trategy;

import com.zx.common.request.CrawlerRequest;
import org.jsoup.nodes.Document;

/**
 * @author ZhaoXu
 * @date 2023/9/22 23:23
 */
public interface CrawlStrategy {
    Document loadPage(CrawlerRequest request);
}

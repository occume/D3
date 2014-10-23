package org.d3.spider;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.ConsolePageModelPipeline;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * @author code4crafter@gmail.com
 * @date 14-4-11
 */
@TargetUrl("http://www.cnblogs.com/[#\\-p]*")
@ExtractBy(value = "//div[@id=\"post_list\"]/div",multi = true)
public class QQMeishi {

    @ExtractBy("//div[@class=post_item_body]/h3/a[@class=titlelnk]/text()")
    private String shopName;

//    @ExtractBy("//div[@class=info]/a[@class=title]/text()")
//    private String promo;

    public static void main(String[] args) {
        OOSpider.create(Site.me(), new ConsolePageModelPipeline(), QQMeishi.class).addUrl("http://www.cnblogs.com/").thread(4).run();
    }

}

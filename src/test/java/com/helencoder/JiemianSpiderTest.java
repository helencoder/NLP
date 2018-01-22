package com.helencoder;

import com.helencoder.util.FileIO;
import com.helencoder.util.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 界面抓取测试
 *
 * Created by zhenghailun on 2018/1/22.
 */
public class JiemianSpiderTest {
    public static void main(String[] args) {
        for (int i = 1000000; i < 2000000; i++) {
            String url = String.format("http://www.jiemian.com/article/%d.html", i);
            getDetails(url, i);
        }

    }

    /**
     * 获取文章详情
     */
    private static void getDetails(String url, int id) {
        String response = HttpClientUtil.get(url);

        Document doc = Jsoup.parse(response);
        StringBuilder recordSB = new StringBuilder();
        recordSB.append(id + "\t\t");

        // 标题
        Elements elements1 = doc.getElementsByClass("article-header");
        String title = "";
        for (Element element : elements1) {
            recordSB.append(element.select("h1").text().trim() + "\t\t");
            title = element.select("h1").text();
            //System.out.println(element.select("h1").text());
            //System.out.println(element.select("p").text());
        }

        // 标签
        Elements elements = doc.getElementsByClass("main-mate");
        for (Element element : elements) {
            Elements typeElements = element.select("a:not(.tags)");
            StringBuilder typeSB = new StringBuilder();
            for (Element type: typeElements) {
                typeSB.append(type.text());
                typeSB.append(" ");
            }
            recordSB.append(typeSB.toString().trim() + "\t\t");

            StringBuilder tagSB = new StringBuilder();
            Elements tagElements = element.select("a[class=tags]");
            for (Element tag: tagElements) {
                tagSB.append(tag.text());
                tagSB.append(" ");
            }
            recordSB.append(tagSB.toString().trim() + "\t\t");
        }

        if (recordSB.toString().length() > 10) {
            System.out.println(id + "\t" + title);
            FileIO.appendFile("jiemian_record.txt", recordSB.toString());
        }
    }

}

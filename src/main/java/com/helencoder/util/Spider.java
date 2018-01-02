package com.helencoder.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 参考频道文章抓取
 *
 * Created by helencoder on 2017/12/28.
 */
public class Spider {
    public static void main(String[] args) throws Exception {

        int num = 1;
        while (true) {
            List<String> newsidList = getNewsIDList(num);
            if (newsidList.size() == 0) {
                break;
            }
            for (String newsid : newsidList) {
                getNewsContent(newsid);
            }

            num++;
        }

    }

    /**
     * 获取文章newsid列表
     */
    private static List<String> getNewsIDList(int pageNum) throws JSONException {
        String baseURL = "http://ai.cmbchina.com/mbf4info/Services/CmbNewsServices.ashx?type=getcmbnews&keyword=&pageNum=" + pageNum;
        String response = HttpClientUtil.get(baseURL);
        JSONObject json = new JSONObject(response.substring(1, response.length() -1));
        List<String> newsidList = new ArrayList<String>();
        System.out.println("pageNum: " + pageNum);
        if (json.getInt("code") == 0) {
            String[] newsids = json.getString("newsids").split("~@\\*\\^");
            String[] newstitles = json.getString("newstitles").split("~@\\*\\^");
            String[] newsdates = json.getString("newsdates").split("~@\\*\\^");
            String[] newstypenames = json.getString("newstypenames").split("~@\\*\\^");
            for (int i = 0; i < newsids.length; i++) {
                System.out.println(newstitles[i]);
                if (compareDate(newsdates[i], "2017-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss")) {
                    newsidList.add(newsids[i]);
                    String details = "{newstitle: \"" + newstitles[i] + "\", " + "newstypename: \"" + newstypenames[i] + "\", " +
                            "newsid: \"" + newsids[i] + "\", " + "newsdate: \"" + newsdates[i] + "\"}";
                    FileIO.appendFile("corpus/zixun_record.txt", details);
                }
            }
        }
        System.out.println();

        return newsidList;
    }

    /**
     * 获取文章详情
     */
    private static void getNewsContent(String newsid) {
        String newsUrl = "http://ai.cmbchina.com/mbf4info/CmbNewsView.aspx?newsID=" + newsid;
        String response = HttpClientUtil.get(newsUrl);

        Document doc = Jsoup.parse(response);
        String title = doc.getElementById("cnTitle").text();
        String content = doc.getElementById("cnContent_mst").text();

        String filepath = "corpus/zixun2017/" + newsid + ".txt";
        FileIO.appendFile(filepath, title + "\n" + content);
    }

    /**
     * 日期比较
     */
    public static boolean compareDate(String date1, String date2, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Boolean flag = false;
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            flag = dt1.after(dt2);
        } catch (Exception exception) {
        }

        return flag;
    }

}

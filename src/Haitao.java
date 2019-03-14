import myNet.OkHttp;
import utils.ExcelUtils;
import utils.MyUtil;

import java.util.ArrayList;

/**
 * @author sucre chen 906509023@qq.com
 * @Title: Haitao
 * @Package
 * @Description: 55海淘 优惠折扣
 * @date 2019-03-09 10:06
 */
public class Haitao {


    public void getDiscount() {
        ArrayList<String> total = new ArrayList<>();
        total.add("商品连接|标题|商品图片连接|简介");
        OkHttp okHttp = new OkHttp();
        String ret = "";
        for (int i = 1; i <= 3018; i++) {
            ret = okHttp.goGet("https://www.55haitao.com/deals/0-0-0-0-0-0-0-0-" + i + ".html");
            if (!MyUtil.isEmpty(ret)) {
                ArrayList<String> titleList = MyUtil.midWordAll("<h3 class=\"index-deal-title\">", "</h3>", ret);
                ArrayList<String> detailList = MyUtil.midWordAll("<div class=\"clearfix index-deal-con\">", "</div>", ret);
                ArrayList<String> desList = MyUtil.midWordAll("<div class=\"index-deal-des\">", "</div>", ret);

                //循环处理数据并显示
                String titleTemp = "";
                String detailTemp = "";
                total.add("第 " + i + "页");
                System.out.println(total.get(total.size()-1));

                for (int k = 0; k < titleList.size(); k++) {
                    titleTemp = titleList.get(k);
                    detailTemp = detailList.get(k);

                    //标题 连接 是否有返利
                    String url = trimString("https:" + MyUtil.midWord("<a href=\"", "\" ", titleTemp));
                    String title = trimString(MyUtil.midWord("target='_blank'>", "</a>", titleTemp));


                    //图片连接 详细信息
                    String picUrl = trimString("https:" + MyUtil.midWord("data-original=\"", "\" ", detailTemp));
                    String details = trimString(desList.get(k));
                    String rowData = url + "|" + title + "|" + picUrl + "|" + details;

                    //保存数据
                    total.add(rowData);

                    //System.out.println(rowData);

                }

                //ArrayList<String> titleList =MyUtil.midWordAll("<div class=\"index-deal-des\">","</div>",ret);
                // ArrayList<String> titleList =MyUtil.midWordAll("<div class=\"index-deal-des\">","</div>",ret);

            }
            MyUtil.sleeps(500);
        }
        ExcelUtils.writeExcel("55海淘优惠数据.xlsx", total);
        System.out.println("done!!!");
    }


    private String trimString(String data) {
        String temp = MyUtil.midWord("<b", ">", data);
        if (!MyUtil.isEmpty(temp)) {
            data = data.replace(temp, "");
        }
        return data.replace("\n", "").replace("\t", "").replace("<br>", "").replace("</b>", "").replace("<b>", "").replace("<p>", "").replace("</p>", "");
    }
}

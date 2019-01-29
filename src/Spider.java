import myNet.Nets;
import myNet.OkHttp;
import utils.MyUtil;

import java.util.ArrayList;

/**
 * 友米乐公司爬虫工具类
 */

public class Spider {


    public static void main(String[] arg) {
        getDetail();

    }


    /**
     * 取云集限时购数据。
     */
    public static void getDetail() {
        Nets nets = new Nets();
        OkHttp okHttp=new OkHttp();
        String ret = "";
        String time = "";
        ArrayList<String> times = null;
        int page = 0;

        System.out.println("开始抓取！");
        System.out.println("名称|简介|现价|原价");
        //先取出所有抢购时间
        //ret = nets.goPost("m.yunjiglobal.com", 443, getTime());
        ret= okHttp.goGet("https://m.yunjiglobal.com/yunjibuyer/queryAllActivityTimesList.json",null);
        if (!MyUtil.isEmpty(ret)) {
            times = MyUtil.midWordAll("activityTimesId\":", ",\"", ret);
            //System.out.println(times);
            String[] buyTime=new String[]{"昨日19:00","昨日21:00","08:30","9:00","10:00","11:00","12:00","13:00","14:00","16:00","17:00","19:00","21:00","明天"};
            //循环取出所有页数数据。
            for (int k = 2; k <= 14; k++) {
                page=0;
                System.out.println("抢购时间："+ buyTime[k-2] +"\r\n");
                while (true) {

                    //ret = nets.goPost("m.yunjiglobal.com", 443, DetailData(times.get(k), String.valueOf(page)));
                    ret= okHttp.goGet("https://m.yunjiglobal.com/yunjibuyer/queryItemListByTimesId.json?activityTimesId=" + times.get(k) + "&pageNo=" + String.valueOf(page) ,null);
                    if (!MyUtil.isEmpty(ret)) {
                        if (ret.indexOf("itemList\":[]") != -1) {
                            break;
                        }
                        ArrayList<String> name = MyUtil.midWordAll("activityName\":\"", "\"", ret);
                        ArrayList<String> spot = MyUtil.midWordAll("subtitle\":\"", "\",\"", ret);
                        ArrayList<String> nowprice = MyUtil.midWordAll("itemPrice\":", ",\"", ret);
                        ArrayList<String> price = MyUtil.midWordAll("itemVipPrice\":", ",\"", ret);
                        if (name.size() != 0) {
                            for (int i = 0; i < name.size(); i++) {
                                System.out.println( name.get(i) + "|" + spot.get(i) + "|" + nowprice.get(i) + "|" + price.get(i));
                            }
                            page++;
                        }
                    }
                }
            }
        }


        System.out.println("结束抓取！");
    }


    /**
     * 根据时间和当前页数取数据
     *
     * @param time 抢购时间
     * @param page 数据当前页数
     * @return byte
     */
    public static byte[] DetailData(String time, String page) {
        StringBuilder data = new StringBuilder(900);
        String temp = "";
        data.append("GET /yunjibuyer/queryItemListByTimesId.json?activityTimesId=" + time + "&pageNo=" + page + " HTTP/1.1\r\n");
        data.append("Host: m.yunjiglobal.com\r\n");
        data.append("Connection: keep-alive\r\n");
        data.append("Accept: application/json, text/plain, */*\r\n");
        data.append("X-Requested-With: XMLHttpRequest\r\n");
        data.append("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36\r\n");
        data.append("Referer: https://m.yunjiglobal.com/yunjibuyer/static/vue-buyer/idc/index.html\r\n");
        data.append("Accept-Language: en-US,en;q=0.9\r\n");
        data.append("\r\n");
        data.append("\r\n");
        data.append("\r\n");
        return data.toString().getBytes();
    }


    public static byte[] getTime() {
        StringBuilder data = new StringBuilder(900);
        String temp = "";
        data.append("GET /yunjibuyer/queryAllActivityTimesList.json HTTP/1.1\r\n");
        data.append("Host: m.yunjiglobal.com\r\n");
        data.append("Connection: keep-alive\r\n");
        data.append("Accept: application/json, text/plain, */*\r\n");
        data.append("X-Requested-With: XMLHttpRequest\r\n");
        data.append("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36\r\n");
        data.append("Referer: https://m.yunjiglobal.com/yunjibuyer/static/vue-buyer/idc/index.html\r\n");
        data.append("Accept-Language: en-US,en;q=0.9\r\n");
        data.append("\r\n");
        data.append("\r\n");
        return data.toString().getBytes();
    }


}

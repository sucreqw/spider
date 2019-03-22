import myNet.Nets;
import myNet.OkHttp;
import utils.ExcelUtils;
import utils.MyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 友米乐公司爬虫工具类
 */

public class Spider {


    public static void main(String[] arg) {
        //getDetail();
        //ExcelUtils.writeExcel("test.xlsx",new String[]{"1|1|1","2|2|2|2","3|2|2|3|3"});

        String date = MyUtil.getDate("MM");
        if (date.equals("03") || date.equals("04")) {
            //System.out.println(MyUtil.timestampToDate("1551150000000"));
            //云集数据。
            // getDetail();
            //取myzebravip数据
            //myzebravip();

            //取55海淘 优惠折扣
            /*Haitao haitao=new Haitao();
            haitao.getDiscount();*/

            /*OkHttp okHttp=new OkHttp();
            HashMap<String,String> map=new HashMap();
            map.put("nick","test");*/

            //云集新品热销榜
            //queryHistoryTrackItems();

            //爆品的其它类别数据
            zebravip_rest();

        } else {
            ArrayList<String> list = new ArrayList();
            list.add("出错了！");
            ExcelUtils.writeExcel("云集抢购数据（" + MyUtil.getDate("dd") + "）.xlsx", list);
        }
    }


    /**
     * 取云集限时购数据。
     */
    public static void getDetail() {
        Nets nets = new Nets();
        OkHttp okHttp = new OkHttp();
        String ret = "";
        String time = "";
        ArrayList<String> times = null;
        int page = 0;

        System.out.println("开始抓取！");
        ArrayList<String> list = new ArrayList<>();
        list.add("名称|简介|现价|原价");
        System.out.println("名称|简介|现价|原价");
        //先取出所有抢购时间
        //ret = nets.goPost("m.yunjiglobal.com", 443, getTime());
        //                     https://m.yunjiglobal.com/yunjibuyer/queryAllActivityTimesList.json
        ret = okHttp.goGet("https://m.yunjiglobal.com/yunjibuyer/queryAllActivityTimesList.json");
        if (!MyUtil.isEmpty(ret)) {
            times = MyUtil.midWordAll("activityTimesId\":", ",\"", ret);
            ArrayList buyTime = MyUtil.midWordAll("startTime\":", ",\"", ret);
            //ArrayList showTime=MyUtil.midWordAll("alias\":\"","\",\"",ret);
            //ArrayList showTime2=MyUtil.midWordAll("dateTime\":\"","\",\"",ret);
            //System.out.println(times);
            //String[] buyTime=new String[]{"昨日19:00","昨日21:00","08:30","9:00","10:00","11:00","12:00","13:00","14:00","16:00","17:00","19:00","21:00","明天"};
            //循环取出所有页数数据。
            for (int k = 2; k < buyTime.size(); k++) {
                page = 0;
                list.add("抢购时间：" + MyUtil.timestampToDate(buyTime.get(k - 1).toString()));
                //list.add("抢购时间："+ showTime.get(k-1).toString() + " " + showTime2.get(k-1).toString());//MyUtil.timestampToDate(buyTime.get(k).toString()));
                System.out.println("抢购时间：" + MyUtil.timestampToDate(buyTime.get(k - 1).toString()) + "\r\n");
                //System.out.println("抢购时间："+ showTime.get(k-1).toString() + " " + showTime2.get(k-1).toString());//MyUtil.timestampToDate(buyTime.get(k).toString()));

                while (true) {

                    //ret = nets.goPost("m.yunjiglobal.com", 443, DetailData(times.get(k), String.valueOf(page)));
                    //                     https://m.yunjiglobal.com/yunjibuyer/queryItemListByTimesId.json?activityTimesId=101892&pageNo=0
                    ret = okHttp.goGet("https://m.yunjiglobal.com/yunjibuyer/queryItemListByTimesId.json?activityTimesId=" + times.get(k) + "&pageNo=" + String.valueOf(page));
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
                                list.add(name.get(i) + "|" + spot.get(i) + "|" + nowprice.get(i) + "|" + price.get(i));
                                System.out.println(name.get(i) + "|" + spot.get(i) + "|" + nowprice.get(i) + "|" + price.get(i));
                            }
                            page++;
                        }
                    }
                }
            }
        }

        ExcelUtils.writeExcel("云集抢购数据（" + MyUtil.getDate("dd") + "）.xlsx", list);
        System.out.println("结束抓取！");
    }

    /**
     * 取 myzebravip 限时购数据。
     */
    private static void myzebravip() {
        OkHttp okHttp = new OkHttp();
        String ret = "";
        String time = "";
        ArrayList<String> times = null;
        int page = 0;

        System.out.println("开始抓取！");
        ArrayList<String> list = new ArrayList<>();
        String cloum = "名称|简介|现价|原价|赚";
        list.add(cloum);
        System.out.println(cloum);
        //先取出所有抢购时间
        //ret = nets.goPost("m.yunjiglobal.com", 443, getTime());
        //                     https://m.yunjiglobal.com/yunjibuyer/queryAllActivityTimesList.json
        HashMap<String, String> header = new HashMap<>();
        HashMap<String, String> body = new HashMap<>();
        header.put("imei", "0d64935528e84ba0f2cef8cbee470c4a");
        header.put("platform", "301");
        header.put("requestid", "34f289b2c70707839b10a3d916b5a4bf");
        header.put("sessionid", "6648c1af15eb42c6b301de6ddf846ab0");

        body.put("type", "12");

        ret = okHttp.goPost("https://prodapi.myzebravip.com/api/hot/index", header, body);
        if (!MyUtil.isEmpty(ret)) {
            times = MyUtil.midWordAll("\"id\":", ",\"", ret);
            ArrayList<String> buyTime = MyUtil.midWordAll("startTime\":\"", "\",\"", ret);
            //ArrayList showTime=MyUtil.midWordAll("alias\":\"","\",\"",ret);
            //ArrayList showTime2=MyUtil.midWordAll("dateTime\":\"","\",\"",ret);
            //System.out.println(times);
            //String[] buyTime=new String[]{"昨日19:00","昨日21:00","08:30","9:00","10:00","11:00","12:00","13:00","14:00","16:00","17:00","19:00","21:00","明天"};
            //循环取出所有页数数据。
            for (int k = 0; k < buyTime.size(); k++) {
                page = 0;
                list.add("抢购时间：" + buyTime.get(k));
                //list.add("抢购时间："+ showTime.get(k-1).toString() + " " + showTime2.get(k-1).toString());//MyUtil.timestampToDate(buyTime.get(k).toString()));
                System.out.println(list.get(list.size() - 1));
                //System.out.println("抢购时间："+ showTime.get(k-1).toString() + " " + showTime2.get(k-1).toString());//MyUtil.timestampToDate(buyTime.get(k).toString()));
                body.clear();

                while (true) {
                    //id=7140&startTime=2019-02-27%2010%3A00%3A00
                    body.put("id", times.get(k));
                    body.put("startTime", buyTime.get(k));
                    ret = okHttp.goPost("https://prodapi.myzebravip.com/api/hot/list", header, body);
                    if (!MyUtil.isEmpty(ret)) {
                       /* if (ret.indexOf("itemList\":[]") != -1) {
                            break;
                        }*/
                        ArrayList<String> name = MyUtil.midWordAll("name\":\"", "\",\"", ret);
                        ArrayList<String> spot = MyUtil.midWordAll("promotionText\":\"", "\",\"", ret);
                        ArrayList<String> nowprice = MyUtil.midWordAll("memberPrice\":\"", "\",\"", ret);
                        ArrayList<String> price = MyUtil.midWordAll("salePrice\":\"", "\",\"", ret);
                        ArrayList<String> earn = MyUtil.midWordAll("shareProfits\":\"", "\",\"", ret);

                        if (name.size() != 0) {
                            for (int i = 0; i < name.size(); i++) {
                                list.add(name.get(i) + "|" + spot.get(i) + "|" + nowprice.get(i) + "|" + price.get(i) + "|" + earn.get(i));
                                //System.out.println( name.get(i) + "|" + spot.get(i) + "|" + nowprice.get(i) + "|" + price.get(i)+ "|" + earn.get(i));
                                System.out.println(list.get(list.size() - 1));
                            }
                            page++;
                        }

                        break;
                    }
                }
            }
        }

        ExcelUtils.writeExcel("myzebravip抢购数据（" + MyUtil.getDate("dd") + "）.xlsx", list);
        System.out.println("结束抓取！");
    }

    /**
     * 云集新品热销榜
     */
    private static void queryHistoryTrackItems(){
        OkHttp okHttp = new OkHttp();
        String ret = "";
        ArrayList<String> total = new ArrayList<>(); ;
        System.out.println("开始抓取！");
        total.add("名称|详情|价格|原价");
        String[] trackType={"美食榜","服饰榜","美妆榜","家居榜","更多新品"};
        String[] trackTypeNum={"4","6","3","5","7"};
        for(int k=0;k<trackTypeNum.length;k++) {

            total.add(trackType[k]);
            for (int page = 0; page < 3; page++) {
                int curosr = page * 10;
                String url = "https://yunjioperate.yunjiglobal.com/yunjioperateapp/subject/queryHistoryTrackItems.json?subjectId=66720&trackType="+ trackTypeNum[k] +"&pageIndex=" + page + "&storeType=1&max=30&cursor=" + curosr + "&hideNoStock=1&appCont=0&isPersonality=0";
                ret = okHttp.goGet(url);
                if (!MyUtil.isEmpty(ret)) {
                    //System.out.println(ret);
                    ArrayList<String> name = MyUtil.midWordAll("activityName\":\"", "\",\"", ret);
                    ArrayList<String> productSpot = MyUtil.midWordAll("productSpot\":\"", "\",\"", ret);
                    ArrayList<String> nowPrice = MyUtil.midWordAll("itemPrice\":", ",\"", ret);
                    ArrayList<String> oldPrice = MyUtil.midWordAll("itemVipPrice\":", ",\"", ret);

                    total.add("第" + page + "页");
                    String spot = "";
                    for (int i = 0; i < name.size(); i++) {
                        if (!MyUtil.isEmpty(productSpot) && productSpot.size() > i) {
                            spot = productSpot.get(i);
                        }else{
                            spot="";
                        }
                        total.add(name.get(i) + "|" + spot + "|" + nowPrice.get(i) + "|" + oldPrice.get(i));
                    }

                }
            }
        }
        ExcelUtils.writeExcel("云集云集新品热销榜" + MyUtil.getDate("dd") + ".xlsx",total);
        System.out.println("结束抓取！");

    }

    /**
     * 爆品 其它类别数据。
     */
    private static void zebravip_rest(){
        //尖货爆品
        zebravip_jh("https://wap.myzebravip.com/lego/1552986133567.html?isHide=1&uid=3044","尖货爆品");
        //美妆个护
        zebravip_jh("https://wap.myzebravip.com/lego/1552887805496.html?isHide=1&uid=2960","美妆个护");
        //量贩会场
        zebravip_jh("https://wap.myzebravip.com/lego/1552877971645.html?isHide=1&uid=2941","量贩会场");
        //新会员必入
        zebravip_jh("https://wap.myzebravip.com/lego/1552878034786.html?isHide=1&uid=2939","新会员必入");
        //春野食记
        zebravip_jh("https://wap.myzebravip.com/lego/1550474900431.html?isHide=1&uid=1143","春野食记");
        //DW指定入驻
        zebravip_jh("https://wap.myzebravip.com/lego/1552715262623.html?isHide=1&uid=2903","DW指定入驻");


    }

    /**
     * 尖货爆品
     */
    private static void zebravip_jh(String url,String filename){
        System.out.println("开始抓取。");
        ArrayList<String> totals=new ArrayList<>();
        totals.add("名称|现价|原价|会员价");
        //先取尖货爆品所有类别。
        OkHttp okHttp = new OkHttp();
        String ret = "";
        ret=okHttp.goGet(url);
        if(!MyUtil.isEmpty(ret)){
            String titles=MyUtil.midWord("floorList:[","}]",ret);
            ArrayList<String> title=MyUtil.midWordAll("{\"title\":\"","\"}",ret);
            ArrayList<String> titleId=MyUtil.midWordAll("ids:\"","\" ,",ret);

            for(int i=0; i<titleId.size();i++){

                //类别名称
                totals.add(i<title.size()?title.get(i):"");
                ret=zebravip_detail(titleId.get(i));

                if(!MyUtil.isEmpty(ret)){
                    ArrayList<String> names=MyUtil.midWordAll("name\":\"","\",\"",ret);
                    ArrayList<String> memberPrice=MyUtil.midWordAll("memberPrice\":\"","\",\"",ret);
                    ArrayList<String> nowPrice=MyUtil.midWordAll("salesPrice\":\"","\",\"",ret);
                    ArrayList<String> oldPrice=MyUtil.midWordAll("originalPrice\":\"","\",\"",ret);
                    for(int k=0;k<names.size();k++){
                        totals.add(names.get(k) + "|" + nowPrice.get(k)+ "|" + oldPrice.get(k) + "|"+ memberPrice.get(k));
                    }
                }
            }
        }
        ouputExcel(totals,filename);
        System.out.println("结束抓取。");
    }

    /**
     * 根据类别id取详细的商品内容。
     * @param id 包含类别id
     * @return 商品数组
     */
    private static String zebravip_detail(String id){
        return id.startsWith("AG")?zebravip_AG("https://cmsapinew.51bushou.com/api/cms/goods/queryBySaleCode?saleCode="+ id +"&activityIds=&pageToken=legopg_3044_1553180641886"):zebravip_SI("https://selection.myzebravip.com/api/selection/item?code="+id);
    }

    /**
     * AG开头的url取数据
     * @param url 包含类别id 的url
     * @return
     */
    private static String zebravip_AG(String url){
        return getData(url);
    }

    /**
     * SI开头的url取数据
     * @param url 包含类别id 的url
     * @return
     */
    private static String zebravip_SI(String url){
        return getData(url);
    }

    /**
     * 根据url返回网页数据。
     * @param url
     * @return
     */
    private static String getData(String url){
        OkHttp okHttp = new OkHttp();
        return okHttp.goGet(url);
    }

    private static void ouputExcel(ArrayList<String> list , String filename){
        ExcelUtils.writeExcel(filename + MyUtil.getDate("dd") + ".xlsx",list);
    }
}

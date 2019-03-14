package myNet;

import okhttp3.*;

import java.util.HashMap;

public class OkHttp {


    /**
     * 重载post请求，去掉报关参数。
     *
     * @param url
     * @param body
     * @return
     */
    public String goPost(String url, HashMap<String, String> body) {
        return goPost(url, null, body);
    }

    /**
     * post 请求，报头可为空。
     *
     * @param url    连接地址 可带上端口号。
     * @param header 报头，可为空
     * @param body   key,value形式的body
     * @return
     */
    public String goPost(String url, HashMap<String, String> header, HashMap<String, String> body) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder requestBody = new FormBody.Builder();
        //加入body
        if (body != null && body.size() != 0) {
            for (String bodys : body.keySet()) {
                requestBody.add(bodys, body.get(bodys));
            }
        }
        Request.Builder request = new Request.Builder();

        //加入报头
        if (header != null && header.size() != 0) {
            for (String head : header.keySet()) {
                request.addHeader(head, header.get(head));
            }
        }
        request.url(url);
        request.post(requestBody.build());
        //request.build();

        try (
                Response response = client.newCall(request.build()).execute()
        ) {
            return response.headers() + response.body().string();
        } catch (Exception e) {
            System.out.println("okhttp出错！" + e.getMessage());
        }


        return null;
    }

    /**
     * 重载get请求，无报头参数。
     *
     * @param url
     * @return
     */
    public String goGet(String url) {
        return goGet(url, null);
    }

    /**
     * get请求，参数可以直接定义在url里。
     *
     * @param url
     * @param header 自定义报头，可为空。
     * @return
     */
    public String goGet(String url, HashMap<String, String> header) {

        OkHttpClient client = new OkHttpClient();

        Request.Builder request = new Request.Builder();

        //加入报头
        if (header != null && header.size() != 0) {
            for (String head : header.keySet()) {
                request.addHeader(head, header.get(head));
            }
        }
        request.url(url);
        //request.build();

        try (
                Response response = client.newCall(request.build()).execute()
        ) {
            return response.headers() + response.body().string();
        } catch (Exception e) {
            System.out.println("okhttp出错！" + e.getMessage());
        }


        return null;
    }
}

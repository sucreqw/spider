package myNet;

import okhttp3.*;

import java.util.HashMap;

public class OkHttp {

    public String goPost(String url){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (
                Response response = client.newCall(request).execute()
        ) {
            return response.body().string();
        }catch (Exception e){
            System.out.println("okhttp出错！" + e.getMessage());
        }


        return null;
    }

    public String goGet(String url, HashMap<String,String> header){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (
                Response response = client.newCall(request).execute()
        ) {
            return response.headers() + response.body().string();
        }catch (Exception e){
            System.out.println("okhttp出错！" + e.getMessage());
        }


        return null;
    }
}

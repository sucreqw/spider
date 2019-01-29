package utils;


import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileInputStream;
import java.io.InputStream;

public class JsUtil {
	private static String JS="";

	public static void loadJs(String fileName) {

		try {
			// 打开文件读取数据,如出现异常自动释放.
			InputStream in=new FileInputStream(fileName);
			int len = 0;
		
			byte[] buffer=new byte[in.available()];
			while ((len = in.read(buffer)) != -1) {

				String temp= new String(buffer);
				JS=JS+MyUtil.trimNull(temp);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    
	//动态添加js代码。
	public static void AddJs(String js){
		JS+=js;
	}
	//清除所有js代码
	public static void SetJs(String js){
		
		JS=js;
	}
	/**
	 * 运行js的方法
	 * 
	 * @return
	 */
	public static String runJS(String function,Object ... arg) {
		String ret="";
		ScriptEngineManager sem = new ScriptEngineManager();
		/*
		 * sem.getEngineByExtension(String extension)参数为js
		 * sem.getEngineByMimeType(String mimeType) 参数为application/javascript
		 * 或者text/javascript sem.getEngineByName(String
		 * shortName)参数为js或javascript或JavaScript
		 */
		ScriptEngine se = sem.getEngineByName("js");
		try {
			
			//String script = "function say(t){ return 'hello,'+t; }";
			se.eval(JS);
			Invocable inv2 = (Invocable) se;
			ret = (String) inv2.invokeFunction(function, arg);
			//System.out.println(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}

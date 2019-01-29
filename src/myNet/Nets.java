package myNet;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

/**
 * 负责socket发�?�与接收.接收后的数据交出调用者自己处�?.
 * 
 * @author sucre
 *
 */
public class Nets {

	/**
	 * https数据包发�?.get/post通用.
	 * 
	 * @param host 服务器域�?
	 * @param port 服务器端�?
	 * @param data  要发送的数据
	 * @return 成功后返回服务器返回的数�?,不成功返回错误码.
	 */
	public String goPost(String host, int port, byte[] data) {
		StringBuilder ret = new StringBuilder(data.length);
		// 创建sslsocket工厂
		SocketFactory factory = SSLSocketFactory.getDefault();
		try (
				// 括号内的对象自动释放.
				// 创建socker对象
				Socket sslsocket = factory.createSocket(host, port);
				// 创建要写入的数据对象,直接用bufferedoutputstream 更灵�?.必要时可传文件之�?.
				BufferedOutputStream out = new BufferedOutputStream(sslsocket.getOutputStream());

		) {
			// 写入要发送的数据并刷�?!
			out.write(data);
			out.flush();
			//sslsocket.shutdownOutput();
			// 接收数据,为了解决乱码的情�?,要用inputstreamreader,用bufferedreader 包装后会更高效些.
			//BufferedReader in = new BufferedReader(new InputStreamReader(sslsocket.getInputStream(), "UTF-8"));
			 InputStream in=new DataInputStream(sslsocket.getInputStream());
			// String str = null;
			//char[] rev = new char[1024];
			byte [] rev=new byte[1024];
			int len = -1;
			while ((len = in.read(rev)) != -1) {

				ret.append(new String(rev, 0, len));
				// 由于socket会阻�?,当装不满缓冲区时,当作是结�?,
				int p=in.available();
				if (p<=0) {
					//break;
					int i=0;

				}
			}

			// 安全起见还是关闭�?下资�?.
			in.close();
			sslsocket.close();
			out.close();

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("网络错误：" + e.getMessage());
		}

		return ret.toString();
	}

	/**
	 * 普通数据包,非https
	 * 
	 * @param host
	 * @param port
	 * @param data
	 * @return
	 */
	public String GoHttp(String host, int port, byte[] data) {
		StringBuilder ret = new StringBuilder(data.length);
		try (
				// 括号内的对象自动释放.
				// 创建socker对象
				Socket socket = new Socket(host, port);
				// 创建要写入的数据对象,直接用bufferedoutputstream 更灵�?.必要时可传文件之�?.
				BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

		) {
			// 写入要发送的数据并刷�?!
			out.write(data);
			out.flush();
			// 接收数据,为了解决乱码的情�?,要用inputstreamreader,用bufferedreader 包装后会更高效些.
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String str = null;
			char[] rev = new char[1024];
			int len = -1;
			while ((len = in.read(rev)) != -1) {
				ret.append(new String(rev, 0, len));
				// 由于socket会阻�?,当装不满缓冲区时,当作是结�?,
				if (len < 1024) {
					break;
				}
			}

			// 安全起见还是关闭�?下资�?.
			in.close();
			socket.close();
			out.close();

		} catch (Exception e) {
			System.err.println("http网络错误：" + e.getMessage());
		}

		return ret.toString();
	}

	/**
	 * http数据包返回文件
	 * 
	 * @param host
	 * @param port
	 * @param data
	 * @return
	 */
	public byte[] goPostByte(String host, int port, byte[] data) {
		// StringBuilder ret = new StringBuilder(data.length);
		byte[] ret = null;
		// 创建sslsocket工厂
		SocketFactory factory = SSLSocketFactory.getDefault();
		try (
				// 括号内的对象自动释放.
				// 创建socker对象
				Socket sslsocket = factory.createSocket(host, port);
				// 创建要写入的数据对象,直接用bufferedoutputstream 更灵�?.必要时可传文件之�?.
				BufferedOutputStream out = new BufferedOutputStream(sslsocket.getOutputStream());

		) {
			// 写入要发送的数据并刷�?!
			out.write(data);
			out.flush();
			// 接收数据,为了解决乱码的情�?,要用inputstreamreader,用bufferedreader 包装后会更高效些.
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(sslsocket.getInputStream(), "UTF-8"));
			// String str = null;
			InputStream in = new DataInputStream(sslsocket.getInputStream());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] rev = new byte[1024];
			int len = -1;
			int start = 0;
			while ((len = in.read(rev)) != -1) {
				// ret.append(new String(rev, 0, len));
				// 由于socket会阻�?,当装不满缓冲区时,当作是结�?,
				start = serachB(rev, new byte[] { 13, 10, 13, 10 });
				start=start==-1?0:start+4;
				//清除脏数据（数据里的换行符用标准）
				int[] clear=clearByte(rev, start, len);
				//返回的数组，第一个是起启，第二个是结束。
				output.write(rev, clear[0], clear[1]-clear[0]);
				
				/*if (len < 1024) {
					break;
				}*/
			}

			ret = output.toByteArray();
			// 安全起见还是关闭�?下资�?.
			in.close();
			sslsocket.close();
			out.close();

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("网络错误：" + e.getMessage());
		}

		return ret;
	}

	/**
	 * byte形式的indexof
	 * @param source
	 * @param target
	 * @return
	 */
	private int serachB(byte[] source, byte[] target) {
		int ret = -1;
		for (int i = 0; i < source.length; i++) {
			if (source[i] == target[0]) {
				for (int j = 1; j < target.length; j++) {
					if (target[j] == source[i + j]) {
						ret = i;
					} else {
						ret = -1;
						break;
					}

				}
				if (ret != -1) {
					return ret;
				}
			}
		}
		return ret;
	}

	/**
	 * 去除脏数据用，有些网站返回的byte里会有一些自己的规则，或者socket里也会有一些长度数据，要做清理
	 * @param source 原始byte
	 * @param start 起始位置
	 * @param end 结束位置
	 * @return
	 */
	private int[] clearByte(byte[] source, int start,int end){
		int[] ret={-1,-1};
		//真正需要的长度。
		int length=end-start;
		//从中间开始，"两边发散查找换行符"。最后还是要往后第一个，因为有些图片数据里在确会有换行，以后遇到新网站可能还要再改。
		int halfStart=length/2+ start;
		//用来接收查找的结果，开始部分
		int retStart=-1;
		//用来接收查找的结果，结束部分
		int retEnd=-1;
		/*//前面循环查看是否存在换行符
		for(int i=start;i<=halfStart; i++){
			//找到换行符
			if(source[i]==13 && source[i+1]==10){
               retStart=i+2;
               break;
		    }
		}*/

		//直接查找图片的头部十六进制码 png头
		int temp=serachB(source, new byte[] { -119, 80, 78, 71 } );
		if(temp!=-1 && temp>retStart){ retStart=temp;}

		//往后循环查看是否存在换行符
		for(int i=halfStart;i<end; i++){
			//找到换行符
			if(source[i]==13 && source[i+1]==10){
				retEnd=i;
				break;
			}
		}
        //判断找到的换行符是否是有效的数据。
		//如果搜索到的换行符 位置不比起启位置大的话，说明无效。直接用起启位置。
		if(!(retStart>start)){
			ret[0]=start;
		}else{
			ret[0]=retStart;
		}
		//判断找到的换行符是否是有效的数据。
		//如果往后搜索不到 换行符，就直接以byte的长度作为结束，如果搜索到了就是最近的换行符了，直接采用。
		if(retEnd==-1){
			ret[1]=end;
		}else{
			ret[1]=retEnd;
		}

		return ret;
	}
}

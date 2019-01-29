package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SinaCapchaUtil {
	private static Map<String, String> maps = new HashMap<String, String>();

	/**
	 * 把base转成图片，并根据index位置还原图片。S
	 * 
	 * @param index
	 * @param base64Image
	 * @return
	 */
	public static byte[] getPic(String index, String base64Image) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] ret = decoder.decode(base64Image);
		index = new String(decoder.decode(index));
		// byte[] b = drawImage(ret,160,160);//cut(ret, 0, 32, 32, 32);
		/*
		 * File file=new File("temp.gif"); try { OutputStream outputStream=new
		 * FileOutputStream(file); outputStream.write(b); outputStream.flush();
		 * outputStream.close(); }catch (Exception e) {
		 * 
		 * System.out.println("wrong,,,,"); }
		 */

		String temp[] = index.split("_");

		int l = 160;
		int m = 160;

		int h = Integer.parseInt(temp[0]);
		int i = Integer.parseInt(temp[1]);

		int e = l / h;
		int f = m / i;
		int[] g = new int[50];
		int[] j = new int[25];
		for (int k = 2; k < 27; k++) {
			j[k - 2] = Integer.parseInt(temp[k]);
		}

		int p = 0;
		for (int n = 0; n < 25; n++) {
			// g += "-" + j[n] % h * e + "px -" + (j[n] / h) * f + "px,";
			g[p] = j[n] % h * e;
			g[p + 1] = (j[n] / h) * f;
			p = p + 2;

		}
		// System.out.println(g);
		return drawImage(ret, l, m, g);// cut(ret, 0, 32, 32, 32);

	}

	/**
	 * 拖动的图片还原
	 * 
	 * @param index
	 *            还原的索引
	 * @param Image
	 *            乱的图片byte[]
	 * @return
	 */
	public static byte[] recombineShadow(String index, byte[] image) {
		String temp[] = index.split("\\|");

		/*
		 * for (var t, _, a, i, n, r = e[0], s = e[1], o = e.slice(2), h = 0; h
		 * < o[length]; h++){
		 * 
		 * _ = 360 / 8; a = 180 / 5; i = "-" + (o[h] % r * _ )+ "px" +"-" +
		 * (o[h] / r * a) + "px";
		 */
		int g = 360 / 8;
		int a = 180 / 5;
		int r = 8;
		int[] ret = new int[80];
		int p = 0;
		for (int h = 0; h < 40; h++) {
			ret[p] = Integer.parseInt(temp[h]) % r * g;
			ret[p + 1] = Integer.parseInt(temp[h]) / r * a;
			p = p + 2;
		}

		// System.out.println(Arrays.toString(ret));

		return drawImage2(image, 180, 360, ret);
	}

	/**
	 * 拖动的图片还原
	 * 
	 * @param srcImage
	 *            打乱的图
	 * @param height
	 *            总图的高
	 * @param width
	 *            总图的宽
	 * @param index
	 *            还原的索引
	 * @return
	 */
	public static byte[] drawImage2(byte[] srcImage, int height, int width, int[] index) {

		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = tag.getGraphics();
		int p = 0;
		try {

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 8; j++) {
					int x = index[p];
					int y = index[p + 1];
					p = p + 2;
					ByteArrayInputStream in = new ByteArrayInputStream(cut(srcImage, x, y, 45, 36)); // 将b作为输入流；
					BufferedImage img = ImageIO.read(in);// ImageIO.read(new
															// File(srcImageFile));
					g.drawImage(img, j * 45, i * 36, 45, 36, null); // 绘制切割后的图
				}
			}

			g.dispose();
			OutputStream out = new ByteArrayOutputStream();
			ImageIO.write(tag, "jpg", out);
			return ((ByteArrayOutputStream) out).toByteArray();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 把base64转为图片byte[]
	 * 
	 * @param base64Image
	 * @return
	 */
	public static byte[] getPic(String base64Image) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] ret = decoder.decode(base64Image);
		return ret;// cut(ret, 0, 32, 32, 32);

	}

	/**
	 * 图像切割(按指定起点坐标和宽高切割) *
	 * 
	 * @param srcImageFile
	 *            源图像byte数组 切片后的图像地址 *
	 * @param x
	 *            目标切片起点坐标X *
	 * @param y
	 *            目标切片起点坐标Y *
	 * @param width
	 *            目标切片宽度
	 * 
	 * @param height
	 *            目标切片高度
	 */
	public static byte[] cut(byte[] srcImage, int x, int y, int width, int height) {
		try {
			// 读取源图像
			ByteArrayInputStream in = new ByteArrayInputStream(srcImage); // 将b作为输入流；
			BufferedImage bi = ImageIO.read(in);// ImageIO.read(new
												// File(srcImageFile));
			int srcHeight = bi.getHeight(); // 源图宽度
			int srcWidth = bi.getWidth(); // 源图高度
			if (srcWidth > 0 && srcHeight > 0) {
				Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
				// 四个参数分别为图像起点坐标和宽高
				// 即: CropImageFilter(int x,int y,int width,int height)
				ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
				Image img = Toolkit.getDefaultToolkit()
						.createImage(new FilteredImageSource(image.getSource(), cropFilter));
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
				g.dispose();
				// 输出为文件
				OutputStream out = new ByteArrayOutputStream();
				ImageIO.write(tag, "GIF", out);
				// ImageIO.write(tag, "GIF", new
				// File(MyUtil.makeNonce(3)+".jpg"));
				return ((ByteArrayOutputStream) out).toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据int数组定义的位置 重新截取图片并重组。
	 * 
	 * @param srcImage
	 * @param height
	 * @param width
	 * @param index
	 * @return
	 */
	public static byte[] drawImage(byte[] srcImage, int height, int width, int[] index) {

		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = tag.getGraphics();
		int p = 0;
		try {

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					int x = index[p];
					int y = index[p + 1];
					p = p + 2;
					ByteArrayInputStream in = new ByteArrayInputStream(cut(srcImage, x, y, 32, 32)); // 将b作为输入流；
					BufferedImage img = ImageIO.read(in);// ImageIO.read(new
															// File(srcImageFile));
					g.drawImage(img, j * 32, i * 32, 32, 32, null); // 绘制切割后的图
				}
			}

			g.dispose();
			OutputStream out = new ByteArrayOutputStream();
			ImageIO.write(tag, "GIF", out);
			return ((ByteArrayOutputStream) out).toByteArray();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] grayImage(byte[] srcImage) {
		int[] rgb = new int[3];
		ByteArrayInputStream in = new ByteArrayInputStream(srcImage);

		try {
			BufferedImage bi = ImageIO.read(in);// ImageIO.read(new
												// File(srcImageFile));

			int width = bi.getWidth();
			int height = bi.getHeight();
			int minx = bi.getMinX();
			int miny = bi.getMinY();
			byte[] ret = new byte[width * height];
			//// System.out.println("width=" + width + ",height=" + height +
			//// ".");
			// System.out.println("minx=" + minx + ",miniy=" + miny + ".");
			for (int i = minx; i < width; i++) {
				for (int j = miny; j < height; j++) {
					int pixel = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
					rgb[0] = (pixel & 0xff0000) >> 16;
					rgb[1] = (pixel & 0xff00) >> 8;
					rgb[2] = (pixel & 0xff);
					if (rgb[0] < 250 && rgb[1] < 250 && rgb[2] < 150) {
						ret[i * j] = 1;
					} else {
						ret[i * j] = 0;
					}
				}
			}
			return ret;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 设定阀值二值化图片
	 * 
	 * @param srcImage
	 *            原图
	 * @return
	 */
	public static String grayImage2(byte[] srcImage, int y) {
		int[] rgb = new int[3];
		ByteArrayInputStream in = new ByteArrayInputStream(srcImage);

		try {
			BufferedImage bi = ImageIO.read(in);// ImageIO.read(new
												// File(srcImageFile));

			int width = bi.getWidth();
			int height = bi.getHeight();
			int minx = bi.getMinX();
			int miny = bi.getMinY();
			// byte[] ret = new byte[width * height];
			//// System.out.println("width=" + width + ",height=" + height +
			// ".");
			// System.out.println("minx=" + minx + ",miniy=" + miny + ".");
			String ret = "";

			int StartRGB = 0;
			int pixel = bi.getRGB(1, 1); // 下面三行代码将一个数字转换为RGB数字
			rgb[0] = (pixel & 0xff0000) >> 16;
			rgb[1] = (pixel & 0xff00) >> 8;
			rgb[2] = (pixel & 0xff);
			// 记录背景色。
			StartRGB = rgb[0] + rgb[1] + rgb[2];

			for (int i = minx; i < width; i++) {
				for (int j = miny; j < height; j++) {
					pixel = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
					rgb[0] = (pixel & 0xff0000) >> 16;
					rgb[1] = (pixel & 0xff00) >> 8;
					rgb[2] = (pixel & 0xff);

					if (j == (y + 1)) {
						// if (rgb[0] > R && rgb[1] > G && rgb[2] > B) {
						// ret[i * j] = 1;
						// ret+="1";
						// ret+="("+rgb[0]+","+rgb[1]+","+ rgb[2]+")";
						// } else {
						// ret[i * j] = 0;
						// ret+="0";
						// ret+="("+rgb[0]+","+rgb[1]+","+ rgb[2]+")";
						// } rgb[0]+","+rgb[1]+","+ rgb[2]
						int rgbs = rgb[0] + rgb[1] + rgb[2];
						int rgbret = 0;
						if (i < 314 && (rgbs - StartRGB) < -70 && rgbs < 220) {
							for (int k = i; k < (i + 44); k++) {
								pixel = bi.getRGB(k, j); // 下面三行代码将一个数字转换为RGB数字
								rgb[0] = (pixel & 0xff0000) >> 16;
								rgb[1] = (pixel & 0xff00) >> 8;
								rgb[2] = (pixel & 0xff);
								int rgbss = rgb[0] + rgb[1] + rgb[2];
								if (Math.abs(rgbss - rgbs) < 200) {
									rgbret = 1;
								} else {
									rgbret = 0;
									break;
								}

							}

						}
						// System.out.print(i + ":" + rgbs +",");
						if (rgbret == 1) {
							ret += String.valueOf(i) + ",";
							i = i + 45;
						}
						// ret+="("+rgb[0]+","+rgb[1]+","+ rgb[2]+")";
					}
				}
				// ret += "\r\n";
			}
			return ret;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean compareImage(byte[] image1, byte[] image2) {
		byte[] ret1 = grayImage(image1);
		byte[] ret2 = grayImage(image2);
		int result = 0;
		for (int i = 0; i < ret2.length; i++) {
			if (ret1[i] == ret2[i]) {
				result++;
			}
		}
		double ret = (double) result / (double) ret2.length;
		System.out.println(ret);
		if (ret > 0.9997) {
			return true;
		}

		return false;
	}

	public static String compareAll(byte[] image) {
		if (null == image) {
			return "";
		}
		maps.put("1234",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsuTDBwACEIhg0uCCAAIqtJxJkyODlBRq6tw5sQEAARN4Ch2q0AEAAEGJKl2KMoCEpVCHQjjKMqpVmgKOHm1wtatJCUcfeB07EmwAsWTTepxw1IHatxspbIVL9yKFAAAY1N070ULWBTSbCoDAt7Dhw4gTK17MuLHjx5AjS7aI8qiCC5Mzk7wZIKfmugwCdP5c12iApKTfmn6a+u1UAFVbdyUs8HVs2WMjzMVNFiwAtLy9SsDrNrjXCXi5Gu96N+9y5n+fS0fY9Oz069iza9/Ovbv37+DDi/8fTz6qBNrjs2pdz769+/fw48ufT7893p/hKWDeyVlm+ZmcefafSaGNNiCBOB1okk8GKjiSaag5GNJqEj6YEmsVgvQahhl6ZFuHGlIFIkevIXAUeiNulMB6ARjgAAYpYqTbbzFy5BtwNWJkFo45WjQcAMX1eNGPQQpZ0QRZKWdkRXIBoOSSEzWnF5QUSUllldFdKVEFWZYkAYfimXhffWTWNyZ9Z5YpH14wfRfBbVrGKeecdNZp55145qnnnnz26eefgAYq6KCEFhqRUQb4F94CCiiQQKOPOgrppJJWGumljVKKqaQJGJDSUQEUYKmmpI5q6qaYRqqAWhNM8OWrsMb/Kuusr7ZKa6wJsAnAAA/c6qqrtwZLK7AmFhDeAwIwAKNO/RHa7KABOpsgtNMKGu2gDAoYaLaEQthtShECSuGg4wpabqDniosXmH82xa6f7hJa2bt9fjiovYLiG6i+fF7gZAZTBYCin7l+CqefFnzK5pN9zjjAAhPs96dvA08cFqE3YnzxoBlzvLGgRBLKFpAityUycSJnVSSgTTL8Z3Mu+wkzoTMPaqXNeE0p6F0B6Bwozz4DWgFegA3KJQBF75zVqi0ZlYDE4Q0NwAG/SlD11VZnjfXWWnftqgJahQqB12RzbXbZaIfblQGctv3o227HDffcchewngAHvK23o3QnI7C333wHDvjgf2P6HUoHQG3o4ow37vjjkEcu+eSUV275UgEBADs=");
		maps.put("1243",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKyqrKyurLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTi5OTm5Ozq7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAnCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsuTDAAACIIhgEmEDlS1jyizZAMCBmThzcqx5U6fPnxJ5Ah1KVKHQokiTHk3K1KeDlFAVNJ2acynVqy2tYt0qUivXrx29gh2LUSxZkREOAFjr8yWCs3Djyp1Lt67du3jz6t3Lt+haAAMa9B2s0yxhuIYPk02sGCzjxlwfQ8YqeTJVBgEMWJZbeTPTzp6Rgg5NdDRpoKYtMxjw97Tr17Bjy55Nu7bt27hz6979eiVvgQj+Ch9OvLjx48iTK1+efMAC3c+Hpv4d1ib1n9Ova8yuvaz17ji5g/+vKH78xPLmI6JP/3A9+4bu3yNkIBxlgJ7yP0YIgJJB/pE1vfVfV98NCFJ8BhaEYIIDLcjgBA4yGGGCExpY4YAX4uVfYQU2FRx/KdknYogkgmjiiCeWaN9fKLGoooopxojijCVC5dxvaT2o44489ujjj0AGKeSQRBZp5JFIJqnkkkw26eSTFaWkgAS8LYDAlQdgqSUCWXK5ZZdgbvnlmAesCIAABoTp5Zpqtjkmm29GRxUEDdRp55145qnnnnzWueJ9DPQp6KCCJgCAgLgBQIADqB0K5UEBPmqQW5Iq6GilDcKEqUCRbgqhppt2GiqomIpaKqmVmpoqqpKq2iqrj7qvGiusUMpaK61P2porrk7q2iuvSxLQ4loBsCSpWmsNQCWmagXgKYQdpnrpqIiWOu2p1Uqb7avbxnrtqt3W+i23z/raJKWemsskuqGOO2u4ubp7K7y9yrsrvbI9QAAAbdk7FJ2EBiywnWVCFcDACBNqKL4/LZDlw15CLHHEFE9sccUFCGCwTQc87DGXEn/cMcgkj2yyyGDKmZsECqD07MswxyzzzDTXbPPNOOess0kBAQA7");
		maps.put("1324",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcqCCABJKqqRYQQCAAApWSlQAIILMmwYpBACwwKAEADFxOqRpU+hInQAYOPwZ1OhCok41StjZwCLTqE9RYoU4FYADj1ObbjUIdWzBnwAeqPyZwGxBAwDiUhXaYKfcnkIh7Ayg1i3BsiUhvCzql63fgjRTenwQQOthhFcfCzypuKKDl5UlM4wsmfJDBi8paLYKdLRng6ADiB7NkfPjkwgACKjAmmTY2rhz697Nu7fv38CDCx9OvLjx4yoBI2dIwWVQ14eVLxeIVGlC6H6lC58wNyJ2t9pzd//9mvG72fCF4/YFaVgzeqER1K+U8OBBggAFHkCA8LhA2rxxFdUAeUa111lNIz0QIGsGvuZYRg7slJlu5o11WkQNYGZchVtdiFBqq00nEIdYXQiiiAuRiOKKLLbo4oswxijjjDTWaOONOOao44489ugjRBYgwNp7LrbEk4pOESkiUniNCJNpCLpYHWRPuhflkjtZp9BtVhJmHAUvadkQlwd6GRx3AFQ1EZmvXenbeBixGZ2b4u1EYHlVlineS+t11OCcZqb330h/ZkfnVmj1aRsAbR1G3wP+JQDBAyGudAEECrj00p0qJSDXSwc85qmERgn2KQZOMSZklzeZGmhUhYL/d+hHCs46Vqzn2ZqRgg+OhquFAEyIUYTB8ianocJOdFmvxubZZrIOLQtts2LNOS1CGQYwAXLHynrtQCeK2G2u04bL4rjATmjui+h2WOy6MraL1QICFEBbjhII0OSP/Pbr778AByzwwAQXbPDBCCes8MIMN+zwwxBHLPHEFFds8cUYD8yAAN++WMFOBkDZMYtGHlCalSOLaGRPv3bI7IvN8TRQyyUWG2PM+2ZAc1QesqhTAGLOzKjIUmZ53dDuvYzclFsifWDKvjGdotMOQr0bmGk+tHOSNn8Zl5paUx2d0r8hBTZXYiNL3AQune1d2uCRrRuanEq0tVGJATdBXHWv/wn3eXL7audGdwuV925wtvZ3uhTy/RGSeHftq3zsndxZ4IhSHpK8PEvuV3yDEmp51Y+BrqhInHNtNUmm3wS54ZirZIGnAPA336dx6YqTqS/JdUGrufcugFAXaPrS736BadcAqAoVm13bGkXAS5pdsNMApQaYYQGwjm7t7gsO1HyBzn6/kquOep9d7Bqh/1jqka8+kfuSvY5Tzx3x+mph5atNq4a4sd9NlKQsAO6pUXraiLR6U7j76Q4iC3zT4ty1PwgaUIIIbFMFGxLB4DRwgA9MyLKiN5wPyoSABhnhcUy4EhQOJFsk3NAEa7ZBgmSrUiucYedqmAEGNAaH3NIh10826EPVtIiFybEVvFCExJJoZ4kraiJJlAPFcwkxckVZQGhqJMWREEWLAbgXF69oOLmI0UYCzJga18jGNrrxjXCMoxznSMc62vGOeMzjVgICADs=");
		maps.put("1342",
				"R0lGODlhoACgAPMAAJSSlJSWlJyanKyurLy+vMTGxMzOzOzq7PT29Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEMI/wATCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcmAAACVTXgSAUqVEli5jRgQQQCZEmjZzLjypsyHPnkAJwgya8CdRoEaPFhyqVCfTpgkIsGRJAKrLA1NpBjhgNQHOrjK/gk0KNiVZqE/LlkyLtqZalWK7nn0bcq5Sti4N0AQgwEBPu3QDCx5MuLDhw4gTK17MuLHjx5Adxo0cETBRy5QV4r3bMvNMt2Ane/bZuavo0QtPK8WMuiDrv6Vb74yNlrZshJuPqr69FLRc27xdA9c9PLjA3ER3G/fqe3lDvQEIIHBOvbr169iza9/Ovbv37+DDi/8fH/P1bfM5kRtXHhR9a/VA2Qd3H7b4+uZQ6Y+G31M+b/0u8XebfzoBmJmA6dkXHII2GUiZg2vhVx2BMQ3A0kksDWAdAgKwJMB0VkHnV3UIFGAiTSYWAGJTDKLWYnkK/hejUxJSR6FNL3qWo0oQQtbjSDe6WCNx2+1o1oyy/ShSkPsNmRyS70Epk5IVGTAAV+1JWV9WXHbp5Zd7gTkVhmOKaeaZYgZA5Wgrkufmm3DGKeecdNZp55145qnnnnz26eefgAYq6KCEFmrooYhex6SOTga1aGZrfhSpYkaupSWjaj364KU8ckpZpSSB+pimEW43aUenIibqkp5GRmqojc7/1ypJqR62aki3MvYqkLHKqFathuX6kbCU9kqjqbPyWqSxOQFbmLMYEZuYtBzt6pi1dSXrGLQradsYtRchYMC4LI1rQJvLiWjVAR1euJV1BZx0UlVQcXjSh9YNoGZWGloFbrDegoStrsw2GPBi3Fr0b2ELR1uwkL8erFjCFTU8mMUKP9xkxMhmKvG0H1erMaZlUUyXyRNhHJjKFA28mMseocxxWSy/VfNLIx+Y85Ed0xyyrT9rBPO0O5ckc8lBZ3Szz5kWjRF09Pbn9EjyyruX1VhfrXXWXG8dptZeY9312GF7feF40PE1YqIRWYkl23DHLffcdNdt991456333nz3A91QQAA7");
		maps.put("1423",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzOzNTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJkiCGAgEMXCjJ8mEDAAFeKmjJ8SQAAitphmQQIICEgxAAJNApEoOBmxaIQmQA86fDoEOVtsRwAMAACwgAOBiJAYAACgu8TrgIVarZgQgCEKigUYHYj0Fnnp1r8ECAqwrdfm35IIBcuoAPZg1glYLSowASw0waWGrWAWwb6jVMEoLfxi0fM+Y4uaPlv5g1UrW6WWlYAWMpfg5N0ajVnKwNMg2QemHZ2AltFiiNWyJPnwUfAADNOmsB2L1FNiAcNbnz59CjS59Ovbr169iza9/Ovbtz3ci9D//87TTDbewn1fKGPrs2wvPiRw8IbzYs7YnwxRPMmpgwgAUhVdCfYpCRJZR+EmHwWGQQuRUAZSHlh2BFCwqUlQAM0pSAVxNe9BiEA224F00SdohQhRU5CGJHwhHXoXzrgaRAAKhlVGJ2oxEQ41z2ledQi+gdpWN2s/lY0GrP6bYjgi8Bd6OJUEYp5ZRUVmnllVhmqeWWXHbp5ZdghimmcRhw+RIACEB3QQEAFFDmlE3+9CRuruGEIHlHHnhdenZiV2RCc0pX55Kh/clQoNVRpR5rPUaEKHpV4VUfTO45qmeUGAgAE0wj2ZfYAzZeOuUFDgxwV4ZtwbRApQY2hyWMFqn/GFJfLmqpmUMOogpSXGJmoKBVul64YmWi9prBYF7pypJbMA3w5piFCSSissS66uWHCok47KzDXQusRAnQuC1HjyL4q6QYhTuiZ5dVeWtIerFaUbmQ3kStSKfJCxGQ5lY1JGD54lcsjgbcRR9m7T2EZJCvVWdoQvxSx+fB1j1MEL10KQnlwwvTyeZxWDbJlLXGlmzyySinrPLKLLfs8sswxyzzzDTXbPPNOOes88489+zzz0AHjSBWFFvJAAP3ArbmTUVH2WS33wnZNJNN8QqdTX1u3JRAHSf58dR+bj2Q1dNdcBTI3lnMdbuJSr0dUwIYOTbUe7pdsdjvsc1w1uzh/w0o3dpN/JzagOq9nWvz4ZawwoB3NyjClErUtXc5EsoS3Poe2rh+lVuYtEYIUHaa3BBN3u+maI4U1KY1Glgrgvz1R/pGBCgmLkamcx7BAmz2Z3lFiMGUwAOfN0S2lZH+3uBbLBpe5bnKLxTvrgNf+a5EnUW4+av+Fk9QriTRCuavGDIEfkkYT3khtbK2dHyYKAo0vU59pWnyh9oqdUFPAIBqMgYDgAkEpEIB1AGgASX7ELZoIpwBEcZ+YIpfBqZFFPFBq0AH2dAAxrUr521JgghRFwc9U70rnct7IfLKCDUSsSydkCL5i5AHqQTCiWTPI+nrzvUycsONtFBKsPrI/Kg08j4TBVEkQ7yIBaHUuZYkUTUzdFz3pBKweZXwcMmjSxUl8kPuHBFgMFkhUKJoHcRFjyijK90VJXaUxDknjQ3pYtvaBDbMwFEhuYuO4O4GnL+RTI8o4dt18HQQOX4nkHWUDuHyGBuNTchiOZQKeKRkKENipk6J7E6RGJmx2qEtZDAhI10qgIBMmogBC0Ch0FbJyla68pWwjKUsZ0nLWtrylrjMpS5HEhAAOw==");
		maps.put("1432",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyurLy6vLy+vMTCxMTGxMzOzNTS1Nze3OTi5Ozq7Ozu7PTy9PT29Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAnCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJkqCEAgAKSCjJ8uHJlCtbcnypUqZImjFt2sSpEyLPnkAZ4oww4EDLn0GTXjwZoGZGpk6VSh2J9GDVqViBSiAAAGbWrz2vWkUZFaxZilu9boSa86zbnxEayBTrliRdsHfrRsyrNyHft2TbtmQQoCsABWELNBXct7Hjx5AjS55MubLly5gza97M+Wjgzgb/ShZtljRogabtfj6tkajhwgZurmadWK3P2bTr/kydO2tV3r3n4r5tO/hI110DLKA63Pje5kmBa5aOlfpZ65GxOy8oAUFhxNvDi/8fT768+fPo06tfz769+/fw4xMkXJY8UwaOtWeHnp//Zf14+ddfcbQByJKBlCE4k4D2McgcgfGlZRgEqkEoH0EQLGBAYQQwhpGC62kHonymjXhhaM2ZeKJQZBEgQIcraoVShyrGOJaFE9R4Im86huggizjaOIEDBwxgGAABRABSjwXOaOQAB/54HpMNUfmYlRNh+ZWWT0lZGZdLetkXmJ4FqZuYHD1gAHhl1ufWkXDGKeecdMZZ2J115qnnnkgiKSRqfwYq6KCEFmrooYgmquiijDbq6KOQRirppJRWaumlmGaqHgMejicBfnqRWR2atbnZm6gVoSqcmaGSeqWrAbL/OhqsgMnamKoPmjqgrp3hKpGvO9HaZqfGAYuQsUohi5qwpwGrbKxOReDAqry2RxNXAkRpK3sRcGXYcrJtyx6RAryWZJjiXuhatb8qxq6gAD7bK6zyssYjs/A6WO92d+3rKW7+modTwOm9BACMmrpUwIv4Prpbw4qKRXCxAk5sr5QWbyZdxpaJCLF7CHKsFwQMGNAVwgum2x5yfcYWksi+cVUYAA9U+C57OEFQ1LA7fpylzxoDnarQXxK9lNGvqhys0plxDLNFT+eI9FRRmzS1jExfdzXPoFU9kNcORa0qsR85vTVCCSDJJtd1MdAnnnC/LXfcdNMtt51z52333nrnId0VqPItUK7aCU+k5tqFJ6744ow37vjjkEcu+eSUV15oQAA7");
		maps.put("2134",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKyqrKyurLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTi5OTm5Ozq7Ozu7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wApCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJciACAA1KJpRwAIBLlTA7IgiQMqbNmyFn1sTJs+dFnT6DCoUIdKjRowZPMkDK1KgClwACAHDQtKpPpVaz8py5VKtXmFi/ih0ZdqxZj2XPqs14cifOAFIPRFhLt67du3jz6t3Lt6/fv4ADU2wwwGUAwWxpIuZbdHHexo7vQo5cNy1lupYvqzUAoKtmzJ0/Vw4tem3m0l9Po9aqerXrhYYHeH5Nu7bt27hz697Nu7fv38CDq2Xpe0FhqMiTK1+uXCrz5lGfS59OXfkB1iiNLvDdVnjQ7t57gv8Pj3M8eZvmz4PNrh49+/brZ8Mn2Xo+WtL2Q7Z0Hl1+fo4MRBWABP+RhV+BINWHIEYKLmhRgw5SBGGEEk1IIVEHXshWhhr+xGGHFVkI4kIierRdcMbBJRVcArbI4osrxuhidDC6uKJhNNqoY408ythjjgj8dgCBIxZp5JFIJqnkkkw26eSTUEYp5ZRUVmnllVhmadYECkj12wIIhHmAmGQiMKaZZZ6pJpljrokmmgYIkNybbp5JZ5p43qnnmSdadVICDQQq6KCEFmrooYcycAB/ATCA6KOQRjooBNj5d9MDBADA3YdWlsikp0uCqqSoSZKKpKlHomqkqkWyOqKrIMK02qGsGtJ6oa0UpoelrldOtut7WvraK7BZCjulBM45R0CWExwHQJBaUiDnddFSwGunilV7bZXGckvsrtlG2y2V25IbbrDf9npuseli6xa774Ib77Dzulutte1yu+6v9erbL7n5lhQAAQ8A96ekCCc8KI4BHKDww5JSqtUCbaJZ8cUWZ4xxmxyb2fEBIEdnWAEgl2zymhtrrHLKJffJG1wJTHDvzDTXbPPNOOes88489+wzTAEBADs=");
		maps.put("2143",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJkqCFAAEYlDwoYQCAAA9WylxJAaXKmThzzqwJoIHOn0BBTggAwEHQo0gvSkBpNKnTpw0jEIUAtapVgg4AaAUg4KpXqBBeRvhKNumDlxLKqgWaNcCEtXBxNnhJIa7dkgzo3q0KQQDRmDMXcK2wt7Dhw4gTK17MuLHjx5AjQ72gYCtgyR153sSMWTNnzjx9fpa8tOho0i8vn3YcQStVga9XK25AdKxsx2EB2L7NuG1a3oznugUeXC9xxYIDED5e8QFKAKqZS59Ovbr169iza9/Ovbv378ch7P/mLmCr+fPo06cnqt48e/bt48ufb77rWs8/L9QFT7AC0c38IYVfgAISJRqBR4WGIFJDmbZgUKU19eBPEU4IVGnRWSiTVADEpuFMrXX4YU6tBYCAayOGhEEDBsAXQAIpzhRWAOPFOFJuNdoY0lkA/KbjSL79SFJbbwkpEpFGiiTcfkl+tGSTIOUVAJNQciQllRNKkGNIyS2nVnn0hememC+VKR98ZNJHFAIIRrBllXDGKeecdNZp55145qnnnnz26eefgAYq6KCEUleBAdB9t4ACjCbQ6KMKOBoppJJW+qilk2aaQAFasVeAppQyGiqopGKKKaNqIYqABKy26uqrsMb/OgGrs8ZKqwMEbPXirLXK2qutwAbL61oWlKfATysKIOF3FZS3QKEG+QcAgNBmMGC11v6H7UA1pbStQN1SC+211Sr4rbnbTlDegdg2uGy17n6bQYXf0rsthvLiW69WGRYqAb/5AryviN+G6GG1Bmcw1wXV5vYSjIVmVSZRFlSLwQQLuETwtjjK2/G3ZwXgI7Yhj1xtyfKiDDJa8gb5rcvbwowtki8bF7PN2D75rc7bXimvz98C3TNKWEKblwBFF3p00t9dkECiMh3tJVkUSMCr1VhfrXXWXG/tddcSQMDpVgqAfbXZaH+99YkBVEzWpI4mEPfckdItd91436033HzHK40AmC8VoLfdcedNuOGID66AARgEeMEBUMsr+eSUV2755ZhnrvnmnHdeUUAAOw==");
		maps.put("2314",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcqACAA0CoCzJ8uGFAgAKYGhJUUEACAclBAjAgKbICwQAGLjgU6NNnA4nCACwoChEC0EPEHX68ehFpQAUUFg6NaQDAAgqRO1KleRJpB23BlCg0cIAsDPLlrXKciuABAorLEUgty9BCypVAhDq1O5Ovn4TIzz5oKRdvA0rvEUQV/Hcm2mXsu0IFa5llowrUlC5mWpnA5U/GwXQmKGEpU1VIwQqNLXsh6EJ6gTQ87bFl0LJ+i6YYHCD4SVfBkCMvLnz59CjS59Ovbr169iza9/OXTbdDLt7d/8vSFtm9O8Iw1OnPTQ7+odYY/vtLHW8ScwWLSwdDEBlhZAM8CcYc/YR9J5Hj0mkn2cFQiRAAJD5pNddAk1IYIMV5dbSY7ZJBhaGFmmI4E4RTuTWcraBeJCIopHGklsAHJBigywyFJ9s9M2IXY0D6cQTdhbAZJ51oamn4kDADXnkkkw26eSTUEYp5ZRUVmnllVhmqeWWXGZX3EpUYgDThaqdBZ5K4h0JVABKImembjulyR17wjn3Zk5xYkfndXcqZKRze2rXZ0NYlWYZfXW6BwBaEt1Y1ontYTgoRQ/wx9RIggUgQKIFTmrRBAu4yNlbAzzwn5OeaoRViQouJSOVqab/pRKrCS14QJaxgkTBW7RauCUGb4la1IQqkbkljyRxmIGHxmKJ7EcJJsTssayNFC1EzOqIarUILkUrRTBSJuWzEo2WlUgwvtokuQ2Za+iLUWnLHbsImStfXznSyC2hmiF3mrzTsfvapdOVB7Cd+8L543YGV6fhnw0mebBlJ6UE5pMSd6nxxhx37PHHIIcs8sgkl2zyySinrPLKLLfs8sswxyzzzDTXbPOSFTAgp5QYHGCBm/0FcBzPQRXAqWKM7TZ0kxgUfTTF3Cq9ZNMxTZyYiBCPt2ak0NWYdXZbP+0bsl+vFxTXASdsUNnPQRUA2tTRy7Zvp4n9HL0CTaDSvXSf/21312ov5OPOitU9Ht4F7cY3vmN1GnhSsM3XuL6tUeRoBc1yhLlA+YKIuEJYBcboRwgMppK6Kn6ekFr9ATCASBK0Pljmhz8eUQUPJLDf2yBVIFhMC0Rg9Y62W+QuR7aOW/xFdr1r4mTD26d6uTs575B+Adxa5fQTNf+Uq84uz5FaCgCcPK7iZ0bhQedrWXrlG3r7115dVtrf392CpRbtVDYgIAAUoEoEVDKA6DmpdKZrHfxKohcI2YV/21tUUXZ1l8r4qksHEgkFv7Us+lFrdCFRCwcJki1c4UeD8ntICa2UK4xc63Zv0R6sTqi+EaowhrCSoKzW1xYcRqmFETneR6zSBSUgOkSI6IqhAflEQ4rYiyYnQp3ndGi5fjklXUuUTgb5dS58DeBtWbwbFeGzt89gsVNNFJwVcfRF1NQOhOmJXHMgFUbvpHFtZYwOHQU1RjwSjDpBqs2O7ngm3mhna3Xsy6TmVp2GBYyGjNST37Sow0gyrGiJpMlRYncxJmWsOSdRydKgRLU2yaYCCyAclC4Qlpu58pWwjKUsZ0nLWtrylrjMpS53ycteJiYgADs=");
		maps.put("2341",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJkiCGAgAKZCjJ0mEGAylXtuyIgUDMmSFfpryAs6fAmgAMyPTJMMMBAAMwEF3K8MIAAAeGjpwAAAGGowQsMN2K0WkABFIrZkCAVCvXsyMtPEWwkOwAs2jjLiUbAEACuXh7KgAQAK7CCgLs5h1sca+ACh0BBzDgl3DeBQAEUMDJUyCFwAsc92TAd4LmgRTqMviMkXMAz6QhTggQYHTqgqYbSIgwuzbt27Zz4949O4Lv38B9AxjOVwGE4Mh5K9fN/LbvBqxdv55Ovbr169iza9/Ovbv37+DDa/8/GVSpeIEZUBao/P0CgQAqq+tMet6he6hhiRpFyr6+Rq9ghQQBcXxV5R9OalWV30JjlXXgZxYExpZAbjX2IGl7BYAYQRECoMCCF26V4YYTVVCXAiGyZNhkIwU2nIYpUgRZACzGFRoAmcU4kGmoXbcaANJxx2OIPwap40R8NWDekUw26eSTUEYp5ZRUVmnllVhmqeWWjh0Vn5PpQfUaeV8+GOZ62QFVpnc6oQmemiCm1mZ//t0nlJwGBDAAnTreFxVe+9FnpVd/6ocVn1k6JSZJKL1oIZcFKTohRw0GsECPkDqUYIAUNfhWphlt+lCFoILU4aQEkVoqSxHqiRSJq6r/GJlWgAkWq0gjJlTrXbdmlCtEu/YakWGwVmRiACgKe5ACAUgWEmcEKlgqZM6W9EBkELCW7JYz1sjUZThWaZq3eIGb45FDUnejkeKlu91qrX3nrnjwsptaA5FJ0FNQ5Lb0YwOpVYBAAgoQbHDBCB+scMIMK5zAwxBHnABxdQlwgMQYK9Dwxgt3XPDDBSNQrLIkl2zyySinrPLKLLfs8sswxyzzzDTXbPPNOOes8848d3eBVVGOhahcYRqwpI5nxikXmUqHd+bRqTFtpnpQUwfUneKdOXR15GHdXZgEVL1dTYs1TRrYYnt3AUpeUzdf2uG5V/Z0bcJdHwYDzK3Z208S/2q2T/sRsHWfebe9VeCDN+mV4T0FmjiUfv6tUQa8Og6q3yVBK4Cej1+paKEgXVBgXajG+nlIT9UVwAEOjLyqpJI7pKrKfmY0e8uwU3Q7zLmP6uDNojK0O/BrJUSWAI/mHPxAbrne86ZjNZt8zwOZyJfz1A80sQARPMVr9gURW31g32cvvkHB9vyrrk9te/P6DB3rvszwP1Tr/C3XL1H6LK+40f0qy1C/MmI9ABBgARGInZWoNcD/vYgvfYkVA0uSANWFa1qRaeBHLhBBc0GKM9VaigexNK64TAAz4uKLBpliPXvpaF6DKVKTYKgZGaaIhqmxoX9wSB0dgocBrMHUu15EI68gnseH18HXaR70ryR2BicRWEoTX6NEIbLkRQronEemqJkZReuLYAwjGC1IxtEV6IFlLKMY1zjGaJ1LS3VBCgTAp5AEaJGOeMyjHvfIxz768Y+ADKQgB0lIhgQEADs=");
		maps.put("2413",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyurLSytLS2tLy6vMTGxMzOzNTS1NTW1Nza3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAtCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcmACAAMAFKBQsuVEBwESuJR40oFBCgVUVpjJs6ADAAh6PkwQwGZDnDqFjvwZVClDokYpIi2w0ylGplYX1vQ4lWVWhzBlfkV4skHLCgRUeh07ECvbghICAAgwQekCAHhRShAKQS7eAAreEiwrWODUqiTdFhYIdTFCnAGoegzrmDEAs5UbolVrUfFiwpkpbl4JFmhm0KE1jl7b1nTlxqlFVigQIK3Y2Lhz697Nu7fv38CDCx9OvLjx4xO3WjiMvKHnwsof55TcvHVTx7AhMif+XHD2i9t3d/9/i/oj2siIHY/PevLvXAC3SW4eUHfs+rHlez7Yu3x6+pYNBHDAaZeFFt5H932VH26raZQgewUS12BED1q14HETIlShUxdWR9CEG3oo4ogklmjiiSimqOKKLLbo4oswxijjjDQ6dBIB/7EYAQIMEOjAgSmGKNR3QJIoZE/fEVRkc0fyFF1CS3LnGnYARHWUf8c1OdOT2mEpnJYucTlRlLGB2ZKYFpGp3pSfRejRaDkuZmZJHXKUYWFzklSneWmR9laeITHgF16YzTTBoCgl8ABPDbzn3nXkuSlUSgMMgJ5Qh85VKHSSfqXmZPARuKlgn2YEqEh7slUqRaeGlGphd17/xSano+oW60uzetepb7eWBimtGPbJGkOtgvTqb736lGuktTaXbLEfHWvcndB6JK2zfVbQaHyFMYAABDFWoACPNZZr7rnopqvuuuy26+678MYr77z01mvvvfjmq+++/Pbr778Aw9tAnAFXhABefrr4E7e6NpCtwqG+VuBmBAx74sKiDjQbZ0HGlPGHwqKIscTNJoscZSQnZHJxI1PZLMgcV4eyy5qFzGTENDu0sm8zt/myyjZLyTCzE+2cW8/Aihb0by37fJHRmSHdcEZQy4mz06otjZvURNupdWhNc2rlRhtTl1rYuo7NEVIVn301dEWNxDbB9nkssdofrdoT2uTF/92S3i5xzRaaIgGe2NtjTSBAXgEEIIDFhacl4AIRZCX4Vwa8Z4BSBzCOUn0z5eQoXYvlNHRLEcglV9tD5jUA3RZWaeEAEmzMOk8HB3Ca32OVDblI1XZEuFNVm4p433h/VXxndlOZfO9fd8T34LxXtjyFzbf5vGBwInj84LLnZjvsuJ6OX/i6Gb7Q9PhVn76XF12uIPq9qW8Q+wq6Xz/8EskP4fbvS0r/vtc+AO5Gff6LnQF5Azj8sUd/xFlVAjlEP+R8yoEWguBxyDRBpQxvg/xrjfnyt8DiAKmDrSuhccKDQQ5pkETMQSGSKpiiqQjqV1N70cYIWLAe+vCHQAyiEBGHSMQiGvGISEyiEpfIROIEBAA7");
		maps.put("2431",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSmpKyqrKyurLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTi5OTm5PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAnCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcmCCAA5KqrToIECClRITAGgAsyZEBwAQ2Hwok+bOnwlx6gS68GRKokgHCk2aUOZRpkRbvoRq0CjVqDmvDlwAoGuAAA+0wnwAIEBZAAvETnCq1ubStlbbwpQqt6fcuVnhzry78q1au3xL+hUb92cEBF0BRHUZuLHjx5AjS55MubLly5gza97MuSLgzhIHa/0M+mbevyhLhz5NOLVq00NRP33dUPTVA3tpO7RNla3uhg0CHKgLYPbvoKxH5z6ukDdU0swPOmcKPXrB6UmrW1eafDvDBgO8ev8fT768+fPo06tfz769+/fw48uHySABhPQQEjAg7vM8dqTaefcfUQFuNyBQBVp34E8JRrfgTr75111vxaX3oE0RmndhTRmWtyFMHZL34UohjjdiSQ14FUF5KSZmVgCxJYWYV2f1510EXy03mmsSTiWbhRM+x6OGQVKno4dFZnekiEkCuKSJTRL4pIBRIjilgVUyeKWCWUK4pYNdYvjlSja6FWZNDAzw1YtsltVmjnC++aKbdMaZ2J1y1pmnnXzu+eYA+803wWGCFmrooYgmquiijDbq6KOQRirppJRWaumlmGaq6aacdurpp5CVSKWPhFWIHk6kKmcck6leVZiGjOn/tSqUrfY2pIex/mVqj3XdKmKupc46aq/CGgiscmWyyp+Fx7o65nGoLntqsxQmS6u0/lH73LO/RauXtcN+C2St24JrLLlGmsslukqq66C26Y4rlwAwMssuUBKEZ1aM3hVglosrQoXjWV0RYN6MAAwgga7uModYANgSya+zDUMLwL1SVtwtvO3KK+60GFupsW7e6lrsusSdDGbIDPpqIsdOqsxcya3JDC3MUtrc7cXE2puyxz+C/POpPMPlsoA4W6kzyUUHnS3LXi5NG807Sv0aXbImBUEBigFFtbMKOCD22GSXbfbZaKeNgAA0pu3223CTrUDTajGAQAJ354333nr3NM33334HjrcB9BKMwOF45614AoA3LvjjiesnqAQLmAXq5ZhnrvnmnHfu+eeghy766KQzFBAAOw==");
		maps.put("3124",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTi5OTm5Ozu7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wApCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcmCCAA5KJgTAEkEElQgdAEgAU2ICAA1q6oToIADNnQ5v5gRKNGHPn0UVCk3KlODRpglPpoTK9ClVg0uvFpWJQOvABSwBBADwwGvNB2NZBlhglsLNqW1rco3rFiVduT7pZr1bcm7cvXxH+m0LOHDIwWYLG/4oE+nix5AjS55MubLly5gza97MmWPYAXA7a1QsuqFVwjhLSzydOLVqnnn/un7tkLVX0rRjAuga98Ds3AsR3wYQGnjCBgEO6CVunKFwrbibD3x+Nbp0CtSpWpeeHer25t2vi/8fT768+fPo06tfz769+/fw40dOIEE+QQYDwurfz79//7T+BSjggAQWCAABDCw3FFEJQtWYguo9KNuC50mIGoXmWdgahuVpOByH5HkI3W8ZzgRheiJWR2KHu52IXnhMfWccjEU1oFZ95dkI4Fi8QYVAWAA2SJ4EYwUAond2oWjiX0m+2CKTxbHY44YRPnlhlVN+iKWLFVpJpZJZjnikeDQWJSNwZRJ1Zm5pAsUAAhCoBwECQtpn55145qnnnnz26eefgAYq6KCEFmrooYgmquiijDbq6KPjibXABHcykMClCGCqaQKZcrppp6BqGqqnpGaKQJEsCXDAqKOW+umrrsb/ymmdrSngwK245qrrrrz26mtPYomFQAO/FmtssQp4OVyUNQVQQJxVKTsis+O1uZNUW8pGLZnSVtdkl2GquO111uqELZjLjctdt9p9W2K42jGHLpTZoqYueOwiee+M+Tb1Vr2JuStlugAvW6VjBiuJMHQChxibvQfr1XC1D7e2L5pLtiVAAPCSWfFVE+Q31sLXGaDWjVcRGaxYBpx3E0sDUPolej8GwOW7N7NIcrxjkvuxmBFPGOHPKva87s7eyesk0v4q3SXTMU7sMdRJ/auwxBezSXS8WdOWYrtdv2bbtEETJrXPVJvpdIlpq3k2d1snHbZqX+s7dNtAWb001ncTHHw1vfNCKvjghBdu+OGIJ6744ow37vjjkEfeUUAAOw==");
		maps.put("3142",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozu7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcqCCAAgACKhQsuVECQEUuJSoAECEgwwABKAws2dBCQAS+HxY86bDBjp5DhUJVOhShkUvOtA54WnGplYXnpTw0UGAAFyzOgQqUyzCqC0hALBp9mfQtgUNrNUJoMFQpAHoLliqVucDuAXRAs4QIS+ElmQHE9yqOCFMAH87wizbWHDjho8dWMR62fLliRTyMhj7tjPbzx1DAxjt1mllsKhLVhCgk3Ls27hz697Nu7fv38CDCx9OvLjxkYJzClB6nCHnyqcT5tzZvPVnz0epFn+uGHvFqbB5c/8f7J3jA51hL48HXACAZp9qAxh18H7perjlh0ZYexixgwcJBFDAAw/0B1h+bRUGGVMA2EZedLtlptF9bSHI2wS02RURhWZZOFxoAbCGEIdieVhdBqrtlQGJJ7bo4oswxijjjDTWaOONOOao44489ujjjyUxIIABLPGIoYjdsbWASszZyKJVnk3XZIxPPpUfA19NeWKVS5koZYtcDmUiQXhpGVyYPo15EF5VCYdmT2oqBF564pUGnVEbzRmhnUni6dF54aH25kxxVnQehIAN6lKhGPXlp1iKllQTenzN9RUGT52HgGIRENheAgSaOZIFEQSYl3s+JUBXXgfcOdh+ABj/WNJk1yEKmIKygkSraY+qt1ZkHu3qaoR51XcVnw/2qpuEF0Uqqa2+TVAsRc6SxGhsqmlImmt9uqgakiMieyC01YEI7kDVIkeut6IRlK5I1xaX4rtAilRBApjWq+++/Pbr778AByzwwAQXbPDBCCes8MIMN+zwwxBHLPHEFFds8cVarbUSj4mZJsGSARSJY8fQhQWyyDWS3F2gGZzsZEy10inQlzOqTB7LBNEMo7BJylyQzmA2eB3OOGXpIs8P+owQ0MbZfCDRCpXZHNLjKr2Q1NsJ3RnUDSElgNXiweyxReC1CZzT+HENkZ6/UY0fAGBHxHadDj4dt0Rz5+Z2hXB7/5R3bGjzffd3aw1uVuAd9h3SoYZbtXeHam/E+GeIl6g4SQ/k1XhPj5cY+Z95Keu41iXDt5af+dontmIX5EVApbFOZcDodZtFAV0ADJB6TynNZbZPBAQggGJ9narTBT31ZekAQ11Am8bIr7y5varGOhNQc9E1fUs1bT8SrLmSRC9IjL36q0vjf9T9ZbACyyC3N3t//fnvx3wb9u5LJm7a8g/1WP4T2p/g9oQqjlQuK+sLWwGPVbsKfY5y08JI5xB4uWhFsCIHhNID75cXbUlkglCqYHCkVZeXkE56xyGhBzGzup6Vay0rVEgGrbRB32SLhQ1MXP9y862FzLBLNQxOD05H1MKkzWiIbsmh53Zow3a5q4hVuxESfyimIDZnNgDYCwitJEIbYfEAJxyMkJhYnArkZXYYS6Ma18jGNrrxjXCMoxznSMc62vGOeNxNQAAAOw==");
		maps.put("3214",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAzCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJkmCDAAEklDRoQUEAAAFWFpTwsoFMjwxgqrwpkmYABjyDClwAc4JQiBMEAFhwtClDogKMUhBgQSaFl0ydar2oAIAAChunAlCwtSzJBAAGgFVIYQCABGbjar0Kc63cuzctKD3QEEHaqngDV7QwIAACDBsxHABAALDgxxYIADCAOMNhmYoZO34s84JkAxc4Z8BggHFo0Rc9Tz6NuiFpAAVYtx54oQDsyrMT2y6AO7fv38CDCx9OvLjx48iTK1/u8IFkAACYi2ygUzpBmgCAKs8pYOdv7FmtL/+EatQs3fDiLVZQ+hK63Y90yaYP2vXrRLFw5z9GK6DCwPUBJNCbfqLpBQACBfk1gH8E3kUYAHxNhIGCDDYY0oTQZSibR5kNsJmFEEUG4YZOZdYYiLR9RmJrGBQQAAErCoeBZLzNVxtsMaKo44489ujjj0AGKeSQRBZp5JFIJgkZgkoSleGTUEYp5ZRURtlelVhmqWUA6HFGXUpNPcATdtoFlxOYzJG5HAMoeSecT2Wmd2Z5rUmgVJcgEhUAnXJRAB2eP5I310vyKZmBky/lN1J8hhp0AQQLuFUXfEoV2qhDXQXw3n1KKXppRfxtypYAAX76EVpqHQSgp6aKpGAEbrH/2upIBh7mV3+ziqRXABEepOCHuVJkYK+uHRCAh8GG6BaxFf2a7H8OIHDlAANq1CGwl4qo1FIlmYgtkZEFQJlW3g6pWo14vTZAjumdW+1jr8Foo4rFXVCavMjNuJp0Ld4WXL/oEtgvvqJVgAC7BF6AQIXPNuzwwxBHLPHEFFds8cUYZ6zxxhx37PHHIIcs8sgkW4fBSdEpSQECCrScgMswK/CyzDHPbHPMNcd8wLbQBUDzzzcDnbPQRAc9MwKi3nVmAxA07fTTUEctddMPRDA1BBE84NKTVV+N9ddehy01ynE+9qWbN1FgW1DY2eTbmWhbp6ZwcIs3d3F1K3c3cnkX/7f3mtW9eSeBevLZWlLcgshd3IEhDmiDiz+GuKU9CiqX40VarhXmSNZH57chnXdpfQ8o5cBKoptKF0wCkJT6pxhmiFVIkz9cgYv2cTRVqRPXl7RE+GGcQAC5c/oWx6FGFPzHvje0vMjJI/R8yfwxnMH0JQ9U/fWdvpu9QGhBJ+D3CVXg1vnWk5/BegcORKH66yvFZIJppQ9yrQvdav/Gwz6kv8e7YpZDnJWx/lVEMS/an8MeJECKdEiBs5IAmyDkPYsQMFd+6RlMTgcSBJ4oVxVACUwIQJJrmYowvLqAsSKwEhMqiYEVLOFikGUkER0AYS2cIQ4tZMMd3qRcPrJhDGXJtZgPokhEoGGRi4yon3CNyzctehHokOMZcfkwMPG6om9ulETjRJFgxuGiFpWIo3rRaIjEyWJw3CWw3aAxLvoKWIMA9kat+CU2PbIXhODHxz768Y+ADKQgB0nIQhrykIhM5MMCAgA7");
		maps.put("3241",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJciCGAgAMYCjJMmIDAAEAKGi5MYMBAARW0hzJAOYEgxFk7gyZoUCAnEMl9gzws2HQmUlZZjiA80LUBQAENKX4NKpXgQgADLC6UUFWCh4jBID6tS3BsAMsMDQbQG7JoAnc6k2YYQDMCFEpAIAJk8HetnDtKkyQtQLLroelUh3L0awAxx0hR+Y4tSpYzCIRYMYqAG1FvJsr2hSrMzVB0lsXonatsOjR1rQjLo0tcDbtogAKZMg98qUAocSTK1/OvLnz59CjS59Ovbr169iTnww+PDvC3b2RS/9fjTQ6+ISas5Mnu3epaYjpvRvsTHmk5fcX48tnOPWo4oh0gfaRb/tZlBhB9w1FYIEUHTgQYwL8xxIEABzAoEMZOCgRhAKmJd6FGoQVU0y8dTRATDBdhtGC19HHnl5YBdChUwDkNZ1t5UF3HkIs/oZSAbgxeF6PIBZp5JFIJqnkkkw26eSTUEYp5ZRUVmkliBcgEGSTDDAgoWvkbQmicRIQSdt2QArpE0FqsfVcmNb1pBWPa1kHZ3M7olenejcNIKZe7sG3536dEfBiVHLiJ6ibDBZ6qH1nrTjokY6ClOBGbT452aMSBQiSmUlmKNaXCnk6EqhNhhUhQqY+VuOVb4n/VQECAahIU1AojogArHBVACGpJGEQQEwQWKlhQQnUOqNI+jEpalwQcUhSpk4eS9GlaU3a6AQMEEBYAJxmdNNgAiQAQbjwvVpkZ8NWeOpgw8q4orqEbuoWthGh+maf6LYFW7o2Urdams39Kxu9bxqV43QMDFtieAEvB1wB/VKXp75u3YnkUmUinJoFB3QHJQMLAAvrySinrPLKLLfs8sswxyzzzDTXbPPNOOes88489+zzz0AHLfTQ2YUlHJQvAbArcROLnORLAbzE6Jk3EWwk1D81+9tNC+/XMFMDYRzZxH9a7HBBYm9GNnZfP5x2asB1/VyeaHsMHY4VuxaoQm/n/7Yec3TzaPeNXOf9VeDoDS7wAUcZvlOMD/OteHX0lf04TIo61LdzLn4VY+YPbf6mvZeXlt/k3nVeEmmgSyT6eIzXZ2mtrU/0OnWkV0Z7ZqhfqDpGli0779ShWgtgpJ9+WK1YjiOIfEjURvn7QnSZzJHWy8t+UPAtRU/ls1+qKjz0ylfpoKrWQ6/tlSI2ltRNKMLUvJLtDzA+pIP5yausYGWVvqVZ2Z/9DCKtW60PSr1aDP9cFTEEjsoh3JtW745kPIYky1bka+CSKviQVnlIg5SCy/wWo6wBTdA7GHjAibSnEbow4H6uK99+wjKYGi4gJBaAVw1XZRHvXQgDx4HXSKdiNJhiDW9dk7EArRwwEmGVhnX5OSB2pjcUKHJFhnaiiqH2YsV8SfFGWhzh6taULuKNxwAB0F9u9uaUL3KuT5aLDBslZ8bmrEaNzkEcm9yYnL8xjIwHwR7TFBZHPAFyj3X0UXDEqJy21S2RavsRIw0Jtt7wcS8TmyR0sKYBQeplbUuCmtSIYzSnMSlpICSaKlfJyla68pWwjKUsZ0nLWtrylrjM5VACAgA7");
		maps.put("3412",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsmAFAQAWlCT4AEAAARBWypypgUIAAAxo6ty50iaABjyDCu044aaDoUiTUpRw84HSp1AXRgAAIGbUq1hR3gwAFKvXpy0DSPhKNmnYsWXT8nTgcoLatzQbuKQAt25JBgEC0LVrluqACDzxCqjAt7Dhw4gTK17MuLHjx5AjK23p0qpkkBVu5rzM2WaAzZwlT0DZNXRkpgCOmo4coelq1lRVv1bsVCAEqoBnOz6r2zFbsb0b/94bXDGDucUXHxdAPDnEsAASXHBOvbr169iza9/Ovbv37+DDJ/+PgFa8QARU06tfz769+/fqb8KPP7++e/kC3p5MKVNCefMH+QQagEN5NiCBQQmIIFI+lbYgT0Wl9qBQqMk2oU4VXsgTarVpSNNUAHTooUwgWjbiSgmsd4ADGZwI0lQBHHCTiS6OdBsAudVIEm867ujSfz2C9BuQQXo0ZJEiDYdkSHLptSRIxzn5pEdRNjelRstZKV4FGAQVJWFqoWTfmOnJZ99WZ5I5JpouhRcBjVfGKeecdNZp55145qnnnnz26eefgAYq6KCEFgpZSwh0CaABCiiQQKOPOgrppJJWGumljVKKqaMGqDcAApZqGuqom5YqaqQKlHVSjP616uqrsMb/6t8EE8jaagJmFvCArRLUyuuvtvpaq1tpZYbTTIgqiqCChhJk7IGGGtgsQcxOW5Nm1grUYLbX/sTtttmCa22EFjZLLrcZZpuutetOyyG6VInYrATxwhsAnIXSey+3JfJLFb6EwjgAAxO0aKgF7DkoaIrp5UgoBt4+cJPDzd4WAMWGWoxxoTduTGjH3FJGZKE8Wntktieb3Ba3vxGrcgAuT9tkzM02qWWhNnObc7Y7W1sltz9nG7S1C7gEJtFGAyhxAwbrVHQAFqhFwbC9Vk311VZnjfXWWkuggHoE7Mr12F2TjbV/6BmQ1qSPti2p23C/LXfcCbRtt6MJFFBmAAXUJe3333XPLTjdg+MNqUrgtVTA0dw27vjjkEcu+eSUV2755ZhfFBAAOw==");
		maps.put("3421",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKyqrKyurLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTm5Ozq7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAnCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsuTDCAgCAAhgEqGDAAlaypxZ0gEABDRz6uRoE+fOn0Al9gxKtKjClzGNKl06dKlToA8ASJW64KlVnU2vapWJdKtXk1m/iv0YdqxZjWXPkpyKIMLPrmrjyp1Lt67du3jz6t3Lt29RBwOksvRLGOvNwnnTIpareLHaxo7NQo4stgEAA5TpTs68dTPnq54/Pw0tmunh0pJPo144dUCD1bBjy55Nu7bt27hz697NuzdilLwTTB1OvLjx48iLq1SZvLnz5wAIMPA9YfpO0tRDYs9OVjV3mtu/8//0Lr5l+PIZz6O/qH59xfbuJ8KPH3E+fYcRmL++/5GB8QP8gWRfgAsNSGBCBh54UIIKFsRggwM9COEEEkJYYYMXKpghXtYFtWFGwgWw3IgrkShiiSieqKKIJpp4XIspwrhijDTOGMAAHeLW1oQ89ujjj0AGKeSQRBZp5JFIJqnkkkw26eSTUPIowQIq8cZAAlgikOWWCWjZJZdehsnll2SKeYAAxJUJJpZrqummmG52meNTEDhg55145qnnnnz22UBKUwXQQJ+EFmronQqQh9sDBQDwFgBJRUmQTZFKKhBcll4KaaYRwsSpppVaimmmlH5Koaeflpoqqpyq2iqrpG6xumqokroaK61R2ioqrKLK+iquUOoqaUoCfCrcSgNIYGkBy5Xo1q4+/WqqsLnyWquvpFqbK7a9AvsktcFqGyy3tYr7LbnVeuskuN+auy664arbJLvrujsvvO3KC1sABTzgIb5E1XnowATj2WwACBSssKGJRvsUA1pG/KXEFE9sccUYX9zlwQEYEPHHXYqJAMgjh2xyySiTHPKct4m4gLKmxizzzDTXbPPNOOes8848mxQQADs=");
		maps.put("4123",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsmADAAEmlDQYAKWCCysLTggQoEFMjwxQqrwpcmYABjyDClyAkoJQiBQEAFhwtClDogKMrrwgoIKGCi2ZOt16UQFKqxorKFXAtSzJBAAGgE1YYQCABGbjbsX6Va7doFQBHGiIIC3Mu4ArXhgQAEEGjggCELAQuLHAwXoPr8wAV0OGAwAI/HUcEwMBAAYwcM5gILNozhc9gz6NuiFpAAVYtx6YoQBsybM3YigdO7fv38CDCx9OvLjx48iTK18esWXaCMxFnkwZneBMAECV5xSw8/d1rdUXQv+VWpYu+PAZvUYVWQGAe/dq0d9Uvxai2LfyHaMVwFigBQEBJIBbfqjlhUBBfQ3QH4FyQbbXRBkkuCCDIjk4oEcYvIeSYRRKdMFnB8i21WWZbdahaqEFV5tiIhKXwWcFXMgcBrb11uGNOOao44489ujjj0AGKeSQRBZpJHMJmHikBgs4p+GTUEYp5ZRPtuQklVhmmWUA5zk2XXcrQdDUddkFlxN1zJG5HAM0gRmcT2WidyZ5qE2gVJcdEhUAnXG1t9SQ483VEllLDgXAemdpSGihCNH3EV1pLQBBi4we5FUA9U10X2WVWrRfpgqJFWCnH6EVn0H/jUpqSQlG4Banq5b/ZGCEaU0Yq0dUBfDgQQkqeStFee3q2gEBDODrrwpZ4JawFfWK7EEWfpSBW+4V5oCtS0ZLUk4oKXaskB/qRelNJGoWpGoxAvbaAOPKh66Mjb1GQLvIoUhva7uZltyLq0VX220qEhBAugz+SzBnFnDIIwYIYPvswxBHLPHEFFds8cUYZ6zxxhx37PHHIIcs8sgkl2yycig1AO+QFSCggAIJvBwzzDLXTPPNM+f8ss0604yWhgIg0HPPOPNsdNFIJ4AAqHad2UAEUEct9dRUVx2BBBJYHXXW79GUAARZV5112FqXrXUDLcXZ2JdBgcZnTGr6dqab1cVtpk7h2U3c3Mvp/30c38b5rR3ewtn5Z356vs1ZUodTuB3dgDGOp+OHQm4W44vyGKhckgN6qOJBdW6keqCHtJl5lZK+0gNpQTBWrAoEgKhIAHbLdOqyl45Rkyi9x+yqjnJElwEOPxv8RfdlbnECst/e0KYefxoR9CEfH6pSsIosPULUn6zBfrZ27/1A4F+F/crjo9WSgOMnpGxatbaPqlIHCiSh/Br8B0D9CMZ/soEL6Qt/SBashwiweBjL1e/44heOFbAil1EMAh82GF1phETGopi2NkKrn1iuUw4SCeus5B7+VSpcB0DfRmxjpQkOCYUq5IgEIoOZDGYLRDEUCQa/1SMU3msyNfwhhW18aJdy8ZBBMIwXZsyFo3ClCDUrYiKBPhQAA+SwiAXwlrsE9kQVlWZe0aFRv4wTRSHORoxdRI7BzNiYd4VHXmyUixvzs8bc8OtgBavRFbfSFxvlSIwmxJ8gB0nIQhrykIhMpCIXychGOvKRvwoIADs=");
		maps.put("4132",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9PT29Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcqCCAAEANCjJMmIGAgEMYGg5UQGACAYnoGRAkyQGmDJ7ZrSJsyGFlAuESrxAAEBQpR6JWqQgAEBSpUwBHMgAleXJohwrVFXA8cIAAAi4doVqEwLLCikTMLxQ9cDauwQbANiLkgJUCQACDFCLt/DBr28FBJC70MLZtIbxSu0oNgDZjY4DQI7sNQDYiVQBXK5gd2QFxma1Eua8EXHDCVWvsi6Y1cDq2RBdE9QJgCduij+dzvyd8CTKlcRFZigQc3jy59CjS59Ovbr169iza9/Ovbv32ZN5+//+fvAlgAK3iU9GKB470+ba1z/kLbtw1gPOv8ufOpZk6q3kKdSWSGIBwNhSdQUo0YA9FXigBnShpWBGCNwkVAVnJUCYYxJOeJFuHzlIEYeledgQiBTBldJeA4g0wV4wlmiiBigahRRn96XHXY273XhdcOjF55lA7c2oAZA6Gqnkkkw26eSTUEYp5ZRUVmnllVhmqeWW11nAwHhTZnDABckRxRtyTJpXQH6/gahTAGCS9xN80/GowZtx/tjUU9bZ2WNv1NXG5nV+HoTnb4KSV6hCodUH1X2DereoUbHRlKOSk0IU2mUe/fckgx5RZRlGEQIYJaghifhQqVeiShKGoun/yOqVCQQGY0qflVQgApUhwKVAEAQWAJlCWaBYArD6+iuNFrYE64MEcahslvuFWBW0jT2WpJPVaqSqRSRO2W1EFzygQFV7FQCSBSuex8AE2+rXrEUqruiWSBUKa+qM4zK0KV61xRvfvK/1h9ul8uZqqI/SIbkjwX/maZ3DhBJcpILB8RkdURKkJLGJGBRwnsCFefmxk2JasOzKLLfs8sswxyzzzDTXbPPNOOes88489+zzz0AHLfTQRBdt9NEX1aoSlebJCJ5KKaHZZMhOEaseAG6dyeScQUIH6sUTUrwx1gWB7V3GkZZJdk4en71n2s+5GrF2aGcnd9ltV1f3wPcu/2Q2cXtvdzdCjSYHqbx9O/Qva4crunZEhdvXFH4TDk6pVWuZRWflj1O0uKVn7euh5Zoa7F/oJHdHOuSmf/Rf6pJ2ntHnGyHcZL/0XqtRapt9CnFYKXE6EV0BOP3pkASiJLxDxPcuJe4bFbh8QrNamalGxhqoUPbTtvq7aboXxD2X13eUPWPja3nBiisaoFT2KXWvZQZn7TV9YrbKv2X4DV7LVbLLWp1HHHSb9GlJgNHj30GkdUDZgeRb2UIL7IyEwNxpLyLSmuDoHBg9lGALIppznu8S18ELkkpbp+JgRSrAAPt1qikDeADcOEdCBN0qMCoDSQtttRf9KaiCC4kAX6wwJxKUCEuDdlPhQ+AiGgoMIAAzzIgDtGKBySGxT0pkCBPv15LXmQiIBGGio7qiOdvQkHVExNETzei4GiaEdrMp4xWv5sacVCo6clRdFu90x0ABZY6csdyhsnMBkbGxYm4cJHe4Bsi7uOpvizRkFJ/WN0h+h2pdG1vW8qYkTDaSJjZpQFWktjWRZfI3SjvZkpbTIaS58pWwjKUsZ0nLWtrylrjMpS53ycteNiQgADs=");
		maps.put("4213",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKyqrKyurLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTi5OTm5PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAlCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJciACAA1KJoRwIACAACoRNgiAIKZEBAFS2tz5sAGAAzwf4tQZtKjMn0YZDk3KlKBPoE0RnmQQtelMqFULTs2a9CnXgQoAiBXr4KvNB2PFKjArYSvbnV7fLn1r02dNuQCo0o058y5bt3tL9qULOPBIu4RzGhZMc7Hjx5AjS55MubLly5gza97cdYBYmJw7zg0tMe5fxaRLI5WLOjVE02ZHu3YI+2vh2Qxrc72NWyEDAAXoGsjbm/bqv8SL5z4eO7lyhYPx6n2eMDry6dQPIpaevToAv93Di/8fT768+fPo06tfz769+/fw6bKMv8Bz2vv48+vfr9+lS/4ABhigSwMsQBhKVRlolG5ZnURUeQxW5SB6EUY14XkVNnWheRkytSGEzNmGIIYh7jYihyU26ByIWDWHHXkdJsWbeDHydMBLY7n0YnYQ/LdjVL+9BAF5C+RngIsUpijhijAqaSGT49VY1IzhbXddkuCJ+GOV3x24ZXdWIkliliZ+mZ11YnLYGHcYrsnWAgc8gN4DBygY35145qnnnnz26eefgAYq6KCEFmrooYgmquiijDbq6KOQjheBAi7duQACmB6Q6aYIaNopp56GyimooBogwH2fpiqqqqSy6uqqmmr/aidyCTRg66245qrrrrzyykBLOTLQ67DEFntrAm6madQDBADQVZdsokimimZSh6aWWHpJYbLYjqltm9MuWe1zYXarZrhPjqtcuWVui66GUNII7ZXgfiutvRBy2663rD0Io74q+hvlvLG1lu+7Hp4IIsIyGvwvw0Z92CTEUzo8XksCmNXjZ2IRUN5JLw0QwVcReOYSxa4R4N9LAQz5lQABtPivzCYKTCPAS9rMJco8yfbwgTqDSbBtFt/M804SD3y0TUnLS3PAST4tYdFcSv1k0Gc6qSHVQlsNL9bWau0h11l7nTDY5IrdMNrrqh2xwk2avXbUQNPdr93IsV2clEH5JDyw3G/r3RvfPcOtdKSIJ6744ow37vjjkEcu+eSUV2755R8FBAA7");
		maps.put("4231",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJcqACABFKqpxoQUAABAAUrJR4MuXMmwQpBACw4KCEmDgf1gw6UicABg9/yiTKcChTjBJ2Nrio9OlCBQEkWE0KIICDj1W3InQqduBPAA9W/kxQ1iAEAF0hEGXQte5UnA3qwi3QliDZkW8D2OyrYS1hvwC0dnTQdfBhg1GXPsaqmCLjrI8hGs5M2SHdABQyUwXAljPmgp9Di+64eTJcARVWl4wsu7bt27hz697Nu7fv38CDCx9OXOXf4gorCOBpFihnlMgLGkWqMKxrx78nSJ1o/fBx21EBfP/V2J3w975n04ps7R06UwfjI8BVf5MxX8IRHjwoECCBfuwiveWeWLQ9ByBHD8B1IHqkidZZRpdVlht75p0GUV4WBkdhXw8ilFp0BW3Y1oMfgriQiCamqOKKLLbo4oswxijjjDTWaOONOOao444UMSCAhDRaYIAAPRlIo3LMlcfhgC0aVaRZAUjW3oLFTZdQgdeZaCVDWE5ZZVfUJRWlg0z2ph0Ad3E3ppG8hTceVWtmiZubrMXppWzphYSiWOcRON9sDbJZVp4z7bkVTPThlMFOOwEgV1BnBdoXTIy+hBNjXfUHwQU4UaBfAgEU8N+dKglIJVGGWtUnggqKlupTq17/lGCGrkraHpAXRajbq0ydhKtEuvbGK1G+WtbVr7sNG1SxD2E4AXLK4sSsQiWCGO1N06LWlWorXjtTttW66O1Kvi7QVWw0jrsSAujy6O678MYr77z01mvvvfjmq+++/Pbr778AByzwwAQXbPDBCCes8ML3xtpiS3BJaV5iNVaw0wLqltQhjBQst1TGJGXb5HJPFmZrhciC2DFzIZ68ZMpVBhBAmC2XNhmt0W2JEMgjiRzzUVy6PCLOwul8otBl+QwcBXCl2RDPIm28dNMSQR2S0rsZ5bRmSPNJdG4TLLd1RFaDhLVtZ75ZUdkfSX3bBHCpbRHbHp2dGZ0b0d2R267u/yR3RnpzZHdbP3kFkpIjUrwaoYc7dzPMVjGup50TQ06UfGiphHjSXzOFeaIkdVn5UxkQ9DlOm3ttuUgGoIn5ozgRIDMGmQ0AAAFMwQ0XAAcQlYHtdbVbFgaNAiAA7TgBD9dOsKtE/PJ1nXpTpLt3/pGACQjPVOAbDe6RqQxKzKH1HYFPWOpbeZ+R+YeJPv7qFrH/GPqqKv7RrNITSPn798MF/1b0g1WZMOIAmf3PT+JLXP4eUkD7zclxpLJIsJK1PwXm6li/CWCvBshADALHfRYEVleeJRwNEouDCrkMCYcDwqSh8CDOio4Jl/VCgmCIW9CqoAulxwCZ4VCGEJwYlU96CBoWtZBPHAyXimYorTIpsVs6ROJgnmjEIC4pJeYq4oyOmD7eyUx7MWLiTVoXADDKSAJEYpga18jGNrrxjXCMoxznSMc62vGOeMyjbgICADs=");
		maps.put("4312",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKyqrKyurLSytLS2tLy6vMTCxMTGxNTS1NTW1Nza3OTi5OTm5Ozq7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wAnCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJciCCAA1KJgTA8kAElTA7IgDAIKbNmyJn1sTJsydGnT6DCoU4M+XQo0gNnjSatKlQBSwBBADgwKlVn0WvauV5cufWryqBgh07UizZsx7Nol2bcanPCAeisp1Lt67du3jz6t3Lt6/fv4ApRh3ANLBFtYbvIk5cdzHjuY4fr3Ur2a4BmpXtZs1MdzNntpE/fw0tWivp0lZPo16NkMEAqQBYy55Nu7bt27hz697Nu7fv35Nf+l7wOqrx48iTK1/OvLnz58qnItiqWuQC4NWB58SsvWf27h+/g/+XyX28TfHmNaJP/xNAYfYkPcOP735+SQYsAwi3/zFu/qjX8QcSZQKGRGCB4aGEoIHlLUieVw5ytF6EDE1IoUIWXohQhhoq1WCHh33YE4S4ERfAiVJNheKKKraY4osrwsYijMi5OOONNuYIo44qAjDdb3CBKOSQRBZp5JFIJqnkkkw26eSTUEYp5ZRUVmklWFIpIMFwCHR5gJdgIvClmGGOaWaYZaZ5QI8ACGDAmWTGCeecacpZ5pcBXjVTAg306eefgAYq6KCENtBjAAcwUOiijDL6AHX19RQAAVX1Jp+VB2Ia6ZUTZFrlpZ8qyGmnm17pKZWgoioqp6lOeaqrpWKquqqpsX4qIqq3wkqirbvi2quuo04gAKKcSvCadFZGwOakrOYqJYdEQjuktEJSC6K1HWKr4avP1qrqe7aC+22wrT47q6biunpuuOSui2u65sIbZbnzugurvCo9QEBsu+3Z6L8A+3mAAP8FbPC/EHy1wJcMk9nwww5HDPEBDFcsJsMFEDxVSxRf7HHHEoc8scgS57mbBApMFezKLLfs8sswxyzzzDTXbLNKAQEAOw==");
		maps.put("4321",
				"R0lGODlhoACgAPQAAJSSlJSWlJyanJyenKSipKSmpKyqrKyurLSytLS2tLy6vLy+vMTCxMTGxMzKzMzOzNTS1NTW1Nza3Nze3OTi5OTm5Ozq7Ozu7PTy9Pz6/Pz+/JSSlJSSlJSSlJSSlJSSlCwAAAAAoACgAEQI/wA1CBxIsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsmRECAICAHhgcuACAAIqtJxJkyMDAAEo1NzJc6IDnBN6Ch2q8GcACUSTKtXwAAAApEuj8oTgNILUqzMbqHQaAKtXkhKcsvxKFmRYAA7Kqu04QQDatXAzUlDZIK7dinMDMLjLN6KFlHt3ShiAc2zfw4gTK17MuLHjx5AjS55MuSCErYYra+54M+dmuw1w6vwM1yhU0modqDyNmixVAFZblzUKQXbZs4Zr25aKe/fXCU7T+sY6F0Dd4VeLB0YetYLbBcyjI8SQQGWAzNKza9/Ovbv37+DDi/8fT768ebCxvbt1yr69+/fw48ufT7++/fgJzktIT7KzzPMtdTYagCUJSKBJoXl2IElGBbXgSKY9CCFOrEnoUVNHWRhSU7BpCNJr/Hm4EYgiZpSBAwZs5VR+JXIUgVMHqKRbixf1RqNGNt6IUY46WgTcWz1eNIFKwgWJl1PHGUlRcUkqKZFyTi4ZgF5RTlSBStBVGREFbinQEwb/gbfefWTiRKZ19qlYZn0qCWAeBCFqKeecdNZp55145qnnnnz26eefgAYq6KCEFuonBggA6d0CCiiQQKOPOgrppJJWGumllGJqaaMIuGVdAZtuqumomZa6qQEZlDWBBKuuKsGrsMb/KuussrZKa6wRTPBAAWYGkICrs9oK7K3E0jpBjAGEKR6iK83kH6EGDhqtoNMGysCUA1qLLbSiERqaAA4KmmC2gDZIqLmD/gRAuIFGmC6F58I7KIZx+knioPcKmm+g+wLa75//7pnBBAykBMCMfobGFU7KAtwri4C+GEBtoV0QsViE8gioxn9y7GdY12WsEnYdjyyyooFKQCShPxYZaFvGsUwXoRQgSbPNg0KZs0rLBaqzoD/7/BzNQw/qHABZCmoBljxloJXL3hkAAAKwCsvq1VZnjfXWWj9AQHsKcC221mSPPTa5WEmawNqOPsr2227H3fbcb9MtdwIFqFkA3Hb3IM3333cH3qiX41XAK8mGJq744ow37vjjkEcu+eSU9xQQADs=");

		for (String temp : maps.keySet()) {
			String im = maps.get(temp);
			byte[] pid = getPic(im);
			System.out.println(temp);
			if (compareImage(pid, image)) {
				return temp;
			}
		}

		return "";
	}
}

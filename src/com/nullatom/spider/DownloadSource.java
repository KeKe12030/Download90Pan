package com.nullatom.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Queue;

import org.eclipse.jetty.util.BlockingArrayQueue;

public class DownloadSource{
	public BlockingArrayQueue<String[]> sourceDownloadUrl = null;
	private String path = "";
	

	/**
	 * path：必须是 "C:/Desktop/Downloads" 的格式
	 * */
	public DownloadSource(String path) {
		sourceDownloadUrl = new BlockingArrayQueue<>();
		this.path = path;
		File pathFile = new File(path);
		if(!pathFile.exists()) {
			pathFile.mkdirs();
		}
		System.out.println("配置成功，所有下载的资源均放在："+pathFile.getPath());
	}


	/**
	 * 提交任务
	 * */
	public void addSource(String name,String url) {
		this.sourceDownloadUrl.add(new String[]{name,url});
	}

	/**
	 * 
	 * 开始下载资源
	 * 
	 * */
	public boolean downloadSource(String fileName,String urlStr) {
		File f = new File(path+"/"+fileName);
		try {
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			if(!f.exists()) {
				f.createNewFile();//如果没有创建文件
			}
			FileOutputStream fos = new FileOutputStream(f);
			int len = 0;
			byte[] temp = new byte[1024];
			while((len = is.read(temp))!=-1) {
				fos.write(temp,0,len);
				fos.flush();
			}
			fos.close();
			is.close();
			return true;
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			System.out.println("==检测到错误== ："+e.getMessage());
			e.printStackTrace();
			System.exit(0);
			return false;
		}
	}

}

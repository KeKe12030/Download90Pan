package com.nullatom.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Queue;

import org.eclipse.jetty.util.BlockingArrayQueue;

public class DownloadSourceThread implements Runnable{
	private Queue<String[]> sourceDownloadUrl = null;
	private String path = "";
	

	/**
	 * path�������� "C:/Desktop/Downloads" �ĸ�ʽ
	 * */
	public DownloadSourceThread(String path) {
		sourceDownloadUrl = new BlockingArrayQueue<>();
		this.path = path;
		File pathFile = new File(path);
		if(!pathFile.exists()) {
			pathFile.mkdirs();
		}
		System.out.println("���óɹ����������ص���Դ�����ڣ�"+pathFile.getPath());
	}


	/**
	 * �ύ����
	 * */
	public void addSource(String name,String url) {
		this.sourceDownloadUrl.add(new String[]{name,url});
	}

	/**
	 * 
	 * ��ʼ������Դ
	 * 
	 * */
	public boolean downloadSource(String fileName,String urlStr) {
		File f = new File(path+"/"+fileName);
		try {
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			if(!f.exists()) {
				f.createNewFile();//���û�д����ļ�
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
			// TODO �Զ����ɵ� catch ��
			System.out.println("==��⵽����== ��"+e.getMessage());
			e.printStackTrace();
			System.exit(0);
			return false;
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(100);//��ֹ�߳̿���
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
			if(sourceDownloadUrl.size() > 0) {//�ж��Ƿ��������ύ
				for(int i=0;i<sourceDownloadUrl.size();i++) {
					String[] infos = sourceDownloadUrl.poll();
					System.out.println(downloadSource(infos[0], infos[1]) ? infos[0]+"���سɹ� ��" : infos[0]+"����ʧ�� ��");

				}
			}
		}

	}
}

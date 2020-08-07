package com.nullatom.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FindSourcesThread implements Runnable{
	private WebDriver driver = null;
//	private WebDriverWait wait = null;
	private static String url = "";
	private static int pageNum = 1;
	private DownloadSourceThread downloadSourceThread = null;
	static {
		//�ڶ���������Ҫ�������ã����ó������ص�  chromedriver.exe  ��ŵ�λ��
		System.setProperty("webdriver.chrome.driver", "C:/Users/Administrator/Desktop/chromedriver.exe");
	}
	public FindSourcesThread(String url) {
		this(url,1,"C:/Users/Administrator/Desktop");//Ĭ�����ص�����Ա����
	}
	public FindSourcesThread(String url , int pageNum,String path){
		this.url = url;
		this.pageNum = pageNum;
		
		//���������߳�
		
		downloadSourceThread = new DownloadSourceThread(path);
		Thread downloadThread = new Thread(downloadSourceThread);
		downloadThread.start();
		
		
		//�̶�ʱ��ȴ���driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		//�趨���ȴ�ʱ�䣬һ����ǩ���ڼ��ɷ��أ�wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".abc")))
		driver = new ChromeDriver(); // �½�һ��WebDriver �Ķ��󣬵���new ���ǹȸ������
//		wait = new WebDriverWait(driver, 10, 1);
//		try {
//			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='go']")));
//		}catch(Exception e) {
//			
//		}
	}
	/**
	 * URL�����ǣ�"https://www.90pan.com/o10001&pg=" ����ʽ
	 * pagesNum��ҳ��
	 * 
	 * */
	public void findDownloadPageUrls(){
		for(int i =1;i<=pageNum;i++) {
			Connection con = Jsoup.connect(url+i);
			try {
				Elements elements = con.get().getElementsByClass("pull-left");
				for(Element e : elements) {
					Elements elements2 = e.getElementsByTag("a");
					for(Element e2 : elements2) {
						sourceDownload("https://www.90pan.com/"+e2.attr("href"));
					}
				}
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				System.out.println("��ȡ�����ˣ���������");
				i--;
				continue;
			}
		}
		System.out.println("�������ؾ�����ɣ�");
	}
	
	public void sourceDownload(String url){
		//��ȡ���˵�ǰҳ��������APK
			driver.get(url); // ��ָ������վ
			String apkName = driver.findElement(By.className("span9")).findElement(By.tagName("h1")).getText();
			//		        driver.quit();// �˳������
			WebElement goId = driver.findElement(By.id("go"));
			List<WebElement> atags = goId.findElements(By.tagName("a"));
			for(WebElement a : atags) {
				if(a.findElement(By.className("txt")).getText().contains("��ͨ����")) {
					
					downloadSourceThread.addSource(apkName,a.getAttribute("href"));
					
					System.out.println("��ȡ�������ύ������Ϊ��"+apkName+"����Դ����������\n�������ӣ�"+a.getAttribute("href").substring(0,20)+"......");
				}
			}
	}
	
	
	@Override
	public void run() {
		
		//��ҳ�濪ʼѰ���������ӣ����һ�ȡ��������ֱ�����ύ����������
		
		findDownloadPageUrls();
		
		
	}
	
	
	

}

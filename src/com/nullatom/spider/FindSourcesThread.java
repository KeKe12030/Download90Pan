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
		//第二个参数需要自行配置，配置成你下载的  chromedriver.exe  存放的位置
		System.setProperty("webdriver.chrome.driver", "C:/Users/Administrator/Desktop/chromedriver.exe");
	}
	public FindSourcesThread(String url) {
		this(url,1,"C:/Users/Administrator/Desktop");//默认下载到管理员桌面
	}
	public FindSourcesThread(String url , int pageNum,String path){
		this.url = url;
		this.pageNum = pageNum;
		
		//开启下载线程
		
		downloadSourceThread = new DownloadSourceThread(path);
		Thread downloadThread = new Thread(downloadSourceThread);
		downloadThread.start();
		
		
		//固定时间等待：driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		//设定最大等待时间，一旦标签存在即可返回：wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".abc")))
		driver = new ChromeDriver(); // 新建一个WebDriver 的对象，但是new 的是谷歌的驱动
//		wait = new WebDriverWait(driver, 10, 1);
//		try {
//			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='go']")));
//		}catch(Exception e) {
//			
//		}
	}
	/**
	 * URL必须是："https://www.90pan.com/o10001&pg=" 的形式
	 * pagesNum是页数
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
				// TODO 自动生成的 catch 块
				System.out.println("爬取出错了，正在重试");
				i--;
				continue;
			}
		}
		System.out.println("所有下载均已完成！");
	}
	
	public void sourceDownload(String url){
		//获取到了当前页数的所有APK
			driver.get(url); // 打开指定的网站
			String apkName = driver.findElement(By.className("span9")).findElement(By.tagName("h1")).getText();
			//		        driver.quit();// 退出浏览器
			WebElement goId = driver.findElement(By.id("go"));
			List<WebElement> atags = goId.findElements(By.tagName("a"));
			for(WebElement a : atags) {
				if(a.findElement(By.className("txt")).getText().contains("普通下载")) {
					
					downloadSourceThread.addSource(apkName,a.getAttribute("href"));
					
					System.out.println("获取并且已提交到名称为："+apkName+"的资源的下载任务！\n下载连接："+a.getAttribute("href").substring(0,20)+"......");
				}
			}
	}
	
	
	@Override
	public void run() {
		
		//从页面开始寻找下载链接，并且获取下载链接直链，提交到下载任务
		
		findDownloadPageUrls();
		
		
	}
	
	
	

}

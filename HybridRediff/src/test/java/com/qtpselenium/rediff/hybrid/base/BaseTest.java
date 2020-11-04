package com.qtpselenium.rediff.hybrid.base;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestMethodFinder;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.qtpselenium.rediff.hybrid.driver.DriverScript;
import com.qtpselenium.rediff.hybrid.reports.ExtentManager;
import com.qtpselenium.rediff.hybrid.util.DataUtil;
import com.qtpselenium.rediff.hybrid.util.Xls_Reader;

public class BaseTest {
	public Properties envProp;
	public Properties prop;// env.properties
	public Xls_Reader xls;
	public String testName;
	public DriverScript ds;
	public ExtentReports rep;
	public ExtentTest test;
	
	
	
	@BeforeTest
	public void init(){
		// init testName
		System.out.println("*** "+ this.getClass().getSimpleName());
		testName=this.getClass().getSimpleName();
		String arr[] = this.getClass().getPackage().getName().split("\\.");
		String suiteName= arr[arr.length-1];
		
		// properties file
		prop = new Properties();
		envProp = new Properties();
		// init prop file
		try {
			FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//env.properties");
			prop.load(fs);// init env.properties
			String env=prop.getProperty("env");
			fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//"+env+".properties");
			envProp.load(fs);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// init the xls file
		// how do i come to know the suite ?
		System.out.println(envProp.getProperty(suiteName+"_xls"));
		xls = new Xls_Reader(envProp.getProperty(suiteName+"_xls"));
		
		// init DS
		 ds = new DriverScript();
		 ds.setEnvProp(envProp);
		 ds.setProp(prop);		 		
	}

	@BeforeMethod
	public void initTest(){
		rep = ExtentManager.getInstance(prop.getProperty("reportPath"));
		test = rep.createTest(testName);
		ds.setExtentTest(test);
	}
	
	@AfterMethod
	public void quit(){
		// quit the driver
		if(ds!=null)
		   ds.quit();
		
		if(rep!=null)
			rep.flush();
	}
	
	@DataProvider
	public Object[][] getData(Method method){
		// i can use xls file object to read data
		//System.out.println("Inside data Provider "+method.getName());
		testName=method.getName();
		return DataUtil.getTestData(testName, xls);
	}
}

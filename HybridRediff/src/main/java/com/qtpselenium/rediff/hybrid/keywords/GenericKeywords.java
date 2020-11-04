package com.qtpselenium.rediff.hybrid.keywords;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.qtpselenium.rediff.hybrid.reports.ExtentManager;

public class GenericKeywords {
	public Properties envProp;
	public Properties prop;
	public String objectKey;
	public String dataKey;
	public String proceedOnFail;
	public Hashtable<String,String> data;
	public WebDriver driver;
	public ExtentTest test;
	public SoftAssert softAssert = new SoftAssert();
	
	
	/*********************Setter functions***************************/
	public String getProceedOnFail() {
		return proceedOnFail;
	}

	public void setProceedOnFail(String proceedOnFail) {
		this.proceedOnFail = proceedOnFail;
	}


	public void setEnvProp(Properties envProp) {
		this.envProp = envProp;
	}
	
	public void setExtentTest(ExtentTest test) {
		this.test = test;
	}


	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	public void setData(Hashtable<String, String> data) {
		this.data = data;
	}
    /*****************************************/
	
	
	

	public void openBrowser(){
		String browser=data.get(dataKey);
		test.log(Status.INFO,"Opening Browser "+browser );
		if(prop.getProperty("gridRun").equals("Y")) {
			// run on grid
			
			DesiredCapabilities cap=null;
			if(browser.equals("Mozilla")) {
				cap = DesiredCapabilities.firefox();
				cap.setJavascriptEnabled(true);
				cap.setPlatform(Platform.WINDOWS);
			}else if(browser.equals("Chrome")) {
				cap = DesiredCapabilities.chrome();
				cap.setJavascriptEnabled(true);
				cap.setPlatform(Platform.WINDOWS);
			}
			
			try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}else {// run on normal browser
			if(browser.equals("Mozilla")){
				// options
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
				// invoke profile
				System.setProperty("webdriver.gecko.driver", "D:\\Common\\drivers\\geckodriver.exe");
				driver = new FirefoxDriver();
			}else if(browser.equals("Chrome")){
				// init options
				driver = new ChromeDriver();
			}else if(browser.equals("IE")){
				driver = new InternetExplorerDriver();
			}else if(browser.equals("Edge")){
				driver = new EdgeDriver();
			}
		}
		
		// max and set implicit wait
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public void navigate(){
		test.log(Status.INFO,"Navigating to website "+envProp.getProperty(objectKey));
		driver.get(envProp.getProperty(objectKey));
	}

	public void click(){
		test.log(Status.INFO,"Clicking "+prop.getProperty(objectKey));
		getObject(objectKey).click();
	}
	
	public void click(String objectKey){
		setObjectKey(objectKey);
		click();
	}
	
	public void type(){
		test.log(Status.INFO,"Typing in "+prop.getProperty(objectKey)+" . Data - "+data.get(dataKey));
		getObject(objectKey).sendKeys(data.get(dataKey));
	}
	
	public void type(String objectKey, String dataKey){
		setDataKey(dataKey);
		setObjectKey(objectKey);
		type();
	}
	
	
	
	public void select(){
		test.log(Status.INFO,"Selecting from "+prop.getProperty(objectKey)+" . Data - "+data.get(dataKey));
		if(!isElementInList())
			reportFailure("Option not found in list "+ data.get(dataKey));
		
		new Select(getObject(objectKey)).selectByVisibleText(data.get(dataKey));
	}
	

	public void clear(){
		test.log(Status.INFO,"Clearing "+prop.getProperty(objectKey));
		getObject(objectKey).clear();
	}
	
	public void waitForPageToLoad(){
		JavascriptExecutor js = (JavascriptExecutor)driver;
		int i=0;
		
		while(i!=10){
		String state = (String)js.executeScript("return document.readyState;");
		System.out.println(state);

		if(state.equals("complete"))
			break;
		else
			wait(2);

		i++;
		}
		// check for jquery status
		i=0;
		while(i!=10){
	
			Long d= (Long) js.executeScript("return jQuery.active;");
			System.out.println(d);
			if(d.longValue() == 0 )
			 	break;
			else
				 wait(2);
			 i++;
				
			}
		
		}
	
	public void acceptAlert(){
		test.log(Status.INFO, "Switching to alert");
		
		try{
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
			test.log(Status.INFO, "Alert accepted successfully");
		}catch(Exception e){
			if(objectKey.equals("Y")){
				reportFailure("Alert not found when mandatory");
			}
		}
		
	}
	
	
	public void wait(int timeSec){
		try {
			Thread.sleep(timeSec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void validateElementNotInList(){
		if(isElementInList())
			reportFailure("Could not delete the option");
	}
	public void validateTitle(){
		test.log(Status.INFO,"Validating title - "+prop.getProperty(objectKey) );
		String expectedTitle = prop.getProperty(objectKey);
		String actualTitle=driver.getTitle();
		if(!expectedTitle.equals(actualTitle)){
			// report failure
			reportFailure("Titles did not match. Got title as "+ actualTitle);
		}
	}
	
	public void validateElementPresent(){
		if(!isElementPresent(objectKey)){
			// report failure
			reportFailure("Element not found "+objectKey);
		}
	}
	
	public void waitTillSelectionToBe(String objectkey , String expected) {
		int i=0;
		String actual="";
		while(i!=10){
			WebElement e = getObject(objectkey);
			Select s = new Select(e);
		    actual = s.getFirstSelectedOption().getText();
			if(actual.equals(expected))
				return;
			else
				wait(1);			
				i++;	
		}
		// reach here
		reportFailure("Values Dont match. Got value as "+actual);
		
		
	}
	
	public void quit(){
		if(driver!=null)
			driver.quit();
	}
	/*********************Utitlity Functions************************/
	// central function to extract Objects
	public WebElement getObject(String objectKey){
		WebElement e=null;
		try{
		if(objectKey.endsWith("_xpath"))
			e = driver.findElement(By.xpath(prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_id"))
			e = driver.findElement(By.id(prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_css"))
			e = driver.findElement(By.cssSelector(prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_name"))
			e = driver.findElement(By.name(prop.getProperty(objectKey)));

		WebDriverWait wait = new WebDriverWait(driver,20);
		// visibility of Object
		wait.until(ExpectedConditions.visibilityOf(e));
		// state of the object-  clickable
		wait.until(ExpectedConditions.elementToBeClickable(e));
		
		}catch(Exception ex){
			// failure -  report that failure
			reportFailure("Object Not Found "+ objectKey);
		}
		return e;
		
	}
	// true - present
	// false - not present
	public boolean isElementPresent(String objectKey){
		List<WebElement> list=null;
		
		if(objectKey.endsWith("_xpath"))
			list = driver.findElements(By.xpath(prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_id"))
			list = driver.findElements(By.id(prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_css"))
			list = driver.findElements(By.cssSelector(prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_name"))
			list = driver.findElements(By.name(prop.getProperty(objectKey)));

		if(list.size()==0)
			return false;
		else
			return true;
	}
	
	public boolean isElementInList(){
		// validate whether value is present in dropdown
				List<WebElement> options = new Select(getObject(objectKey)).getOptions();
				for(int i=0;i<options.size();i++){
					if(options.get(i).getText().equals(data.get(dataKey)))
						return true;
				}
				
				return false;
	}
	
	/*******Reporting function********/
	public void reportFailure(String failureMsg){
		// fail the test
		test.log(Status.FAIL, failureMsg);
		// take the screenshot, embed screenshot in reports
		takeSceenShot();
		// fail in testng
		//Assert.fail(failureMsg);// stop on this line
		if(proceedOnFail.equals("Y")){// soft assertion
			softAssert.fail(failureMsg);
		}else{
			softAssert.fail(failureMsg);
			softAssert.assertAll();
		}
	}
	
	public void takeSceenShot(){
		// fileName of the screenshot
		Date d=new Date();
		String screenshotFile=d.toString().replace(":", "_").replace(" ", "_")+".png";
		// take screenshot
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			// get the dynamic folder name
			FileUtils.copyFile(srcFile, new File(ExtentManager.screenshotFolderPath+screenshotFile));
			//put screenshot file in reports
			test.log(Status.FAIL,"Screenshot-> "+ test.addScreenCaptureFromPath(ExtentManager.screenshotFolderPath+screenshotFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void assertAll(){
		softAssert.assertAll();
	}
}

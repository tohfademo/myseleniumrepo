package com.qtpselenium.rediff.hybrid.keywords;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.Status;

public class AppKeywords extends GenericKeywords{

	public void login(){
		test.log(Status.INFO, "Logging in"); 
		getObject("money_xpath").click();
		getObject("signin_xpath").click();
		String username="";
		String password="";
		
		if(data.get("Username") == null){
			username=envProp.getProperty("defaultUsername");
			password=envProp.getProperty("defaultPass");
		}else{
			username=data.get("Username");
			password=data.get("Password");
		}
		getObject("username_id").sendKeys(username);
		getObject("emailsubmit_id").click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(getObject("password_id")));
		
		getObject("password_id").sendKeys(password);
		getObject("continue_id").click();
		// security warining - mozilla
		wait(5);
		
		acceptAlert();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("password_id")));
	}
	
	public void validateLogin(){
		test.log(Status.INFO, "Validating Login");
		boolean result = isElementPresent("portfolioSelection_xpath");
		String expectedResult=data.get("ExpectedResult");
		String actualResult="";
		
		if(result)	
			actualResult="Loginsuccess";
		else
			actualResult="Loginfailure";
		if(!actualResult.equals(expectedResult))
			reportFailure("Got  result "+actualResult );

		
		
	}
	
	public void defaultLogin(){
		test.log(Status.INFO, "Logging in with default ID");

		String username=envProp.getProperty("adminusername");
		String password=envProp.getProperty("adminpassword");
		System.out.println("Default username "+username );
		System.out.println("Default password "+password );
	}
	
	public void verifyPortFolio(){
		test.log(Status.INFO, "Verifying portfolio name "+data.get(dataKey));
		waitTillSelectionToBe("portfolioSelection_xpath",data.get(dataKey));
		
	}
	
	public void addStock(){
		test.log(Status.INFO, "Adding Stockdetails");
		waitForPageToLoad();
		click("addStockButton_id");
		type("stockName_id","Stock Name");
		click("firstOption_xpath");
		click("addStockCalendar_id");
		selectDate(data.get("Date"));
		type("stockQuantity_id","Quantity");
		type("stockPrice_id","PurchasePrice");
		click("addStockSubmit_id");
		waitForPageToLoad();
		test.log(Status.INFO, "Stock added and form submitted");
		test.log(Status.INFO, "Validating company in table");
		int rNum=getRowWithCellData(data.get("Stock Name"));
		if(rNum==-1)
			reportFailure("Could not find the Stock");
	}
	
	public void deleteStock(){
		waitForPageToLoad();
		int rNum=getRowWithCellData(data.get("Stock Name"));
		if(rNum==-1)
			reportFailure("Stock not found in list "+data.get("Stock Name"));
		driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rNum+"]/td[1]")).click();
		driver.findElements(By.xpath("//input[@name='Delete']")).get(rNum-1).click();

		driver.switchTo().alert().accept();
		waitForPageToLoad();
		driver.switchTo().defaultContent();
		rNum=getRowWithCellData(data.get("Stock Name"));
		System.out.println(rNum);
		reportFailure("Stock found after deletion "+data.get("Stock Name"));
	}
	
	public void buySellStock(){
		test.log(Status.INFO, "Finding Stock");
		int rNum=getRowWithCellData(data.get("Stock Name"));
		if(rNum==-1)
			reportFailure("Stock not found in list "+data.get("Stock Name") );
		
		driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rNum+"]/td[1]")).click();
		driver.findElements(By.xpath("//input[@class='buySell']")).get(rNum-1).click();
		test.log(Status.INFO, "Performing action - "+data.get("Action"));
		click("buySellCalendar_id");
		new Select(getObject("actionBuySell_id")).selectByVisibleText(data.get("Action"));
		selectDate(data.get("Date"));
		type("buysellqty_name", "Quantity");
		type("buysellprice_name","PurchasePrice");
		click("buySellStockButton_id");
		waitForPageToLoad();
		test.log(Status.INFO, "Buy/Sell Action SuccessFull");

	}
	
	public void checkHistory(){
		waitForPageToLoad();
		test.log(Status.INFO, "Checking history");
		int rNum=getRowWithCellData(data.get("Stock Name"));
		if(rNum==-1)
			reportFailure("Stock not found in list "+data.get("Stock Name") );
	
		driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rNum+"]/td[1]")).click();
		driver.findElements(By.xpath("//input[@class='equityTransaction']")).get(rNum-1).click();
		String actual=driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rNum+"]/td[5]")).getText();
		List<WebElement> shares = driver.findElements(By.xpath(prop.getProperty("shares_xpath")));
		List<WebElement> prices = driver.findElements(By.xpath(prop.getProperty("prices_xpath")));
		
		int totalShares=0;
		int totalAmount=0;
		for(int i=0;i<prices.size();i++){
			int share = Integer.parseInt(shares.get(i).getText());
			int price = Integer.parseInt(prices.get(i).getText());
			totalShares = share + totalShares;
			totalAmount = totalAmount + (share*price);
		}

		test.log(Status.INFO,"Total shares - "+totalShares );
		test.log(Status.INFO,"Total Amount spent "+totalAmount );
		double average = Double.valueOf(totalAmount)/Double.valueOf(totalShares);
		test.log(Status.INFO,"Average - "+average );
		test.log(Status.INFO,"Actual - "+actual );
		
		reportFailure("Actual average is "+ actual+"Expected was "+average);
		// fix the decimals - Math
	}
	
	
	public void selectDate(String d){
		// day, month , year
		Date current = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date selected = sd.parse(d);
			String day = new SimpleDateFormat("d").format(selected);
			String month = new SimpleDateFormat("MMMM").format(selected);
			String year = new SimpleDateFormat("yyyy").format(selected);
			System.out.println(day+" --- "+month+" --- "+ year);
			String desiredMonthYear=month+" "+year;
			
			while(true){
				String displayedMonthYear=driver.findElement(By.cssSelector(".dpTitleText")).getText();
				if(desiredMonthYear.equals(displayedMonthYear)){
					// select the day
					driver.findElement(By.xpath("//td[text()='"+day+"']")).click();
					break;
				}else{
					
					if(selected.compareTo(current) > 0)
						driver.findElement(By.xpath("//*[@id='datepicker']/table/tbody/tr[1]/td[4]/button")).click();
					else if(selected.compareTo(current) < 0)
						driver.findElement(By.xpath("//*[@id='datepicker']/table/tbody/tr[1]/td[2]/button")).click();

				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getRowWithCellData(String data){
		List<WebElement> rows = driver.findElements(By.xpath("//table[@id='stock']/tbody/tr"));
		for(int rNum=0;rNum<rows.size();rNum++){
			WebElement row = rows.get(rNum);
			List<WebElement> cells = row.findElements(By.tagName("td"));
			for(int cNum=0;cNum<cells.size();cNum++){
				WebElement cell = cells.get(cNum);
				if(!cell.getText().trim().equals("") && data.contains(cell.getText()))
					return ++rNum;
			}
		}
		
		return -1;// not found
	}
	
}

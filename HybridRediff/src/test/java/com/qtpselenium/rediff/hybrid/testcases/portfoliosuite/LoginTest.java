package com.qtpselenium.rediff.hybrid.testcases.portfoliosuite;

import java.util.Hashtable;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.qtpselenium.rediff.hybrid.base.BaseTest;
import com.qtpselenium.rediff.hybrid.util.Constants;
import com.qtpselenium.rediff.hybrid.util.DataUtil;

public class LoginTest extends BaseTest {

	
	@Test(dataProvider="getData")
	public void loginTest(Hashtable<String,String> data) throws Exception{
		
		test.log(Status.INFO, "Starting "+ testName);

		if(DataUtil.isSkip(testName, xls) ||data.get(Constants.RUNMODE_COL).equals(Constants.RUNMODE_NO)){
			test.log(Status.SKIP, "Runmode is set to NO");
			throw new SkipException("Runmode is set to NO");
		}		
	    ds.executeKeywords(testName, xls, data);
	}
}

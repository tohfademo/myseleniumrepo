package com.qtpselenium.rediff.hybrid.base;

import java.util.Arrays;

import org.testng.TestNG;

public class ParallelExec {

	public static void main(String[] args) {
		TestNG testng = new TestNG(); 
		testng.setTestSuites(Arrays.asList(new String[] {System.getProperty("user.dir")+"//src/test/resources/testng.xml"}));

		testng.setSuiteThreadPoolSize(2);
		
		testng.run();

	}

}

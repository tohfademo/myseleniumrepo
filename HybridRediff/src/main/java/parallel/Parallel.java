package parallel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;



import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

public class Parallel {

	public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, IOException, org.xml.sax.SAXException {
		TestNG testng = new TestNG(); 
		//testng.setXmlSuites((List <XmlSuite>)(new Parser(System.getProperty("user.dir")+"//src//test//resources//testng.xml").parse()));
		testng.setTestSuites(Arrays.asList(new String[] {System.getProperty("user.dir")+"//src/test/resources/testng.xml"}));

		testng.setSuiteThreadPoolSize(2);
		
		testng.run();
    }	

}

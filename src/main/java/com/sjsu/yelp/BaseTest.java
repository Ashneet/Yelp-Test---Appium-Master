package com.fox.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;

import com.taf.core.TestBedManager;
import com.taf.core.exception.DefectException;
import com.taf.core.exception.ProfileBuilderException;
import com.taf.util.LogUtil;

/**
 * Base test that all the test classes should extend. This class initializes
 * {@link TestBedManager} based on configuration file input
 * 
 */

public class BaseTest {

	static Log log = LogUtil.getLog(BaseTest.class);

	protected TestBedManager testBedManager = null;
	private Map<String, String> map;
	protected static String defect_properties_file = null;
	public long startTime = 0;
	public int methodNum = 0;

	/**
	 * @param method
	 */
	@BeforeMethod()
	public void printTimeStart(Method method) {
		methodNum = methodNum + 1;
		log.info("<.<.<.<.<.<.<.<.<.<.< Starting method " + methodNum + "::"
				+ method.getName() + "<.<.<.<.<.<.<.<.<.<.<");
		startTime = System.currentTimeMillis();
	}

	/**
	 * @param method
	 */
	@AfterMethod()
	public void printTimeTaken(Method method) {
		log.info(">.>.>.>.>.>.>.>.>.>.>.>.> Ending method " + methodNum + "::"
				+ method.getName() + ">.>.>.>.>.>.>.>.>.>.>.>.>Time Taken ::"
				+ (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		sendAttachmentEmail();
		WebDriver driver = (WebDriver) TestBedManager.INSTANCE
				.getCurrentTestBed().getDriver();
		try {
			driver.wait(150000);
		} catch (Exception e) {
		}
		driver.close();
		driver.quit();
	}
	/*
	* @author: Ashneet Lattar
	* Generic DataProvider
	**/
	private Object[][] getData(String sht) throws IOException {
		System.out.println("inside get Data");
		map = new LinkedHashMap<String, String>();
		Workbook workBook = null;
		InputStream inputStream = getInputFileStream(fileName);
		if (fileName.toLowerCase().endsWith("xlsx") == true) {
			workBook = new XSSFWorkbook(inputStream);
		} else if (fileName.toLowerCase().endsWith("xls") == true) {
			workBook = new HSSFWorkbook(inputStream);
		}
		inputStream.close();
		Sheet sheet = workBook.getSheet(sht);
		int rows = sheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rows; i++) {
			Row row = sheet.getRow(i);
			map.put(row.getCell(0).toString(),
					new DataFormatter().formatCellValue(row.getCell(1)));
		}
		return new Object[][] { { map } };
	}

	private InputStream getInputFileStream(String fileName)
			throws FileNotFoundException {
		ClassLoader loader = this.getClass().getClassLoader();
		InputStream inputStream = loader.getResourceAsStream(fileName);
		if (inputStream == null) {
			inputStream = new FileInputStream(new File(fileName));
		}
		return inputStream;
	}

	public void excelUpdate(long timeTaken,
			String status, int index, String sht) {
		try {
			log.info(status);
			InputStream file = getInputFileStream(fileName);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(sht);
			Cell cell = null;
			Row row = sheet.getRow(index);
			XSSFCellStyle style = workbook.createCellStyle();
			
			if(status.equals("Fail"))
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			else
				style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			
			// Update the value of cell
			cell = row.createCell(3);
			cell.setCellValue(timeTaken);
			
			cell = row.createCell(4);
			cell.setCellValue(status);
			cell.setCellStyle(style);
			file.close();

			FileOutputStream outFile = new FileOutputStream(fileName);
			workbook.write(outFile);
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	* @author: Ashneet Lattar
	* Send Email Utility
	**/
	private void sendAttachmentEmail(){
		 // Recipient's email ID needs to be mentioned.
	      String to1 = "nish25sp@gmail.com";
	      String to2 = "thirdpen@gmail.com";
	      String to3 = "ashneet.468@gmail.com";
	      String to4 = "kaul_ankita@yahoo.com";
	      String to5 = "simran.pruthi2010@gmail.com";
	      String to6 = "sourabh.bhosale@gmail.com";

	      // Sender's email ID needs to be mentioned
	      String from = "thirdpen@gmail.com";
	      String pass = "@*****$";

	      // Assuming you are sending email from localhost
	      String host = "smtp.gmail.com";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      properties.setProperty("mail.smtp.starttls.enable", "true");
	      properties.setProperty("mail.smtp.user", from);
	      properties.setProperty("mail.smtp.password", pass);
	      properties.setProperty("mail.smtp.port", "587");
	      properties.setProperty("mail.smtp.auth", "true");
          
	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties,new Authenticator() {
	          protected PasswordAuthentication  getPasswordAuthentication() {
	              return new PasswordAuthentication(
	                          "thirdpen@gmail.com", "@*****$");
	                      }
	          });

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to1));
	         message.addRecipient(Message.RecipientType.CC,
                     new InternetAddress(to2));
	         message.addRecipient(Message.RecipientType.CC,
                     new InternetAddress(to3));
	         message.addRecipient(Message.RecipientType.CC,
                     new InternetAddress(to4));
	         message.addRecipient(Message.RecipientType.CC,
                     new InternetAddress(to5));
	         message.addRecipient(Message.RecipientType.CC,
                     new InternetAddress(to6));

	         // Set Subject: header field
	         message.setSubject("CMPE 287 Automation Test Report");

	         // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setText("The attached file is the result of all the test valid test credentials.\n This is system generated mail do not reply..");
	         
	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         messageBodyPart = new MimeBodyPart();
//	         String filename = "file.txt";
	         DataSource source = new FileDataSource(fileName);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName("CMPE 287 Automation Report.xlsx");
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart );

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	    	
	}
}
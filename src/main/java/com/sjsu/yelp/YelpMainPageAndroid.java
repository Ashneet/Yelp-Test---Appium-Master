package com.sjsu.yelp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fox.common.BaseTest;
import com.taf.core.TestBedManager;
import com.taf.core.driver.SwipeableWebDriver;
import com.taf.core.exception.PageException;
import com.taf.util.LogUtil;

public class YelpMainPageAndroid extends BaseTest {
	private WebDriver driver;
	private WebDriverWait wait;
	private WebElement LoginBtn;
	private WebElement SetBtn;
	private WebElement moreBtn;
	private WebElement emailTextField;
	private WebElement PwdTextField;
	private WebElement LogInBtn;
	private WebElement LogOutBtn;
	private WebElement successLogIn;
	private WebElement signUpBtn;
	private WebElement settingHead;
	private String bckUpKey;
	private String bckUpValue;
	private long CredentialstartTime = 0;
	private long CredentialstopTime = 0;
	private WebElement popUpTitle;
	private WebElement popUpMsg;

	private static Log log = LogUtil.getLog(YelpMainPageAndroid.class);

	private ExpectedCondition<WebElement> visibilityOfElementLocated(final By by) {
		return new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement element = driver.findElement(by);
				return element.isDisplayed() ? element : null;
			}
		};
	}

	private ExpectedCondition<List<WebElement>> listVisibilityOfElementLocated(
			final By by) {
		return new ExpectedCondition<List<WebElement>>() {
			public List<WebElement> apply(WebDriver driver) {
				List<WebElement> element = driver.findElements(by);
				return element;
			}
		};
	}

	@BeforeTest
	public void setUp() {
		driver = (SwipeableWebDriver) TestBedManager.INSTANCE
				.getCurrentTestBed().getDriver();
		wait = new WebDriverWait(driver, 0);
	}
	/*
	* @author: Ashneet Lattar
	* Login Test
	**/
	@Test(dataProvider = "ValidCredentialInput")
	public void testYelp(Map<String, String> mp) throws PageException {
		log.debug("inside test");
		try {

			moreBtn = wait.until(visibilityOfElementLocated(By
					.className("android.widget.ImageButton")));
			moreBtn.click();
			SetBtn = wait
					.until(visibilityOfElementLocated(By.name("Settings")));
			SetBtn.click();
			signUpBtn = wait.until(visibilityOfElementLocated(By
					.id("com.yelp.android:id/sign_up")));
			wait.until(
					visibilityOfElementLocated(By
							.id("android:id/action_bar_title"))).click();
		} catch (Exception e) {
			wait.until(
					visibilityOfElementLocated(By
							.id("android:id/action_bar_title"))).click();
			logout();
		}
		int index = 0;
		for (String key : mp.keySet()) {
			try {
				++index;
				bckUpKey = key;
				bckUpValue = mp.get(key);

				moreBtn = wait.until(visibilityOfElementLocated(By
						.className("android.widget.ImageButton")));
				moreBtn.click();
				LoginBtn = wait.until(visibilityOfElementLocated(By
						.name("Log In")));
				LoginBtn.click();
				emailTextField = wait
						.until(visibilityOfElementLocated(By
								.id("com.yelp.android:id/activity_login_editUsername")));
				emailTextField.click();
				emailTextField.sendKeys(key);

				PwdTextField = wait
						.until(visibilityOfElementLocated(By
								.id("com.yelp.android:id/activity_login_editPassword")));
				PwdTextField.click();

				PwdTextField.sendKeys(mp.get(key));

				LogInBtn = wait.until(visibilityOfElementLocated(By
						.id("com.yelp.android:id/activity_login_btnLogin")));
				LogInBtn.click();
				CredentialstartTime = System.currentTimeMillis();
				successLogIn = wait.until(visibilityOfElementLocated(By
						.id("com.yelp.android:id/nearby")));

				log.info("::Element::" + successLogIn);
				log.info("::Text::" + successLogIn.getText());
				
				 /*Assert.assertEquals(actualURL,
				 inputs.getParamMap().get("expected"), "URL did not matched");
				 */
				Assert.assertEquals(successLogIn.getText(), "Nearby");
				CredentialstopTime = System.currentTimeMillis();

				excelUpdate(
						TimeUnit.MILLISECONDS
								.toSeconds((CredentialstopTime - CredentialstartTime)),
						"Pass", index, "Valid Credentials");
				logout();

			} catch (AssertionError e) {
				excelUpdate(0, "Fail", index, "Valid Credentials");
				log.info("the test case 1 failed");
			} catch (Exception e) {
				excelUpdate(0, "Fail", index, "Valid Credentials");
				try {

					if (wait.until(
							visibilityOfElementLocated(By
									.id("android:id/message"))).getText()
							.equals("Wrong email or password."))
						wait.until(
								visibilityOfElementLocated(By
										.id("android:id/button2"))).click();
					else if ((wait.until(
							visibilityOfElementLocated(By
									.id("android:id/message"))).getText()
							.equals("Please enter your email address."))
							|| (wait.until(
									visibilityOfElementLocated(By
											.id("android:id/message")))
									.getText()
									.equals("Please enter your password.")))
						wait.until(
								visibilityOfElementLocated(By
										.id("android:id/button3"))).click();
					wait.until(
							visibilityOfElementLocated(By
									.id("android:id/action_bar_title")))
							.click();
				} catch (Exception e1) {
					excelUpdate(0, "Fail", index, "Valid Credentials");
				}
				log.info(":::Test Case Failed");
			}
		}
	}

	private void logout() {
		moreBtn = wait.until(visibilityOfElementLocated(By
				.className("android.widget.ImageButton")));
		moreBtn.click();
		SetBtn = wait.until(visibilityOfElementLocated(By.name("Settings")));
		SetBtn.click();
		LogOutBtn = wait.until(visibilityOfElementLocated(By
				.id("com.yelp.android:id/login_logout")));
		LogOutBtn.click();
		SetBtn = wait.until(visibilityOfElementLocated(By.name("Settings")));
		SetBtn.click();
	}

	@Test(dataProvider = "InvalidCredentialInput")
	public void InvalidUsers(Map<String, String> mp) throws PageException {

		log.debug("abcTest inside test2");
		try {

			moreBtn = wait.until(visibilityOfElementLocated(By
					.className("android.widget.ImageButton")));
			moreBtn.click();
			SetBtn = wait
					.until(visibilityOfElementLocated(By.name("Settings")));
			SetBtn.click();
			signUpBtn = wait.until(visibilityOfElementLocated(By
					.id("com.yelp.android:id/sign_up")));
			wait.until(
					visibilityOfElementLocated(By
							.id("android:id/action_bar_title"))).click();
		} catch (Exception e) {
			wait.until(
					visibilityOfElementLocated(By
							.id("android:id/action_bar_title"))).click();
			logout();
		}
		int index = 0;

		for (String key : mp.keySet()) {
			try {
				++index;
				bckUpKey = key;
				bckUpValue = mp.get(key);

				moreBtn = wait.until(visibilityOfElementLocated(By
						.className("android.widget.ImageButton")));
				moreBtn.click();
				LoginBtn = wait.until(visibilityOfElementLocated(By
						.name("Log In")));
				LoginBtn.click();

				emailTextField = wait
						.until(visibilityOfElementLocated(By
								.id("com.yelp.android:id/activity_login_editUsername")));
				emailTextField.click();
				emailTextField.sendKeys(key);

				PwdTextField = wait
						.until(visibilityOfElementLocated(By
								.id("com.yelp.android:id/activity_login_editPassword")));
				PwdTextField.click();

				PwdTextField.sendKeys(mp.get(key));

				LogInBtn = wait.until(visibilityOfElementLocated(By
						.id("com.yelp.android:id/activity_login_btnLogin")));
				LogInBtn.click();
				CredentialstartTime = System.currentTimeMillis();

				/*
				 * Pop up 1; Alert Title - android:id/alertTitle - Log In Alert
				 * Message - android:id/message - Please enter your email
				 * address. Button - android:id/button3 - OK
				 * 
				 * Pop Up 2; Alert Title - android:id/alertTitle - Log In Alert
				 * Message - android:id/message - Please enter your password.
				 * Button - android:id/button3 - OK
				 * 
				 * Pop Up 3; Alert Title - android:id/alertTitle - Log In Alert
				 * Message - android:id/message - Wrong email or password.
				 * Button - android:id/button2 - Try Again; android:id/button1 -
				 * I forgot my password
				 */

				popUpTitle = wait.until(visibilityOfElementLocated(By
						.id("android:id/alertTitle")));
				CredentialstopTime = System.currentTimeMillis();
				Assert.assertEquals(popUpTitle.getText(), "Log In");
				{
					excelUpdate(
							TimeUnit.MILLISECONDS
									.toSeconds((CredentialstopTime - CredentialstartTime)),
							"Pass", index, "Invalid Credentials");
				}
				popUpMsg = wait.until(visibilityOfElementLocated(By
						.id("android:id/message")));
				if (popUpMsg.getText().equals("Please enter your email")
						|| popUpMsg.getText().equals(
								"Please enter your password."))
					wait.until(
							visibilityOfElementLocated(By
									.id("android:id/button3"))).click();
				else
					wait.until(
							visibilityOfElementLocated(By
									.id("android:id/button2"))).click();
				wait.until(
						visibilityOfElementLocated(By
								.id("android:id/action_bar_title"))).click();
			} catch (AssertionError e1) {
				log.info("the test case 1 failed");
				CredentialstopTime = System.currentTimeMillis();
				excelUpdate(
						TimeUnit.MILLISECONDS
								.toSeconds((CredentialstopTime - CredentialstartTime)),
						"Fail", index, "Invalid Credentials");
			} catch (Exception e) {
				CredentialstopTime = System.currentTimeMillis();
				excelUpdate(
						TimeUnit.MILLISECONDS
								.toSeconds((CredentialstopTime - CredentialstartTime)),
						"Fail", index, "Invalid Credentials");
				logout();
			}
		}
	}

	/*
	* @author: Ashneet Lattar
	* Login Test
	**/
	@Test(priority = 1)
	public void searchAndAddReviewTest() {
		try {
			wait.until(
					visibilityOfElementLocated(By
							.id("com.yelp.android:id/nearby"))).click();
			wait.until(visibilityOfElementLocated(By.name("More Categories")))
					.click();
			List<WebElement> row = wait.until(listVisibilityOfElementLocated(By
					.id("com.yelp.android:id/category_content")));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			HashMap<String, Double> swipeListObject = new HashMap<String, Double>();
			swipeListObject.put("startX", 0.5);
			swipeListObject.put("startY", 0.7);
			swipeListObject.put("endX", 0.5);
			swipeListObject.put("endY", 0.3);
			swipeListObject.put("duration", .9);
			js.executeScript("mobile: swipe", swipeListObject);

			HashMap<String, Double> swipeRestObject = new HashMap<String, Double>();

			for (WebElement w : row) {
				log.info("::: Web Element: " + ((RemoteWebElement) w).getText());
				w.click();

				List<WebElement> restRow = wait
						.until(listVisibilityOfElementLocated(By
								.id("com.yelp.android:id/search_inner_layout")));
				swipeRestObject.put("startX", 0.5);
				swipeRestObject.put("startY", 0.7);
				swipeRestObject.put("endX", 0.5);
				swipeRestObject.put("endY", 0.3);
				swipeRestObject.put("duration", .9);
				js.executeScript("mobile: swipe", swipeRestObject);

				for (WebElement wr : restRow) {
					log.info("::: Web Element: "
							+ ((RemoteWebElement) wr).getText());
					wr.click();
					
					  wait.until(
					 * visibilityOfElementLocated(By.id("android:id/up")))
					 * .click();
					 
					try {
						WebElement review;
						wait.until(
								visibilityOfElementLocated(By
										.id("com.yelp.android:id/write_review")))
								.click();
						Assert.assertEquals(
								wait.until(
										visibilityOfElementLocated(By
												.id("com.yelp.android:id/actionbar_title")))
										.getText(), "Write Review");
						wait.until(
								visibilityOfElementLocated(By
										.id("com.yelp.android:id/review_overview_stars")))
								.click();
						review = wait
								.until(visibilityOfElementLocated(By
										.id("com.yelp.android:id/review_overview_text")));
						review.click();
						review  = wait
								.until(visibilityOfElementLocated(By
										.id("com.yelp.android:id/review_compose_edit_text")));
						review.click();
						review.sendKeys(
										"my name is abcTest. I am testing yelp app via appium. My college project");
						wait.until(
								visibilityOfElementLocated(By
										.id("com.yelp.android:id/add_review_next")))
								.click();

						wait.until(
								visibilityOfElementLocated(By
										.id("com.yelp.android:id/add_review_next")))
								.click();
						wait.until(
								visibilityOfElementLocated(By
										.id("com.yelp.android:id/review_complete_close")))
								.click();
						Assert.assertEquals(wait.until(
								visibilityOfElementLocated(By
										.name("Finish Review"))).getText(), "Finish Review");
						wait.until(
								visibilityOfElementLocated(By
										.id("android:id/action_bar_title")))
								.click();
						
					} catch (Exception e) {
						try {
							WebElement el;
							Assert.assertEquals(
									wait.until(
											visibilityOfElementLocated(By
													.id("android:id/action_bar_title")))
											.getText(), "Log In");

							el = wait
									.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/activity_login_editUsername")));
							el.click();
							el.sendKeys("test.yelp@gmail.com");

							el = wait
									.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/activity_login_editPassword")));
							el.click();
							el.sendKeys("spiderman");

							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/activity_login_btnLogin")))
									.click();

							Assert.assertEquals(
									wait.until(
											visibilityOfElementLocated(By
													.id("com.yelp.android:id/actionbar_title")))
											.getText(), "Write Review");
							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/review_overview_stars")))
									.click();

							el = wait
									.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/review_compose_edit_text")));
							el.click();
							el.sendKeys("my name is abcTest. I am testing yelp app via appium. My college project");
							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/add_review_next")))
									.click();
							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/add_review_next")))
									.click();
							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/review_complete_close")))
									.click();
							Assert.assertEquals(wait.until(
									visibilityOfElementLocated(By
											.name("Finish Review"))).getText(), "Finish Review");
							wait.until(
									visibilityOfElementLocated(By
											.id("android:id/action_bar_title")))
									.click();

						} catch (AssertionError ex) {
							ex.printStackTrace();
							log.info("Failed");

						}
					} catch (AssertionError e1) {
						try {
							WebElement el;
							Assert.assertEquals(
									wait.until(visibilityOfElementLocated(By
											.id("android:id/action_bar_title"))),
									"Log In");

							el = wait
									.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/activity_login_editUsername")));
							el.click();
							el.sendKeys("abc.test@sjsu.edu");

							el = wait
									.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/activity_login_editPassword")));
							el.click();
							el.sendKeys("$******@");

							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/activity_login_btnLogin")))
									.click();

							Assert.assertEquals(
									wait.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/actionbar_title"))),
									"Write Review");
							wait.until(
									visibilityOfElementLocated(By
											.id("com.yelp.android:id/review_overview_stars")))
									.click();

							el = wait
									.until(visibilityOfElementLocated(By
											.id("com.yelp.android:id/review_compose_edit_text")));
							el.click();
							el.sendKeys("my name is abcTest. I am testing yelp app via appium. My college project");

						} catch (AssertionError ex) {
							ex.printStackTrace();
							log.info("Failed");

						}

					}
				}
				wait.until(visibilityOfElementLocated(By.id("android:id/up")))
						.click();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
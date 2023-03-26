import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;


public class BrowserMobProxyExample2 {
    WebDriver             driver;
   
   @Test
    public void browserMobProxyTest() throws InterruptedException, IOException {
	   teardown();
	   //proxy.newHar("google.com");
        driver.get("https://www.google.com");
       // driver.get("https://www.youtube.com");

        Thread.sleep(2000);
        getHAR(driver,"gg");
//        proxy.stop();
//        Har har = proxy.getHar();
//        File harFile = new File("google.har");
//        har.writeTo(harFile);
    }

    public void teardown() {
        
    	ChromeOptions chromeOptions = new ChromeOptions();
    	chromeOptions.addArguments("ignore-certificate-errors");
    	chromeOptions.addArguments("disable-infobars");
    	chromeOptions.addArguments("start-maximized");
    	chromeOptions.addArguments("--remote-allow-origins=*");

    	// More Performance Traces like devtools.timeline, enableNetwork and enablePage
    	Map<String, Object> perfLogPrefs = new HashMap<>();
    	perfLogPrefs.put("traceCategories", "browser,devtools.timeline,devtools");
    	perfLogPrefs.put("enableNetwork", true);
    	perfLogPrefs.put("enablePage", true);
    	chromeOptions.setExperimentalOption("perfLoggingPrefs", perfLogPrefs);

    	// For Enabling performance Logs for WebPageTest
    	LoggingPreferences logPrefs = new LoggingPreferences();
    	logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
    	chromeOptions.setCapability("goog:loggingPrefs", logPrefs);

    	
        WebDriverManager.chromedriver().setup();       
    	 driver = new ChromeDriver(chromeOptions);
    }
    
    private static JSONArray getPerfEntryLogs(WebDriver driver) {
    	LogEntries logEntries = driver.manage().logs().get(LogType.PERFORMANCE);
    	JSONArray perfJsonArray = new JSONArray();
    	logEntries.forEach(entry -> {
    		JSONObject messageJSON = new JSONObject(entry.getMessage()).getJSONObject("message");
    		perfJsonArray.put(messageJSON);
    	});
    	return perfJsonArray;
    }
    
    public static void getHAR(WebDriver driver, String fileName) throws IOException {
    	String destinationFile = "./" + fileName + ".har";
    	((JavascriptExecutor) driver).executeScript(
    			"!function(e,o){e.src=\"https://cdn.jsdelivr.net/gh/Ankit3794/chrome_har_js@master/chromePerfLogsHAR.js\",e.onload=function(){jQuery.noConflict(),console.log(\"jQuery injected\")},document.head.appendChild(e)}(document.createElement(\"script\"));");
    	File file = new File(destinationFile);
    	file.getParentFile().mkdirs();
    	FileWriter harFile = new FileWriter(file);
    	harFile.write((String) ((JavascriptExecutor) driver).executeScript(
    			"return module.getHarFromMessages(arguments[0])", getPerfEntryLogs(driver).toString()));
    	harFile.close();
    }
}

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import lombok.SneakyThrows;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;


public class BrowserMobProxyExample {
    WebDriver             driver;
    BrowserMobProxyServer proxy;
    Proxy                 seleniumProxy;

   
    
    public void setup() throws UnknownHostException {
        //Proxy Operations
        proxy = new BrowserMobProxyServer();
        proxy.start(0);
        seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        String hostIp = Inet4Address.getLocalHost().getHostAddress();
        seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
        seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        WebDriverManager.chromedriver().setup();       
        
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.PROXY, seleniumProxy);
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--ignore-urlfetcher-cert-requests");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
    }

   @Test
    public void browserMobProxyTest() throws InterruptedException, IOException {
	   setup();
	   proxy.newHar("google.com");
        driver.get("https://www.google.com");
        driver.get("https://www.youtube.com");

        Thread.sleep(2000);
        proxy.stop();
        Har har = proxy.getHar();
        File harFile = new File("google.har");
        har.writeTo(harFile);
    }

    public void teardown() {
        
        //driver.quit();
    }
}

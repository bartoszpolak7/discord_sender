package io.github.bartoszpolak7.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@Tag("e2e")
class HomePageE2ETest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    void pageLoadsWithPolishTitleAndConfig() {
        String url = System.getenv("APP_URL");
        assertNotNull(url, "APP_URL environment variable must be set");

        assertEquals("Ostoja na rozwidleniu", driver.getTitle());
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !d.findElement(By.id("config")).getText().isBlank());
    }
}

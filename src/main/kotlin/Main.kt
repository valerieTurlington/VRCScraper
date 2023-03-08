import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

object VRCWorldFinder {
    private val worldIds: MutableSet<String> = HashSet()
    private val driver by lazy {
        val chromeOptions = ChromeOptions().apply {
//            addArguments("--headless=new")
            addArguments("--remote-allow-origins=*")
            addArguments("user-data-dir=C:\\Users\\twili\\AppData\\Local\\Google\\Chrome\\User Data\\Default")
        }
        ChromeDriver(chromeOptions).apply {
            manage().timeouts().implicitlyWait(Duration.ofSeconds(5))
            manage().window().maximize()
        }
    }
    private val wait by lazy { WebDriverWait(driver, Duration.ofSeconds(10)) }

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            System.setProperty("webdriver.chrome.driver", "D:\\Downloads\\Chromedriver\\chromedriver.exe")
            login()
            getWorlds()
        } finally {
            driver.quit()
        }
    }

    private fun login() {
        driver.get("https://vrchat.com/home/worlds")
        if (driver.currentUrl != "https://vrchat.com/home/worlds") {
            wait.until { ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("input")) }
            val userInput = driver.findElement(By.cssSelector("input#username_email"))
            userInput.sendKeys("barbiewonkenobi@gmail.com")
            val passwordInput = driver.findElement(By.cssSelector("input#password"))
            passwordInput.sendKeys("WorldBarbie")
            val loginButton = driver.findElement(By.cssSelector("button"))
            loginButton.click()
        } else {
            println("Didn't need to log in, I guess.")
        }
    }

    private fun getWorlds() {
        if (driver.currentUrl != "https://vrchat.com/home/worlds") {
            driver.get("https://vrchat.com/home/worlds")
        } else {
            Thread.sleep(2000)
        }
        wait.until {
            ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.world-row"))
        }
        val worldRows = driver.findElements(By.cssSelector("div.world-row"))
        for (worldRow in worldRows) {
            getWorlds(worldRow)
        }
    }

    private fun getWorlds(worldRow: WebElement) {
        val nextButton = worldRow.findElement(By.cssSelector("div.right-world-nav"))
        var worlds = worldRow.findElements(By.cssSelector("div.world-list > div.world > a"))
        worldIds.addAll(worlds.map { it.getAttribute("href").substringAfter("/home/world/") })
        var worldIdsSize = worldIds.size
        var stayedCount = 0
        for (i in 0 until 50) {
            nextButton.click()
            Thread.sleep(2000)
            worlds = worldRow.findElements(By.cssSelector("div.world-list > div.world > a"))
            worldIds.addAll(worlds.map { it.getAttribute("href").substringAfter("/home/world/") })
            if (worldIds.size == worldIdsSize) {
                stayedCount++
                if (stayedCount > 2) {
                    break
                }
            } else {
                stayedCount = 0
            }
            worldIdsSize = worldIds.size
        }
    }
}


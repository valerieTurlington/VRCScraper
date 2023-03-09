import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration

object VRCWorldFinder {
    private val worldIds: HashMap<String, HashSet<String>> = HashMap()
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
            saveWorlds()
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
        val worldRows = driver.findElements(By.cssSelector("div.row > div > div"))
        val nextButtons = HashSet<WebElement>()
        val worldIdsSize = HashMap<String, Int>()
        val stayedCount = HashMap<String, Int>()
        val checkingWorldRow = HashMap<String, Boolean>()
        for (worldRow in worldRows) {
            val bucketName = worldRow.findElement(By.cssSelector("h4")).text
            worldIds[bucketName] = HashSet()
            nextButtons.add(worldRow.findElement(By.cssSelector("div.right-world-nav")))
            val worlds = worldRow.findElements(By.cssSelector("div.world-list > div.world > a"))
            worldIds[bucketName]!!.addAll(worlds.map { it.getAttribute("href").substringAfter("/home/world/") })
            worldIdsSize[bucketName] = worldIds[bucketName]!!.size
            stayedCount[bucketName] = 0
            checkingWorldRow[bucketName] = true
        }
        var i = 0
        do {
            for (worldRow in worldRows) {
                val bucketName = worldRow.findElement(By.cssSelector("h4")).text
                if (checkingWorldRow[bucketName]!!) {
                    val bucket = worldIds[bucketName]!!
                    val worlds = worldRow.findElements(By.cssSelector("div.world-list > div.world > a"))
                    for (world in worlds) {
                        bucket.add(world.getAttribute("href").substringAfter("/home/world/"))
                    }
                    if (bucket.size == worldIdsSize[bucketName]) {
                        stayedCount[bucketName]!!.plus(1)
                        if (stayedCount[bucketName]!! > 2) {
                            checkingWorldRow[bucketName] = false
                        }
                    } else {
                        stayedCount[bucketName] = 0
                    }
                    worldIdsSize[bucketName] = worldIds[bucketName]!!.size
                }
            }
            for (button in nextButtons) {
                button.click()
            }
            Thread.sleep(2000)
            println("Finished iteration: ${i++}")
        } while (i < 25)
    }

    private fun saveWorlds() {
        for (bucket in worldIds) {
            val idFile = Paths.get("${bucket.key}/ids.txt")
            Files.write(idFile, bucket.value)
        }
    }
}


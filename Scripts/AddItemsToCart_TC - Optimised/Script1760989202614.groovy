import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.WebElement as WebElement
import java.math.BigDecimal as BigDecimal
import java.text.NumberFormat as NumberFormat
import java.util.Locale as Locale

// ---------------- CONFIG ----------------
WebUI.callTestCase(findTestCase('EmptyCart_TC'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Shop'))

String shopUrl = 'https://cms.demo.katalon.com/shop/'
String cartUrl = 'https://cms.demo.katalon.com/cart/'

List<Map<String,String>> productsToAdd = [
    [
        name: 'Flying Ninja',
        shopPriceXpath: "//h2[text()='Flying Ninja']//following::span[contains(@class,'price')][1]",
        addToCartXpath: "//h2[text()='Flying Ninja']//following::a[contains(@class,'add_to_cart') or contains(text(),'Add to cart')][1]"
    ],
    [
        name: 'Happy Ninja',
        shopPriceXpath: "//h2[text()='Happy Ninja']//following::span[contains(@class,'price')][1]",
        addToCartXpath: "//h2[text()='Happy Ninja']//following::a[contains(@class,'add_to_cart') or contains(text(),'Add to cart')][1]"
    ]
]

int waitTimeout = 8
int smallDelay = 1

// ---------------- HELPER: parse $12.00 to BigDecimal ----------------
BigDecimal parseCurrencyToBigDecimal(String currencyText) {
    if (!currencyText) return BigDecimal.ZERO
    currencyText = currencyText.trim()

    // Regex to match first number, with optional commas/dots
    def matcher = currencyText =~ /([0-9,.]+)/
    if (!matcher.find()) return BigDecimal.ZERO

    String numberStr = matcher.group(1)

    // Remove all commas
    numberStr = numberStr.replaceAll(",", "")

    // Keep only one decimal point
    int firstDot = numberStr.indexOf('.')
    if (firstDot >= 0) {
        // remove any other dots
        numberStr = numberStr.substring(0, firstDot + 1) + numberStr.substring(firstDot + 1).replaceAll("\\.", "")
    }

    return new BigDecimal(numberStr)
}

// ---------------- STEP 1: Navigate to Shop page ----------------
WebUI.comment("Navigating to Shop page: ${shopUrl}")
WebUI.navigateToUrl(shopUrl)
WebUI.waitForPageLoad(waitTimeout)

// Containers for expected prices and names
Map<String, BigDecimal> expectedPriceByProduct = [:]
List<String> expectedProducts = []

// ---------------- STEP 2: Add products to cart ----------------
productsToAdd.each { prod ->
    String name = prod.name
    String priceXpath = prod.shopPriceXpath
    String addXpath = prod.addToCartXpath

    WebUI.comment("Processing product: ${name}")

    // Dynamic TestObject for price
    TestObject priceObj = new TestObject("${name}_shopPrice")
    priceObj.addProperty('xpath', ConditionType.EQUALS, priceXpath)

    BigDecimal price = BigDecimal.ZERO
    if (WebUI.verifyElementPresent(priceObj, waitTimeout, FailureHandling.OPTIONAL)) {
        String priceText = WebUI.getText(priceObj).trim()
        price = parseCurrencyToBigDecimal(priceText)
    }

    expectedPriceByProduct[name] = price
    expectedProducts << name
    WebUI.comment("Price for ${name}: ${price}")

    // Dynamic TestObject for Add to Cart
    TestObject addObj = new TestObject("${name}_add")
    addObj.addProperty('xpath', ConditionType.EQUALS, addXpath)

    try {
        if (WebUI.verifyElementPresent(addObj, waitTimeout, FailureHandling.OPTIONAL)) {
            WebUI.scrollToElement(addObj, 2)
            WebUI.click(addObj)
            WebUI.comment("Clicked Add to cart for ${name}")
        } else {
            WebUI.comment("Add-to-cart element not found. Using JS fallback.")
            String jsClick = """
                var el = document.evaluate('(//h2[text()="${name}"]//following::a[contains(@class,"add_to_cart") or contains(text(),"Add to cart")])[1]',
                document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
                if(el){ el.click(); return true;} else return false;
            """
            WebUI.executeJavaScript(jsClick, null)
            WebUI.comment("JS fallback click executed for ${name}")
        }
    } catch (Exception e) {
        WebUI.comment("Error adding ${name} to cart: ${e.message}")
    }

    WebUI.delay(smallDelay)
}

// ---------------- STEP 3: Compute expected total ----------------
BigDecimal expectedTotal = expectedPriceByProduct.values().inject(BigDecimal.ZERO) { acc, val -> acc.add(val) }
WebUI.comment("Expected total (sum of shop prices): ${expectedTotal.toPlainString()}")

// ---------------- STEP 4: Navigate to Cart ----------------
WebUI.comment("Navigating to Cart page: ${cartUrl}")
WebUI.navigateToUrl(cartUrl)
WebUI.waitForPageLoad(waitTimeout)
WebUI.delay(smallDelay)

// ---------------- STEP 5: Read cart items ----------------
def driver = DriverFactory.getWebDriver()
List<WebElement> cartRows = driver.findElements(By.xpath("//tr[contains(@class,'cart_item')]"))

Map<String, BigDecimal> actualPriceByProduct = [:]
List<String> actualProducts = []

cartRows.each { row ->
    try {
        WebElement nameEl = row.findElement(By.xpath(".//td[contains(@class,'product-name')]//a"))
        WebElement priceEl = row.findElement(By.xpath(".//td[contains(@class,'product-subtotal')]//span"))
        String prodName = nameEl.getText().trim()
        BigDecimal rowPrice = parseCurrencyToBigDecimal(priceEl.getText().trim())
        actualProducts << prodName
        actualPriceByProduct[prodName] = rowPrice
        WebUI.comment("Cart row: ${prodName} -> ${rowPrice}")
    } catch (Exception e) {
        WebUI.comment("Skipped a cart row due to missing element: ${e.message}")
    }
}

// ---------------- STEP 6: Verify products and prices ----------------
expectedProducts.each { expectedName ->
    boolean found = actualProducts.any { it.equalsIgnoreCase(expectedName) }
    WebUI.verifyEqual(found, true, FailureHandling.CONTINUE_ON_FAILURE)
    if (found) {
        String actualKey = actualProducts.find { it.equalsIgnoreCase(expectedName) }
        BigDecimal actualPrice = actualPriceByProduct[actualKey]
        BigDecimal expectedPrice = expectedPriceByProduct[expectedName]
        WebUI.verifyEqual(actualPrice.compareTo(expectedPrice), 0, FailureHandling.CONTINUE_ON_FAILURE)
        WebUI.comment("Price check for ${expectedName}: expected ${expectedPrice}, actual ${actualPrice}")
    } else {
        WebUI.comment("❌ Expected product not found in cart: ${expectedName}")
    }
}

// ---------------- STEP 7: Verify total/subtotal ----------------
BigDecimal cartTotal = BigDecimal.ZERO
try {
    WebElement totalEl = driver.findElement(By.xpath("//span[contains(@class,'woocommerce-Price-amount')][last()]"))
    cartTotal = parseCurrencyToBigDecimal(totalEl.getText().trim())
} catch (Exception e) {
    WebUI.comment("Failed to read cart total: ${e.message}")
}

WebUI.comment("Expected total: ${expectedTotal}, Cart total: ${cartTotal}")
WebUI.verifyEqual(cartTotal.compareTo(expectedTotal), 0, FailureHandling.CONTINUE_ON_FAILURE)

if (cartTotal.compareTo(expectedTotal) == 0) {
    WebUI.comment("✅ Cart total matches expected sum.")
} else {
    WebUI.comment("❌ Cart total DOES NOT match expected sum.")
    WebUI.takeScreenshot()
}

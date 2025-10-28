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
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiHelper
import org.openqa.selenium.By as By
import org.openqa.selenium.WebElement as WebElement
import shopping.PriceUtils as PriceUtils
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper

WebUI.callTestCase(findTestCase('EmptyCart_TC'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Shop'))

def td = findTestData('Data Files/ProductsToSelect' // <-- change if your Data File path differs
    )

int rows = td.getRowNumbers()

List<Map> productsToSelect = []

for (int r = 1; r <= rows; r++) {
    String name = td.getValue('ProductName', r).trim()

    String priceCell = ''

    String sku = ''

    try {
        priceCell = td.getValue('Price', r).trim()
    }
    catch (def e) {
    } 
    
    try {
        sku = td.getValue('SKU', r).trim()
    }
    catch (def e) {
    } 
    
    BigDecimal price = priceCell ? new BigDecimal(priceCell) : null

    productsToSelect << [('name') : name, ('price') : price, ('sku') : sku]
}

println('Products requested: ' + productsToSelect)

// ---------- 6) Calculate expected total price ----------
BigDecimal expectedTotal = 0.0 // or BigDecimal expectedTotal = BigDecimal.ZERO

for (def item : productsToSelect) {
    expectedTotal = expectedTotal.add(item.price)
}

println('Expected total (from Shop): ' + expectedTotal)

// ---------- 2) Prepare selectedItems variable (Test Case variable) ----------
if (selectedItems == null) {
    selectedItems = []
} else {
    selectedItems.clear()
}

// ---------- 3) Find all product blocks on Shop page ----------
def itemBlockTO = findTestObject('Pages/Shop_page/Item_Block' // <-- ensure this TestObject exists
    )

assert itemBlockTO != null : 'TestObject not found: Pages/Shop_page/Item_Block'

List<WebElement> itemBlocks = WebUiHelper.findWebElements(itemBlockTO, 30)

assert itemBlocks.size() > 0 : 'No product blocks found on the page'

// ---------- 4) For each expected product, find and add matching block ----------
for (Map expected : productsToSelect) {
    boolean added = false

    // iterate through blocks (fresh loop each expected product)
    for (WebElement block : itemBlocks) {
        // read add button attributes if present (SKU / product_id)
        String blockSku = ''

        String blockProductId = ''

        try {
            WebElement addBtn = block.findElement(By.cssSelector('a.add_to_cart_button'))

            blockSku

            blockProductId
        }
        catch (def ignored) {
        } 
        
        // read block name
        String blockName = ''

        try {
            blockName = block.findElement(By.cssSelector('h2.woocommerce-loop-product__title')).getText().trim()
        }
        catch (def ignored) {
        } 
        
        // read block price (prefer .price ins (sale) then fallback)
        String blockPriceText = ''

        try {
            blockPriceText = block.findElement(By.cssSelector('.price ins .woocommerce-Price-amount')).getText()
        }
        catch (def e1) {
            try {
                blockPriceText = block.findElement(By.cssSelector('.price .woocommerce-Price-amount')).getText()
            }
            catch (def ignored) {
            } 
        } 
        
        BigDecimal blockPrice = PriceUtils.parsePriceString(blockPriceText)

        // Matching priority
        boolean skuMatch = (expected.sku && (expected.sku != '')) && (expected.sku == blockSku)

        boolean nameAndPriceMatch = ((expected.price != null) && (expected.name == blockName)) && (expected.price.compareTo(
            blockPrice) == 0)

        if (skuMatch || nameAndPriceMatch) {
            block.findElement(By.cssSelector('a.add_to_cart_button')).click()

            // store what we actually added
            selectedItems << [('name') : blockName, ('quantity') : 1, ('price') : blockPrice, ('sku') : blockSku, ('productId') : blockProductId]

            added = true

            break
        }
    }
    
    assert added
}

// ---------- 5) Output selectedItems for debugging ----------
println('Selected items (actual):')

selectedItems.each({ 
        println(it)
    })

// count selected items from the test variable
int selectedCount = selectedItems == null ? 0 : selectedItems.size()

println(">>> selectedItems count = $selectedCount")

WebUI.click(findTestObject('Pages/Shop_page/a_Cart'))

// Wait until at least one cart row is present
def cartRowTO = findTestObject('Pages/Cart_Page/Cart_Row')

WebUI.waitForElementPresent(cartRowTO, 30)

// Get all cart rows
List<WebElement> cartRows = WebUiCommonHelper.findWebElements(cartRowTO, 30)

// Check row count
assert cartRows.size() == selectedItems.size()

// Loop through selected items and verify
for (Map expected : selectedItems) {
    boolean found = false

    for (WebElement row : cartRows) {
        // Extract details from cart row
        String rowName = row.findElement(By.cssSelector('td.product-name a')).getText().trim()

        String priceText = row.findElement(By.cssSelector('td.product-price .woocommerce-Price-amount')).getText().replace(
            '$', '').trim()

        BigDecimal rowPrice = new BigDecimal(priceText)

        int rowQuantity = Integer.parseInt(row.findElement(By.cssSelector('td.product-quantity input')).getAttribute('value').trim())

        String rowSku = row.findElement(By.cssSelector('td.product-remove a')).getAttribute('data-product_sku')

        // Match by SKU first, then name + price
        boolean match = (expected.sku && (expected.sku == rowSku)) || ((expected.name == rowName) && (expected.price.compareTo(
            rowPrice) == 0))

        if (match) {
            assert expected.quantity == rowQuantity

            found = true

            break
        }
    }
    
    assert found
}

WebUI.comment('✅ All selected items are correctly present in the cart!')

TestObject ItemsTotal = findTestObject('Pages/Cart_Page/ItemsTotal')  
WebUI.waitForElementPresent(ItemsTotal, 30)

// 2️⃣ Extract and parse the total
String totalText = WebUI.getText(ItemsTotal).replace('$','').trim()
BigDecimal actualTotal = new BigDecimal(totalText)
println "Actual total (from Cart): " + actualTotal

// 3️⃣ Compare with expectedTotal
assert expectedTotal == actualTotal
WebUI.comment("✅ Cart total matches expected total: " + expectedTotal)


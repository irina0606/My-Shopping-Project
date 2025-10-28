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

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebElement


WebUI.callTestCase(findTestCase('Login_TC_HappyPath'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Pages/Shop_page/a_Cart'))

// Wait until at least one cart row is present
def cartRowTO = findTestObject('Pages/Cart_Page/Cart_Row')
WebUI.waitForElementPresent(cartRowTO, 30)

// Get all cart rows
List<WebElement> cartRows = WebUiCommonHelper.findWebElements(cartRowTO, 60)

// Check row count
assert cartRows.size() == selectedItems.size() : "Cart row count (${cartRows.size()}) does not match selected items (${selectedItems.size()})"

// Loop through selected items and verify
for (Map expected : selectedItems) {
	boolean found = false

	for (WebElement row : cartRows) {
		// Extract details from cart row
		String rowName = row.findElement(By.cssSelector('td.product-name a')).getText().trim()
		String priceText = row.findElement(By.cssSelector('td.product-price .woocommerce-Price-amount')).getText().replace('$','').trim()
		BigDecimal rowPrice = new BigDecimal(priceText)
		int rowQuantity = Integer.parseInt(row.findElement(By.cssSelector('td.product-quantity input')).getAttribute('value').trim())
		String rowSku = row.findElement(By.cssSelector('td.product-remove a')).getAttribute('data-product_sku')

		// Match by SKU first, then name + price
		boolean match = (expected.sku && expected.sku == rowSku) ||
						(expected.name == rowName && expected.price.compareTo(rowPrice) == 0)

		if (match) {
			assert expected.quantity == rowQuantity : "Quantity mismatch for ${expected.name}: expected ${expected.quantity}, actual ${rowQuantity}"
			found = true
			break
		}
	}

	assert found : "Item not found in cart: ${expected.name} (SKU:${expected.sku}, Price:${expected.price})"
}

WebUI.comment("✅ All selected items are correctly present in the cart!")
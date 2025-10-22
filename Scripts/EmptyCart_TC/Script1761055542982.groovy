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
import java.time.Duration as Duration

// ----------------- CONFIG -----------------
WebUI.callTestCase(findTestCase('Login_TC_HappyPath'), [:], FailureHandling.STOP_ON_FAILURE)

String cartUrl = 'https://cms.demo.katalon.com/cart/'

int maxIterations = 40

int waitForElementTimeout = 5

// ------------------------------------------
// Navigate to cart page (assumes logged-in session already exists)
try {
    WebUI.comment("Navigating to cart: $cartUrl")

    WebUI.navigateToUrl(cartUrl)

    WebUI.waitForPageLoad(10)
}
catch (Exception e) {
    WebUI.comment("Warning: could not navigate to cart page: $e.message")
} 

// TestObject for the remove link inside product-remove cell
TestObject removeLink = new TestObject('removeLink')

removeLink.addProperty('xpath', ConditionType.EQUALS, '//td[contains(concat(\' \', normalize-space(@class), \' \'), \' product-remove \')]//a | //td[contains(@class,\'product-remove\')]/a')

// TestObject for a cart row (used to wait for DOM updates)
TestObject cartRow = new TestObject('cartRow')

cartRow.addProperty('xpath', ConditionType.EQUALS, '//tr[contains(@class,\'cart_item\') or contains(@class,\'cart-row\') or //td[contains(@class,\'product-name\')]]')

// TestObject for a common modal confirm button (eg. some sites use a modal confirm)
TestObject modalConfirm = new TestObject('modalConfirm')

modalConfirm.addProperty('xpath', ConditionType.EQUALS, '//button[contains(@class,\'confirm\') or contains(@class,\'ok\') or contains(text(),\'Confirm\') or contains(text(),\'OK\') or contains(text(),\'Yes\')]')

// Removal loop — re-check each iteration to avoid stale element references
int iter = 0

try {
    while (WebUI.verifyElementPresent(removeLink, waitForElementTimeout, FailureHandling.OPTIONAL) && (iter < maxIterations)) {
        iter++

        WebUI.comment("EmptyCart: iteration $iter — attempting to remove an item")

        // Try normal Katalon click
        try {
            WebUI.click(removeLink)
        }
        catch (Exception clickEx) {
            WebUI.comment("Normal click failed: $clickEx.message — attempting JS click fallback")

            try {
                String js = '\n                    var el = document.evaluate(\n                      "(//td[contains(concat(\' \', normalize-space(@class), \' \'), \' product-remove \')]//a | //td[contains(@class,\'product-remove\')]/a)[1]",\n                      document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;\n                    if(el) el.click();\n                    return !!el;\n                '

                def clicked = WebUI.executeJavaScript(js, null)

                WebUI.comment("JS click returned: $clicked")
            }
            catch (Exception jsEx) {
                WebUI.comment("JS click failed: $jsEx.message")
            } 
        } // JS click the first matching remove link
        
        // Handle simple browser alert/confirm
        try {
            if (WebUI.verifyAlertPresent(2, FailureHandling.OPTIONAL)) {
                WebUI.acceptAlert()

                WebUI.comment('Accepted native alert/confirm.')
            }
        }
        catch (Exception aEx) {
            WebUI.comment("No native alert or failed to accept: $aEx.message")
        } 
        
        // Try clicking modal confirm button if visible
        try {
            if (WebUI.verifyElementPresent(modalConfirm, 2, FailureHandling.OPTIONAL)) {
                WebUI.click(modalConfirm)

                WebUI.comment('Clicked modal confirm button.')
            }
        }
        catch (Exception mEx) {
            WebUI.comment("Modal confirm click failed/absent: $mEx.message")
        } 
        
        // Wait for the cart row(s) to update/disappear, or small delay if not
        try {
            WebUI.waitForElementNotPresent(cartRow, 8, FailureHandling.OPTIONAL)
        }
        catch (Exception wex) {
            WebUI.delay(1)
        } 
        
        // small pause to allow DOM to stabilize
        WebUI.delay(1)
    }
}
catch (Exception outer) {
    WebUI.comment("Error while emptying cart: $outer.message")

    WebUI.takeScreenshot()
} 

// Final verification: either an empty-cart message or no remove link present
boolean emptyMessage = false

try {
    emptyMessage = WebUI.verifyTextPresent('Your cart is currently empty', false, FailureHandling.OPTIONAL)
}
catch (Exception e) {
} // ignore

if (emptyMessage) {
    WebUI.comment('✅ Cart is empty (empty message found).')
} else if (!(WebUI.verifyElementPresent(removeLink, 3, FailureHandling.OPTIONAL))) {
    WebUI.comment('✅ No remove links found — cart likely empty.')
} else {
    WebUI.comment('⚠️ Cart may still contain items. Check selector/flow. Screenshot saved.')

    WebUI.takeScreenshot()
}


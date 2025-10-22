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

WebUI.callTestCase(findTestCase('AddItemsToCart_TC'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/Pages/Cart_Page/a_Proceed to checkout'))

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_First name_'), 'katalon')

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_Last name_'), 'customer')

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_Company name(optional)'), 'KMS')

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/span_select2-billing_country-container'))

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/span_select2-billing_country-container'))

WebUI.doubleClick(findTestObject('Object Repository/Pages/Checkout_Page/span_select2-billing_country-container'))

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/span_select2-billing_country-container'))

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_combobox'), '')

WebUI.mouseOver(findTestObject('Object Repository/Pages/Checkout_Page/li_select2-billing_country-result-nobh-VN'))

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/li_select2-billing_country-result-nobh-VN'))

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_House number and street name'), '119 Nguyen Thi Thap')

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/input_Postcode _ ZIP(optional)'))

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_Postcode _ ZIP(optional)'), '70000')

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_Town _ City_'), 'HCM')

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_Phone_'), '0359912894')

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/input_Email address_'), 'katalon@example.com')

WebUI.setText(findTestObject('Object Repository/Pages/Checkout_Page/textarea_Notes about your order, e.g. special no'), 
    'present')

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/th_Total'))

WebUI.verifyElementText(findTestObject('Object Repository/Pages/Checkout_Page/th_Total'), 'TOTAL')

WebUI.click(findTestObject('Object Repository/Pages/Checkout_Page/button_place_order'))

WebUI.delay(60, FailureHandling.STOP_ON_FAILURE)

WebUI.verifyElementText(findTestObject('Object Repository/Pages/Checkout_Page/p_Thank you. Your order has been received'), 
    'Thank you. Your order has been received.')

WebUI.click(findTestObject('Pages/Shop_page/a_Cart'))

WebUI.verifyElementText(findTestObject('Pages/Cart_Page/p_Your cart is currently empty'), 'Your cart is currently empty.')

WebUI.closeBrowser()


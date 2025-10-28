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

WebUI.callTestCase(findTestCase('EmptyCart_TC'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Shop'))

WebUI.verifyElementText(findTestObject('Pages/Shop_page/h2_Flying Ninja'), 'Flying Ninja')

WebUI.verifyElementText(findTestObject('Object Repository/Pages/Shop_page/span_12.00'), '$12.00')

WebUI.mouseOver(findTestObject('Object Repository/Pages/Shop_page/li_Sale'))

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Add Flying Ninja to your cart'))

WebUI.verifyElementText(findTestObject('Pages/Shop_page/h2_Happy Ninja'), 'Happy Ninja')

WebUI.verifyElementText(findTestObject('Object Repository/Pages/Shop_page/span_18.00'), '$18.00')

WebUI.mouseOver(findTestObject('Object Repository/Pages/Shop_page/li_Add to cart'))

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Add Happy Ninja to your cart'))

WebUI.verifyElementText(findTestObject('Pages/Shop_page/h2_Happy Ninja_1'), 'Happy Ninja')

WebUI.verifyElementText(findTestObject('Object Repository/Pages/Shop_page/span_35.00'), '$35.00')

WebUI.mouseOver(findTestObject('Object Repository/Pages/Shop_page/li_Add to cart_1'))

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Add Happy Ninja to your cart_1'))

WebUI.click(findTestObject('Object Repository/Pages/Shop_page/a_Cart'))

WebUI.verifyMatch('$12.00', '$12.00', false)

WebUI.verifyMatch('$18.00', '$18.00', false)

WebUI.verifyMatch('$35.00', '$35.00', false)

WebUI.rightClick(findTestObject('Object Repository/Pages/Cart_Page/th_Subtotal'))

WebUI.verifyElementText(findTestObject('Pages/Cart_Page/ItemsTotal'), '$65.00')


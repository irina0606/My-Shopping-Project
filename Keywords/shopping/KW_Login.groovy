package shopping

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

public class KW_Login {
	@Keyword
	def static void loginIntoApplication() {

		WebUI.click(findTestObject('Pages/Home_Page/a_My account'))

		WebUI.setText(findTestObject('Object Repository/Pages/Login_Page/input_Username or email address_'), GlobalVariable.username)

		WebUI.setText(findTestObject('Object Repository/Pages/Login_Page/input_Password_'), GlobalVariable.password)

		WebUI.click(findTestObject('Object Repository/Pages/Login_Page/button_login'))
	}


	@Keyword
	def static void loginIntoApplication_EmptyPW() {

		WebUI.click(findTestObject('Pages/Home_Page/a_My account'))

		WebUI.setText(findTestObject('Object Repository/Pages/Login_Page/input_Username or email address_'), GlobalVariable.username)

		WebUI.click(findTestObject('Object Repository/Pages/Login_Page/button_login'))
	}

	@Keyword
	def static void loginIntoApplication_EmptyUsername() {

		WebUI.click(findTestObject('Pages/Home_Page/a_My account'))

		WebUI.setText(findTestObject('Object Repository/Pages/Login_Page/input_Password_'), GlobalVariable.password)

		WebUI.click(findTestObject('Object Repository/Pages/Login_Page/button_login'))
	}
}

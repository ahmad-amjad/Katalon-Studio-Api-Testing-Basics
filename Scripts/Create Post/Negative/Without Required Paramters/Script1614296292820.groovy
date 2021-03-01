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
import com.kms.katalon.core.testobject.TestObject as TestObject
import groovy.json.JsonSlurper as JsonSlurper
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import groovy.json.JsonOutput as JsonOutput
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent as HttpTextBodyContent

CustomKeywords.'helpers.Utils.setDateVariables'()

jsonSlurper = new JsonSlurper()

newPostTitle = (((newPostTitle + GlobalVariable.todaysDate) + ' ') + GlobalVariable.nowTime)

newPostContent = (((newPostContent + GlobalVariable.todaysDate) + ' ') + GlobalVariable.nowTime)

userId = 1

RequestObject request = findTestObject('Object Repository/Create Post')

String body = ('{"body":"' + newPostContent + '"}')

request.setBodyContent(new HttpTextBodyContent(body))

ResponseObject response = WS.sendRequest(request)

WS.verifyResponseStatusCode(response, 400, FailureHandling.STOP_ON_FAILURE)

println(JsonOutput.prettyPrint(response.responseBodyContent))
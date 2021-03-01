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
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import groovy.json.JsonOutput as JsonOutput
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.testobject.RestRequestObjectBuilder as RestRequestObjectBuilder
import com.kms.katalon.core.testobject.TestObjectProperty as TestObjectProperty
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent as HttpTextBodyContent
import internal.GlobalVariable as GlobalVariable

CustomKeywords.'helpers.Utils.setDateVariables'()

newPostTitle = (((newPostTitle + GlobalVariable.todaysDate) + ' ') + GlobalVariable.nowTime)

newPostContent = (((newPostContent + GlobalVariable.todaysDate) + ' ') + GlobalVariable.nowTime)

userId = 1

jsonSlurper = new JsonSlurper()

ResponseObject response = WS.sendRequest(findTestObject('Create Post', [('BaseUrl') : GlobalVariable.BaseUrl, ('NewPostTitle') : newPostTitle, ('NewPostContent') : newPostContent
	, ('UserId') : userId]))

WS.verifyResponseStatusCode(response, 201, FailureHandling.STOP_ON_FAILURE)

println(JsonOutput.prettyPrint(response.responseBodyContent))

Map responseBody = jsonSlurper.parseText(response.responseBodyContent)

WS.verifyElementPropertyValue(response, 'title', newPostTitle)

WS.verifyElementPropertyValue(response, 'body', newPostContent)

WS.verifyElementPropertyValue(response, 'userId', userId)

postId = responseBody.id

println(postId)

CustomKeywords.'helpers.Utils.setDateVariables'()

TestObjectProperty contentTypeHeader = new TestObjectProperty("Content-Type", ConditionType.EQUALS, "application/json")

TestObjectProperty acceptHeader = new TestObjectProperty("Accept", ConditionType.EQUALS, "application/json")

ArrayList overriddenHeaders = Arrays.asList(contentTypeHeader, acceptHeader)

updatedPostTitle = (((updatedPostTitle + GlobalVariable.todaysDate) + ' ') + GlobalVariable.nowTime)

updatedPostContent = (((updatedPostContent + GlobalVariable.todaysDate) + ' ') + GlobalVariable.nowTime)

String url = GlobalVariable.BaseUrl + "/posts/" + postId

String body = ('{"title":"' + updatedPostTitle + '", "body":"' + updatedPostContent + '", "userId": '+ userId +'}')

RequestObject request = findTestObject('Object Repository/Update Post')

request.setRestUrl(url)

request.setBodyContent(new HttpTextBodyContent(body))

response = WS.sendRequest(request)

WS.verifyResponseStatusCode(response, 401, FailureHandling.STOP_ON_FAILURE)
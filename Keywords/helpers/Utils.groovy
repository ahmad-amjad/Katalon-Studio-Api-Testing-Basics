package helpers
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import internal.GlobalVariable

import MobileBuiltInKeywords as Mobile
import WSBuiltInKeywords as WS
import WebUiBuiltInKeywords as WebUI
import groovy.time.TimeCategory

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException


class Utils {
	/**
	 * Send request and verify status code
	 * @param request request object, must be an instance of RequestObject
	 * @param expectedStatusCode
	 * @return a boolean to indicate whether the response status code equals the expected one
	 */
	@Keyword
	def verifyStatusCode(TestObject request, int expectedStatusCode) {
		if (request instanceof RequestObject) {
			RequestObject requestObject = (RequestObject) request
			ResponseObject response = WSBuiltInKeywords.sendRequest(requestObject)
			if (response.getStatusCode() == expectedStatusCode) {
				KeywordUtil.markPassed("Response status codes match")
			} else {
				KeywordUtil.markFailed("Response status code not match. Expected: " +
						expectedStatusCode + " - Actual: " + response.getStatusCode() )
			}
		} else {
			KeywordUtil.markFailed(request.getObjectId() + " is not a RequestObject")
		}
	}

	/**
	 * Add Header basic authorization field,
	 * this field value is Base64 encoded token from user name and password
	 * @param request object, must be an instance of RequestObject
	 * @param username username
	 * @param password password
	 * @return the original request object with basic authorization header field added
	 */
	@Keyword
	def addBasicAuthorizationProperty(TestObject request, String username, String password) {
		if (request instanceof RequestObject) {
			String authorizationValue = username + ":" + password
			authorizationValue = "Basic " + authorizationValue.bytes.encodeBase64().toString()

			// Find available basic authorization field and change its value to the new one, if any
			List<TestObjectProperty> headerProperties = request.getHttpHeaderProperties()
			boolean fieldExist = false
			for (int i = 0; i < headerProperties.size(); i++) {
				TestObjectProperty headerField = headerProperties.get(i)
				if (headerField.getName().equals('Authorization')) {
					KeywordUtil.logInfo("Found existent basic authorization field. Replacing its value.")
					headerField.setValue(authorizationValue)
					fieldExist = true
					break
				}
			}

			if (!fieldExist) {
				TestObjectProperty authorizationProperty = new TestObjectProperty("Authorization",
						ConditionType.EQUALS, authorizationValue, true)
				headerProperties.add(authorizationProperty)
			}
			KeywordUtil.markPassed("Basic authorization field has been added to request header")
		} else {
			KeywordUtil.markFailed(request.getObjectId() + "is not a RequestObject")
		}
		return request
	}

	@Keyword
	def setDateVariables(){
		Date today = new Date()
		String nowTime = today.format('hhmmss')
		String todaysDate = today.format('MM/dd/yy')
		println (nowTime + " " + todaysDate)
		GlobalVariable.nowTime = nowTime
		GlobalVariable.todaysDate = todaysDate
		use(TimeCategory, {
			String oneYearAgo = 1.year.ago.format('MM/dd/yy')
			GlobalVariable.oneYearAgo = oneYearAgo
		})
		println(GlobalVariable.nowTime)
		println(GlobalVariable.todaysDate)
		println(GlobalVariable.oneYearAgo)
	}
	
	@Keyword
	def removeContentsFromRequestBody(Object requestBody, List contentsToBeRemoved, Map contentLocatorMap){
		contentsToBeRemoved.each{
			if(!it.containsKey('keyName')){
				throw new Exception("Key name for the content to be removed was not found.")
			}
			if(!(it.keyName instanceof String)){
				throw new Exception("Key name for the content to be removed was not a string.")
			}
			if(it.keyName == ''){
				throw new Exception("Key name for the content to be removed cannot be an empty string.")
			}
			if(!contentLocatorMap.containsKey(it.keyName)){
				throw new Exception("Content locator map for the key '$it.keyName' was not found. Please double check the content's key name with the content locator map.")
			}
			if(!isContentLocatorFormatValid(contentLocatorMap[it.keyName])){
				throw new Exception("Invalid type for content locator element. It should either be a non-empty 'String' or a positive 'Integer'.")
			}
			if(it.containsKey('arrayIndex')){
				if(!(it.arrayIndex instanceof Integer)){
					throw new Exception("Array index for the content to be removed was not an integer.")
				}
				if(it.arrayIndex < 0){
					throw new Exception("Array index for the content to be removed cannot be less than 0. Please double check the content's array index.")
				}
			}
			ArrayList contentLocator = new ArrayList(contentLocatorMap[it.keyName])
			requestBody = removeContentFromRequestBody(requestBody, it, contentLocator)
		}
		return requestBody
	}

	def removeContentFromRequestBody(Object requestBody, Map contentDetails, List contentLocator){
		if(requestBody instanceof Map){
			if(contentLocator == []){
				if(contentDetails.containsKey('arrayIndex')){
					throw new Exception("Object was a JSON Object rather than a JSON Array. Please double check the content's locator with the request's body.")
				}
				else{
					requestBody.remove(contentDetails.keyName)
					return requestBody
				}
			}
			else{
				if(contentLocator[0] instanceof String){
					String parentKey = contentLocator[0]
					if(!requestBody.containsKey(parentKey)){
						throw new Exception("The key '$parentKey' was not found the in request body at the specified path. Please double check the content's locator with the request's body.")
					}
					else{
						contentLocator.remove(parentKey)
						requestBody."$parentKey" = removeContentFromRequestBody(requestBody."$parentKey", contentDetails, contentLocator)
						return requestBody
					}
				}
				else{
					throw new Exception("Object was a JSON Object rather than a JSON Array. Please double check the content's locator with the request's body.")
				}
			}
		}
		else if(requestBody instanceof List){
			if(contentLocator == []){
				if(!contentDetails.containsKey('arrayIndex')){
					throw new Exception("Array index for the content to be removed was not found. Did you expect a JSON Object rather than a JSON Array? If so then please double check the content's locator with the request's body.")
				}
				else if(contentDetails.arrayIndex >= requestBody.size()){
					throw new Exception("Content to be removed was not found in the array at index '$contentDetails.arrayIndex'. Please double check the content's array index with the request's body.")
				}
				else{
					requestBody.remove(contentDetails.arrayIndex)
					return requestBody
				}
			}
			else{
				if(contentLocator[0] instanceof Integer){
					Integer parentArrayIndex = contentLocator[0]
					if(parentArrayIndex >= requestBody.size()){
						throw new Exception("The parent array index '$parentArrayIndex' was not found in the request body at the specified path. Please double check the content's locator with the request's body.")
					}
					else{
						contentLocator.remove(parentArrayIndex)
						requestBody[parentArrayIndex] = removeContentFromRequestBody(requestBody[parentArrayIndex], contentDetails, contentLocator)
						return requestBody
					}
				}
				else{
					throw new Exception("Object was a JSON Object rather than a JSON Array. Please double check the content's locator with the request's body.")
				}
			}
		}
		else{
			throw new Exception("The 'requestBody' argument is neither a JSON object nor a JSON Array. Please double check the content's locator with the request's body.")
		}
	}

	@Keyword
	def addContentsToRequestBody(Object requestBody, List contentsToBeAdded, Map contentLocatorMap){
		contentsToBeAdded.each{
			if(!it.containsKey('keyName')){
				throw new Exception("Key name for the content to be added was not found.")
			}
			if(!(it.keyName instanceof String)){
				throw new Exception("Key name for the content to be added was not a string.")
			}
			if(it.keyName == ''){
				throw new Exception("Key name for the content to be added cannot be an empty string.")
			}
			if(!contentLocatorMap.containsKey(it.keyName)){
				throw new Exception("Content locator map for the key '$it.keyName' was not found. Please double check the content's key name with the content locator map.")
			}
			if(!isContentLocatorFormatValid(contentLocatorMap[it.keyName])){
				throw new Exception("Invalid type for content locator element. It should either be a non-empty 'String' or a positive 'Integer'.")
			}
			if(it.containsKey('arrayIndex')){
				if(!(it.arrayIndex instanceof Integer)){
					throw new Exception("Array index for the content to be added was not an integer.")
				}
				if(it.arrayIndex < 0){
					throw new Exception("Array index for the content to be added cannot be less than 0. Please double check the content's array index.")
				}
			}
			if(!it.containsKey('Content')){
				throw new Exception("Content to be added for the key '$it.keyName' was not found.")
			}
			ArrayList contentLocator = new ArrayList(contentLocatorMap[it.keyName])
			requestBody = addOrReplaceContentInRequestBody(requestBody, it, contentLocator)
		}
		return requestBody
	}

	def addOrReplaceContentInRequestBody(Object requestBody, Map contentDetails, List contentLocator){
		if(requestBody instanceof Map){
			if(contentLocator == []){
				if(contentDetails.containsKey('arrayIndex')){
					throw new Exception("Object was a JSON Object rather than a JSON Array. Please double check the content's locator with the request's body.")
				}
				else{
					requestBody."$contentDetails.keyName" = contentDetails.Content
					return requestBody
				}
			}
			else{
				if(contentLocator[0] instanceof String){
					String parentKey = contentLocator[0]
					if(!requestBody.containsKey(parentKey)){
						throw new Exception("The key '$parentKey' was not found the in request body at the specified path. Please double check the content's locator with the request's body.")
					}
					else{
						contentLocator.remove(parentKey)
						requestBody."$parentKey" = addOrReplaceContentInRequestBody(requestBody."$parentKey", contentDetails, contentLocator)
						return requestBody
					}
				}
				else{
					throw new Exception("Object was a JSON Object rather than a JSON Array. Please double check the content's locator with the request's body.")
				}
			}
		}
		else if(requestBody instanceof List){
			if(contentLocator == []){
				if(!contentDetails.containsKey('arrayIndex')){
					throw new Exception("Array index for the content to be added was not found. Did you expect a JSON Object rather than a JSON Array? If so then please double check the content's locator with the request's body.")
				}
				else if(contentDetails.arrayIndex > requestBody.size()){
					throw new Exception("A new array item can only be inserted at the index following the array's last item. Please double check the content's array index with the request's body.")
				}
				else{
					requestBody[contentDetails.arrayIndex] = contentDetails.Content
					return requestBody
				}
			}
			else{
				if(contentLocator[0] instanceof Integer){
					Integer parentArrayIndex = contentLocator[0]
					if(parentArrayIndex >= requestBody.size()){
						throw new Exception("The parent array index '$parentArrayIndex' was not found in the request body at the specified path. Please double check the content's locator with the request's body.")
					}
					else{
						contentLocator.remove(parentArrayIndex)
						requestBody[parentArrayIndex] = addOrReplaceContentInRequestBody(requestBody[parentArrayIndex], contentDetails, contentLocator)
						return requestBody
					}
				}
				else{
					throw new Exception("Object was a JSON Object rather than a JSON Array. Please double check the content's locator with the request's body.")
				}
			}
		}
		else{
			throw new Exception("The 'requestBody' argument is neither a JSON object nor a JSON Array. Please double check the content's locator with the request's body.")
		}
	}

	def isContentLocatorFormatValid(List contentLocator){
		try{
			contentLocator.each{
				if(it instanceof Integer){
					if(it < 0){
						throw new Exception("Content locator element cannot be a negative integer.")
					}
				}
				else if((it instanceof String)){
					if(it == ''){
						throw new Exception("Content locator element cannot be an empty string.")
					}
				}
				else{
					throw new Exception("Content locator element should either be a non-empty 'String' or a positive 'Integer'.")
				}
			}
		}
		catch(Exception){
			return false
		}
		return true
	}
}

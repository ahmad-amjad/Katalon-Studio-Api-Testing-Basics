<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update Post</name>
   <tag></tag>
   <elementGuidId>b7d49def-2789-448d-9b56-eac1f6af6cbc</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n  \&quot;title\&quot;: \&quot;${NewPostTitle}\&quot;,\n  \&quot;body\&quot;: \&quot;${NewPostContent}\&quot;,\n  \&quot;userId\&quot;: ${UserId}\n}&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Basic S2F0YWxvblVzZXI6SWxrdEAjMjM=</value>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
   </httpHeaderProperties>
   <katalonVersion>7.7.2</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>PUT</restRequestMethod>
   <restUrl>${BaseUrl}/posts/${PostId}</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.BaseUrl</defaultValue>
      <description></description>
      <id>4d25fd10-462b-4bcb-b96a-ca012d017921</id>
      <masked>false</masked>
      <name>BaseUrl</name>
   </variables>
   <variables>
      <defaultValue>1</defaultValue>
      <description></description>
      <id>8297c1f6-5a87-4c20-a1b1-622ae059bc57</id>
      <masked>false</masked>
      <name>PostId</name>
   </variables>
   <variables>
      <defaultValue>'Default Post Title'</defaultValue>
      <description></description>
      <id>5c174fbc-12ec-46f2-8036-3ef37b4c86b2</id>
      <masked>false</masked>
      <name>NewPostTitle</name>
   </variables>
   <variables>
      <defaultValue>'Default Post Content'</defaultValue>
      <description></description>
      <id>49f84127-2410-41c2-bb40-e281afc2944f</id>
      <masked>false</masked>
      <name>NewPostContent</name>
   </variables>
   <variables>
      <defaultValue>1</defaultValue>
      <description></description>
      <id>dcef8e4a-b4d9-49cb-88bf-5b359e74ad65</id>
      <masked>false</masked>
      <name>UserId</name>
   </variables>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>

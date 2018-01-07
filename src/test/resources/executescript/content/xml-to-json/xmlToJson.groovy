/*
 * Copyright 2016 BatchIQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/******************************************************************************
 * Parsing URL components into FlowFile attributes
 *
 * Variables provided in scope by script engine:
 *
 *     session - ProcessSession
 *     context - ProcessContext
 *     log - ComponentLog
 *     REL_SUCCESS - Relationship
 *     REL_FAILURE - Relationship
 *
 */

import groovy.json.JsonOutput
import groovy.util.slurpersupport.GPathResult
import org.apache.nifi.flowfile.FlowFile

import java.nio.charset.StandardCharsets
import org.apache.nifi.processor.io.StreamCallback
import org.xml.sax.SAXParseException

def binding = getBinding()
def optionPrettyPrint = binding.hasVariable("prettyPrintJson") ? prettyPrintJson.asBoolean() : false
def optionBatchSize = binding.hasVariable("batchSize") ? batchSize.asInteger() : 10

def flowFiles = session.get(optionBatchSize)
flowFiles.each { flowFile ->
    processFlowFile(flowFile, optionPrettyPrint)
}


void processFlowFile(FlowFile flowFile, boolean prettyPrint) {
    def errorMessage = null
    def outputRelation = REL_SUCCESS
    flowFile = session.write(flowFile, {inputStream, outputStream ->
        try {
            // Parse XML
            def xmlSlurper = new XmlSlurper()
            def inputXmlDoc = xmlSlurper.parse(inputStream)

            // Transform content
            def outputObj = [:]
            outputObj[inputXmlDoc.name()] = xmlNodeToObject(inputXmlDoc)

            // Write output content
            def json = JsonOutput.toJson(outputObj)
            if (prettyPrint) {
                json = JsonOutput.prettyPrint(json)
            }
            outputStream.write(json.getBytes(StandardCharsets.UTF_8))
        } catch (SAXParseException ex) {
            errorMessage = ex.getClass().getName() + ": " + ex.getMessage()
            outputRelation = REL_FAILURE
        }
    } as StreamCallback)

    if (errorMessage != null) {
        flowFile = session.putAttribute(flowFile, "xmlToJson.error", errorMessage)
    }
    session.transfer(flowFile, outputRelation);
}


Object xmlNodeToObject(GPathResult node) {
    def nodeName = node.name()
    def attributes = node.attributes()
    def childNodes = node.children()
    def childNodeNames = new HashSet<String>()
    childNodes.each{ childNode -> 
        def childNodeName = childNode.name()
        childNodeNames.add(childNodeName)
    }
    def childTextNodes = node.localText()

    if (childTextNodes.size() > 0) {
        def nodeText = node.text()
        return nodeText
    } else if (childNodes.size() > 1 && attributes.size() == 0 && childNodeNames.size() == 1) {
        def resultList = []
        childNodes.each { childNode ->
            def childValue = xmlNodeToObject(childNode)
            resultList.add(childValue)
        }
        return resultList
    } else if ((childNodes.size() + attributes.size()) > 0) {
        def resultMap = [:]
        attributes.each { attributeName, attributeValue ->
            resultMap[attributeName] = attributeValue
        }
        childNodes.each { childNode ->
            def childName = childNode.name()
            def childValue = xmlNodeToObject(childNode)
            resultMap[childName] = childValue
        }
        return resultMap
    } else {
        log.warn("Unexpected node structure for node " + nodeName)
    }
}

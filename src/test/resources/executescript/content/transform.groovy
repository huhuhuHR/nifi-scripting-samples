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
import groovy.json.JsonSlurper
import org.apache.nifi.processor.io.StreamCallback
import java.nio.charset.StandardCharsets

def flowFile = session.get()
if (flowFile == null) {
    return
}

try {
    flowFile = session.write(flowFile, {inputStream, outputStream ->
        // Read input FlowFile content
        def jsonSlurper = new JsonSlurper()
        def inputObj = jsonSlurper.parse(inputStream)

        // Transform content
        def outputObj = inputObj
        outputObj.value = outputObj.value * outputObj.value
        outputObj.message = "Hello"

        // Write output content
        def json = JsonOutput.toJson(outputObj)
        outputStream.write(json.getBytes(StandardCharsets.UTF_8))
    } as StreamCallback)

    // Finish by transferring the FlowFile to an output relationship
    session.transfer(flowFile, REL_SUCCESS);
} catch (MalformedURLException ex) {
    flowFile = session.putAttribute(flowFile, "parse_url.error", ex.getMessage())
    session.transfer(flowFile, REL_FAILURE)
}

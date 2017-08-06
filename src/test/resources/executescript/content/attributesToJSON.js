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
 * Setting flowfile attributes in ExecuteScript
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


flowFile = session.get();
if (flowFile != null) {
    var OutputStreamCallback = Java.type("org.apache.nifi.processor.io.OutputStreamCallback");
    var StandardCharsets = Java.type("java.nio.charset.StandardCharsets");

    // Get attributes
    var customer = JSON.parse(flowFile.getAttribute("customer"));
    var product = JSON.parse(flowFile.getAttribute("product"));
    var payment = JSON.parse(flowFile.getAttribute("payment"));

    // Combine
    var merged = {
        "customer": customer,
        "product": product,
        "payment": payment
    };

    // Write output content
    flowFile = session.write(flowFile, new OutputStreamCallback(function(outputStream) {
        outputStream.write(JSON.stringify(merged, null, "  ").getBytes(StandardCharsets.UTF_8));
    }));

    session.transfer(flowFile, REL_SUCCESS);
}

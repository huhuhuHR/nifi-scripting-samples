/*
 * Copyright 2018 BatchIQ
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
 * Format date/time using Java SimpleDateFormat (for Java 8)
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
var flowFile = session.get();

if (flowFile !== null) {

    var StreamCallback = Java.type("org.apache.nifi.processor.io.StreamCallback");
    var IOUtils = Java.type("org.apache.commons.io.IOUtils");
    var StandardCharsets = Java.type("java.nio.charset.StandardCharsets");
    var SimpleDateFormat = Java.type("java.text.SimpleDateFormat")
    var TimeZone = Java.type("java.util.TimeZone")

    flowFile = session.write(flowFile, new StreamCallback(function(inputStream, outputStream) {

            var inputDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            var utcZone = TimeZone.getTimeZone("UTC")
            inputDateFormat.setTimeZone(utcZone)

            var outputDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            var outputZone = TimeZone.getTimeZone("America/New_York")
            outputDateFormat.setTimeZone(outputZone)

            var inputDateTime = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
            var utcDate = inputDateFormat.parse(inputDateTime)
            var outputDateTime = outputDateFormat.format(utcDate)
            IOUtils.write(outputDateTime, outputStream, StandardCharsets.UTF_8)
    }));

    session.transfer(flowFile, REL_SUCCESS);
}

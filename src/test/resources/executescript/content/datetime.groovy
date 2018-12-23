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
 * Working with date/times
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

import org.apache.commons.io.IOUtils
import org.apache.nifi.processor.io.StreamCallback

import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat

def flowFile = session.get()
if (flowFile == null) {
    return
}

try {
    flowFile = session.write(flowFile, {inputStream, outputStream ->

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        TimeZone utcZone = TimeZone.getTimeZone("UTC")
        inputDateFormat.setTimeZone(utcZone)

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        TimeZone outputZone = TimeZone.getTimeZone("America/New_York")
        outputDateFormat.setTimeZone(outputZone)

        String inputDateTime = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
        Date utcDate = inputDateFormat.parse(inputDateTime)
        String outputDateTime = outputDateFormat.format(utcDate)
        IOUtils.write(outputDateTime, outputStream, StandardCharsets.UTF_8)

    } as StreamCallback)

    session.transfer(flowFile, REL_SUCCESS)
} catch (Exception ex) {
    flowFile = session.putAttribute(flowFile, "datetime.error", ex.getMessage())
    session.transfer(flowFile, REL_FAILURE)
}

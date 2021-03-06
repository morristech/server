/*
 * Copyright 2017 Pronghorn Technology LLC
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

package tech.pronghorn.http.protocol

import tech.pronghorn.util.finder.*
import java.nio.ByteBuffer

public interface HttpVersion {
    public val majorVersion: Int
    public val minorVersion: Int
}

public class InstanceHttpVersion(override val majorVersion: Int,
                                 override val minorVersion: Int) : HttpVersion {
    companion object {
        public fun parse(buffer: ByteBuffer,
                         offset: Int,
                         length: Int): InstanceHttpVersion? {
            var majorVersion = 0
            var minorVersion = 0
            var afterColon = false
            var read = 0
            buffer.position(offset)
            while (read < length) {
                if (!buffer.hasRemaining()) {
                    return null
                }

                val byte = buffer.get()
                if (byte == colonByte) {
                    if (majorVersion == 0) {
                        return null
                    }
                    afterColon = true
                }
                else if (byte < 48 || byte > 57) {
                    return null
                }
                else if (!afterColon) {
                    majorVersion = (majorVersion * 10) + (byte - 48)
                }
                else {
                    minorVersion = (minorVersion * 10) + (byte - 48)
                }
                read += 1
            }

            return InstanceHttpVersion(majorVersion, minorVersion)
        }
    }
}

public enum class SupportedHttpVersions(versionName: String,
                                        override val majorVersion: Int,
                                        override val minorVersion: Int) : ByteBacked, HttpVersion {
    HTTP11("HTTP/1.1", 1, 1),
    HTTP10("HTTP/1.0", 1, 0);

    override val bytes: ByteArray = versionName.toByteArray(Charsets.US_ASCII)

    companion object : ByteBackedFinder<HttpVersion> by httpVersionFinder
}

private val httpVersionFinder = FinderGenerator.generateFinder(SupportedHttpVersions.values())

/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Convenience class for accessing the content of a request.
 */
public class RequestContent {
    public String contentType;

    public Integer contentLength = null;

    private final RoutesConfiguration configuration;

    private final HttpServletRequest request;

    RequestContent(RoutesConfiguration configuration, HttpServletRequest request) {
        this.configuration = configuration;
        this.request = request;
        contentType = request.getHeader("Content-Type");

        try {
            contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        } catch (Exception e) {}
    }

    public InputStream getRequestStream() throws IOException {
        return request.getInputStream();
    }

    public byte[] getRequestBytes() throws IOException {
        InputStream inputStream = getRequestStream();
        if (inputStream == null) {
            return null;
        } else {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[configuration.streamBufferSize];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                bytes.write(buffer, 0, read);
            }

            return bytes.size() == 0 ? null : bytes.toByteArray();
        }
    }

    public String getRequestText() throws IOException {
        return new String(getRequestBytes());
    }
}

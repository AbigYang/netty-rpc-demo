/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yang.vo;

import java.nio.charset.StandardCharsets;

public class ResponseHeader extends Header {
    private int code;
    private String error;

    public ResponseHeader(Header header) {
        this(header.getType(), header.getVersion(), header.getRequestId());
    }

    public ResponseHeader(Header header, int code, String error) {
        this(header.getType(), header.getVersion(), header.getRequestId(), code, error);
    }

    public ResponseHeader(int type, int version, String requestId, Throwable throwable) {
        this(type, version, requestId, Code.UNKNOWN_ERROR.getCode(), throwable.getMessage());
    }

    public ResponseHeader(int type, int version, String requestId) {
        this(type, version, requestId, Code.SUCCESS.getCode(), null);
    }

    public ResponseHeader(int type, int version, String requestId, int code, String error) {
        super(type, version, requestId);
        this.code = code;
        this.error = error;
    }


    @Override
    public int length() {
        return Integer.BYTES + Integer.BYTES +
                Integer.BYTES + super.getRequestId().getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES +
                Integer.BYTES +
                (error == null ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}

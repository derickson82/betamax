/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.betamax.util.message;

import software.betamax.message.AbstractMessage;
import com.google.common.base.Joiner;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicMessage extends AbstractMessage {

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers.clear();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            this.headers.putAll(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, String> getHeaders() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (String name : headers.keySet()) {
            map.put(name, Joiner.on(", ").join(headers.get(name)));
        }

        return map;
    }

    public final boolean hasBody() {
        return body != null && body.length > 0;
    }

    @Override
    protected InputStream getBodyAsStream() {
        return new ByteArrayInputStream(body);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    private Multimap<String, String> headers = LinkedHashMultimap.create();
    private byte[] body = new byte[0];
}

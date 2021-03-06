/*
 * Copyright 2012 the original author or authors.
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

package software.betamax.compatibility

import software.betamax.junit.*
import software.betamax.util.server.*
import software.betamax.junit.Betamax
import software.betamax.junit.RecorderRule
import software.betamax.util.server.HelloHandler
import software.betamax.util.server.SimpleSecureServer
import com.google.common.io.Files
import org.junit.ClassRule
import software.betamax.ProxyConfiguration
import software.betamax.util.server.SimpleServer
import spock.lang.*
import wslite.rest.RESTClient

import static software.betamax.Headers.X_BETAMAX
import static software.betamax.TapeMode.READ_WRITE
import static HelloHandler.HELLO_WORLD
import static java.net.HttpURLConnection.HTTP_OK
import static com.google.common.net.HttpHeaders.VIA

@Betamax(mode = READ_WRITE)
@Timeout(10)
@Unroll
class WsLiteSpec extends Specification {

    @Shared @AutoCleanup("deleteDir") def tapeRoot = Files.createTempDir()
    @Shared def configuration = ProxyConfiguration.builder().sslEnabled(true).tapeRoot(tapeRoot).build()
    @Shared @ClassRule RecorderRule recorder = new RecorderRule(configuration)

    @Shared @AutoCleanup("stop") def httpEndpoint = new SimpleServer(HelloHandler)
    @Shared @AutoCleanup("stop") def httpsEndpoint = new SimpleSecureServer(5001, HelloHandler)

    void setupSpec() {
        httpEndpoint.start()
        httpsEndpoint.start()
    }

    void "can record a #scheme connection made with WsLite"() {
        given: "a properly configured wslite instance"
        def http = new RESTClient(url)

        when: "a request is made"
        def response = http.get(path: "/")

        then: "the request is intercepted"
        response.statusCode == HTTP_OK
        response.headers[VIA] == "Betamax"
        response.headers[X_BETAMAX] == "REC"
        response.contentAsString == HELLO_WORLD

        where:
        url << [httpEndpoint.url, httpsEndpoint.url]
        scheme = url.toURI().scheme
    }

}

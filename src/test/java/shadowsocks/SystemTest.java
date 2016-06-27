/*
 *   Copyright 2016 Author:NU11 bestoapache@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package shadowsocks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import shadowsocks.util.GlobalConfig;
import shadowsocks.Shadowsocks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.net.Proxy.Type;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class SystemTest{

    public static Logger log = LogManager.getLogger(SystemTest.class.getName());

    @Before
    public void setUp(){
        log.info("Set up");
        GlobalConfig.get().setPassowrd("testkey");
        GlobalConfig.get().setMethod("aes-128-cfb");
        GlobalConfig.get().setServer("127.0.0.1");
        GlobalConfig.get().setPort(1024);
        GlobalConfig.get().setLocalPort(2048);
        GlobalConfig.get().setOTAEnabled(true);
        GlobalConfig.get().setTimeout(100);
    }
    @After
    public void tearDown(){
        log.info("Tear down");
    }
    @Test
    public void testStartStop() {

        Shadowsocks server = new Shadowsocks(true);
        Shadowsocks local = new Shadowsocks(false);
        //Can't shutdown before boot.
        assertFalse(server.shutdown());
        //Boot and shutdown
        assertTrue(server.boot());
        assertTrue(local.boot());
        assertTrue(server.shutdown());
        assertTrue(local.shutdown());
        //Boot again
        assertTrue(server.boot());
        //Two instances is not allowed.
        assertFalse(server.boot());
        assertTrue(server.shutdown());

    }

    private void testSimpleHttp(boolean ota) {

        GlobalConfig.get().setOTAEnabled(ota);

        Shadowsocks server = new Shadowsocks(true);
        Shadowsocks local = new Shadowsocks(false);

        assertTrue(server.boot());
        assertTrue(local.boot());
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 2048));
        HttpURLConnection conn = null;
        try{
            URL url = new URL("http://example.com");
            conn = (HttpURLConnection)url.openConnection(proxy);
            conn.setRequestMethod("GET");
            DataInputStream in1 = new DataInputStream(this.getClass().getClassLoader().getResourceAsStream("result-example-com"));
            byte [] expect = new byte[8192];
            int dataLen = in1.read(expect);
            DataInputStream in2 = new DataInputStream(conn.getInputStream());
            byte [] result = new byte[8192];
            in2.readFully(result, 0, dataLen);
            boolean compareResult = Arrays.equals(result, expect);
            if (!compareResult) {
                log.debug("====================");
                log.debug(new String(result));
                log.debug("====================");
            }
            assertTrue(compareResult);
        }catch(IOException e){
            log.error("Failed with exception.", e);
            fail();
        }finally{
            if (conn != null) {
                conn.disconnect();
            }
            assertTrue(server.shutdown());
            assertTrue(local.shutdown());
        }
    }
    @Test
    public void testHttp() {
        String [] methodList = {
            "aes-128-cfb",
            "aes-128-ofb",
            "aes-192-cfb",
            "aes-192-ofb",
            "aes-256-cfb",
            "aes-256-ofb",
            "chacha20"
        };
        for (String method: methodList) {
            GlobalConfig.get().setMethod(method);
            log.debug("Test method: " + method);
            testSimpleHttp(true);
        }
    }

    @Test
    public void testHttpWithoutOTA() {
        testSimpleHttp(false);
    }
}

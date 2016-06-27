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

public class UnitTest {
    public static Logger log = LogManager.getLogger(UnitTest.class.getName());

    @Test
    public void testSetConfigFromArgv() {
        String [] argv = {
            "-k", "test",
            "-m", "aes-128-cfb",
            "-s", "9.8.7.6",
            "-p", "4321",
            "-l", "1234",
            "-t", "400",
            "-a",
            "-S",
            "-L"
        };
        Main.main(argv);
        assertEquals(GlobalConfig.get().getPassword(), "test");
        assertEquals(GlobalConfig.get().getMethod(), "aes-128-cfb");
        assertEquals(GlobalConfig.get().getServer(), "9.8.7.6");
        assertEquals(GlobalConfig.get().getPort(), 4321);
        assertEquals(GlobalConfig.get().getLocalPort(), 1234);
        assertEquals(GlobalConfig.get().getTimeout(), 400);
        assertEquals(GlobalConfig.get().isOTAEnabled(), true);
        //The last value go into effect.
        assertEquals(GlobalConfig.get().isServerMode(), false);
    }
}

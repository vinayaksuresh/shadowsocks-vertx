/*   
 *   Copyright 2016 Author:Bestoa bestoapache@gmail.com
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
package shadowsocks.auth;

import java.nio.ByteBuffer;

import shadowsocks.auth.AuthException;

/**
 * Auth base class
 */
public abstract class SSAuth{
    
    public abstract byte [] doAuth(byte[] key, byte[] data) throws AuthException;
    public abstract boolean doAuth(byte[] key, byte[] data, byte[] auth) throws AuthException;

    public static byte [] prepareKey(byte [] i, int c){
        byte [] key = new byte[i.length + 4];
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(c);
        System.arraycopy(i, 0, key, 0, i.length);
        System.arraycopy(b.array(), 0, key, i.length, 4);
        return key;
    }

    public static byte [] prepareKey(byte [] i, byte [] k){
        byte [] key = new byte[i.length + k.length];
        System.arraycopy(i, 0, key, 0, i.length);
        System.arraycopy(k, 0, key, i.length, k.length);
        return key;
    }
}

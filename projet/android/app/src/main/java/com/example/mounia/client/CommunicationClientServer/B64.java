package com.example.mounia.client.CommunicationClientServer;

import java.util.HashMap;

/**
 * Created by Gabriel on 4/3/2018.
 */

public class B64 {
    private static final HashMap<Character, Byte> decodeMap; static {
        decodeMap = new HashMap<Character, Byte>();
        byte b = 0;
        for (char c = 'A'; c <= 'Z'; ++c, ++b)
            decodeMap.put(c, b);
        for (char c = 'a'; c <= 'z'; ++c, ++b)
            decodeMap.put(c, b);
        for (char c = '0'; c <= '9'; ++c, ++b)
            decodeMap.put(c, b);
        decodeMap.put('+', b++);
        decodeMap.put('/', b);
    }
    public static byte[] decode(String msg)
    {
        int len = msg.length();
        if (len == 0)
            return new byte[0];
        byte[] res;
        byte bits0 = decodeMap.get(msg.charAt(len - 4)), bits1 = decodeMap.get(msg.charAt(len - 3)), bits2, bits3;
        if (msg.charAt(len - 2) == '='){
            res = new byte[(len >> 2) * 3 - 2];
            res[res.length - 1] = (byte)((bits0 << 2) | (bits1 >> 4));
        } else if (msg.charAt(len - 1) == '='){
            res = new byte[(len >> 2) * 3 - 1];
            bits2 = decodeMap.get(msg.charAt(len - 2));
            res[res.length - 2] = (byte)((bits0 << 2) | (bits1 >> 4));
            res[res.length - 1] = (byte)(((bits1 & 15) << 4) | (bits2 >> 2));
        } else {
            res = new byte[(len >> 2) * 3];
            bits2 = decodeMap.get(msg.charAt(len - 2));
            bits3 = decodeMap.get(msg.charAt(len - 1));
            res[res.length - 3] = (byte)((bits0 << 2) | (bits1 >> 4));
            res[res.length - 2] = (byte)(((bits1 & 15) << 4) | (bits2 >> 2));
            res[res.length - 1] = (byte)(((bits2 & 3) << 6) | bits3);
        }
        for (int i = 0, j = 0, n = len - 4; i < n; i += 4, j += 3){
            bits0 = decodeMap.get(msg.charAt(i));
            bits1 = decodeMap.get(msg.charAt(i + 1));
            bits2 = decodeMap.get(msg.charAt(i + 2));
            bits3 = decodeMap.get(msg.charAt(i + 3));
            res[j] = (byte)((bits0 << 2) | (bits1 >> 4));
            res[j + 1] = (byte)(((bits1 & 15) << 4) | (bits2 >> 2));
            res[j + 2] = (byte)(((bits2 & 3) << 6) | bits3);
        }
        return res;
    }
}

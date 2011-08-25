package com.madgag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

/**
 * Tests to see if you're running on a VM which has had these fixes:
 *
 * https://issues.apache.org/jira/browse/HARMONY-6637
 * http://code.google.com/p/android/issues/detail?id=11755
 *
 */
public class ZeroByteInflationCheck {

    public static boolean checkHarmoniousRepose() {
        Inflater inflater=new Inflater();
        try {
            inflater.setInput(demoDeflatedZeroBytes());
            inflater.inflate(new byte[0], 0, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inflater.finished();
    }

    private static byte[] demoDeflatedZeroBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
        deflaterOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}

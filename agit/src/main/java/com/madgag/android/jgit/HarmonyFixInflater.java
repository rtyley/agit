package com.madgag.android.jgit;

import android.util.Log;
import org.eclipse.jgit.lib.InflaterCache;
import org.eclipse.jgit.lib.InflaterFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

/**
 * This class is a fix for two separate issues with Android Inflater support:
 *
 * Inflater and the Zero-Byte Killer (pre-HoneyComb)
 * https://issues.apache.org/jira/browse/HARMONY-6637 - basically, Harmony JRE will not set finished()=true when you ask
 * it to inflate zero bytes of data, even if you are looking at a zero-length stream or just happened to have already
 * inflated exactly the amount of data you were already looking for. This causes problem for IndexPack.inflate() and
 * other Inflater-users.
 *
 * InflaterInputStream and This Is Not The End for my Inflater
 * https://github.com/rtyley/agit/issues/47 - Calling InflaterInputStream.close() on Oracle Java doesn't call end() on
 * its Inflater if you supplied the Inflater in the constructor - but Android, incorrectly, *does*. Inflaters can't be
 * used after end() has been called, which means you can't re-use Inflaters after using them with an InflaterInputStream
 * on Android - which is precisely what JGit tries to do with InflaterCache. This results in either NullPointerException
 * or IllegalStateException depending on what version of Android you're using.
 *
 */
public class HarmonyFixInflater extends Inflater {

    public static final String TAG = "HFI";
    
    public static final InflaterFactory HARMONY_FIX_FACTORY = new InflaterFactory() {
        public Inflater create() { return new HarmonyFixInflater(); }

        @Override
        public void decommision(Inflater inflater) {
            ((HarmonyFixInflater) inflater).decommision();
        }
    };

    public static void establishHarmoniousRepose() {
        InflaterCache.INFLATER_FACTORY = HARMONY_FIX_FACTORY;
    }

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

    private static final byte[] oneByteArray = new byte[1];

    private HarmonyFixInflater() {
        super(false);
    }

    public void setInput(byte[] b, int off, int len) {
        super.setInput(b, off, len);
    }

    public void setInput(byte[] b) {
        super.setInput(b);
    }

    public void setDictionary(byte[] b, int off, int len) {
        super.setDictionary(b, off, len);
    }

    public void setDictionary(byte[] b) {
        super.setDictionary(b);
    }

    public int getRemaining() {
        return super.getRemaining();
    }

    public boolean needsInput() {
        return super.needsInput();
    }

    public boolean needsDictionary() {
        return super.needsDictionary();
    }

    public boolean finished() {
        return super.finished();
    }

    public int inflate(byte[] b, int off, int len) throws DataFormatException {
        if (len!=0) {
            return super.inflate(b, off, len);
        }

        int bytesInflated=super.inflate(oneByteArray, 0, 1); // have to pretend to want at least one byte so that the finished flag is correctly set
        if (bytesInflated>0) {
            throw new RuntimeException("The Harmony-Fix hack has served you ill, we were not supposed to read any data...");
        }
        return 0;
    }

    public int inflate(byte[] b) throws DataFormatException {
        return super.inflate(b);
    }

    public int getAdler() {
        return super.getAdler();
    }

    public int getTotalIn() {
        return super.getTotalIn();
    }

    public long getBytesRead() {
        return super.getBytesRead();
    }

    public int getTotalOut() {
        return super.getTotalOut();
    }

    public long getBytesWritten() {
        return super.getBytesWritten();
    }

    public void reset() {
        //Log.d(TAG,this+" - reset()");
        // Thread.dumpStack();
        super.reset();
    }

    public void end() {
        // Log.d(TAG, this + " - end() called, will ignore");
        // Thread.dumpStack();
        // DO NOT call end method on wrapped inflater, because the InflaterCache will want to re-use it
    }

    private void decommision() {
        Log.d(TAG,this+" - decommision(). See https://github.com/rtyley/agit/issues/47");
        super.end();
    }
}

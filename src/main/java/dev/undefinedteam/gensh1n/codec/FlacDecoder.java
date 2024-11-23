package dev.undefinedteam.gensh1n.codec;

import dev.undefinedteam.gensh1n.codec.flac.FLACDecoder;
import dev.undefinedteam.gensh1n.codec.flac.metadata.Metadata;
import dev.undefinedteam.gensh1n.codec.flac.metadata.StreamInfo;
import icyllis.modernui.audio.SoundSample;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@StringEncryption
@ControlFlowObfuscation
public class FlacDecoder extends SoundSample {
    private short[] pcm_data;
    private int sampleIndex,sampleDecoded;

    private FLACDecoder decoder;
    private StreamInfo info;
    private Queue<Short> buffer;

    public FlacDecoder(ByteBuffer mPayload) {
        BufferInputStream input = new BufferInputStream(mPayload);

        try {
            decoder = new FLACDecoder(input);
            Metadata[] d = decoder.readMetadata();
            info = (StreamInfo) d[0];

            // check support
            if (info.getChannels() > 2)
                throw new IOException("Number of channels > 2; unsupported");
//            if (info.getSampleRate() != 44100)
//                throw new IOException("Sample rate is not 44.1kHz; unsupported.");

            // initialize buffer
            buffer = new LinkedList<>();

            this.mChannels = info.getChannels();
            this.mSampleRate = info.getSampleRate();
            this.mTotalSamples = (int) info.getTotalSamples();
            this.pcm_data = new short[mTotalSamples * mChannels];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean seek(int sampleOffset) {
        this.sampleIndex = sampleOffset * this.mChannels;
        return true;
    }

    @Override
    public int getSamplesShortInterleaved(ShortBuffer pcmBuffer) {
        int decoded = 0;
        int copyLen = pcmBuffer.remaining();

        while (sampleIndex + copyLen > sampleDecoded) {
            try {
                var pack = nextMonoFrame();
                System.arraycopy(pack, 0, pcm_data, sampleDecoded, pack.length);
                sampleDecoded += pack.length;
            } catch (Exception e) {
                return 0;
            }
        }

        try {
            pcmBuffer.put(Arrays.copyOfRange(this.pcm_data, this.sampleIndex, this.sampleIndex + copyLen));
            this.sampleIndex += copyLen;
            decoded += copyLen;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return decoded / this.mChannels;
    }

    @Override
    public void close() {
    }

    public short[] nextMonoFrame() throws IOException {
        if (buffer.size() < 1024)
            fillBuffer();

        if (buffer.size() < 1024)
            return null;

        // grab samples from the buffer and return them
        short[] frame = new short[1024];
        for (int i = 0; i < frame.length; i++)
            frame[i] = buffer.poll();

        return frame;
    }

    /**
     * Fills buffer with mono PCM samples as much as it can. Best-effort.
     * @throws IOException on decoder error
     */
    private void fillBuffer() throws IOException {

        while (buffer.size() < 1024) {
            try {
                // get & decode a frame
                var encodedFrame = decoder.readNextFrame();
                var d = decoder.decodeFrame(encodedFrame, null);

                /* ByteData has a larger capacity than the data it contains. getLen()
                 * doesn't return the capacity, it returns the number of valid elements
                 * in the collection. The rest of the values are initialized to 0, so
                 * you can't do a foreach because you'll read out all those as well. */
                byte[] untrimmedByteFrame = d.getData();
                byte[] byteFrame = new byte[d.getLen()];
                if (d.getLen() >= 0) System.arraycopy(untrimmedByteFrame, 0, byteFrame, 0, d.getLen());

                // convert byte[] to short[]
                short[] shortFrame = new short[byteFrame.length];
                for (int i = 0; i < shortFrame.length; i++)
                    shortFrame[i] = byteFrame[i];

                // merge channels to mono if we're working with stereo
                if (info.getChannels() == 2)
                    shortFrame = mergeChannels(shortFrame);

                // add samples to buffer
                for (short s : shortFrame)
                    buffer.add(s);

            } catch (NullPointerException e) { return; }
        }
    }
    /**
     * Merges stereo audio by averaging channels together
     * @param samples interlaced stereo sample buffer
     * @return mono sample buffer
     */
    private short[] mergeChannels(short[] samples) {

        int l = (int) Math.floor(samples.length / 2);
        short[] merged = new short[l];

        for (int i = 0; i < l; i++)
            merged[i] = (short) ((samples[i * 2] + samples[i * 2 + 1]) / 2.0f);

        return merged;
    }
}

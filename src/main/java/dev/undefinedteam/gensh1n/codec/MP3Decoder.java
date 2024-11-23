package dev.undefinedteam.gensh1n.codec;

import icyllis.modernui.audio.SoundSample;
import javazoom.jl.decoder.*;
import org.checkerframework.checker.units.qual.C;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.*;

import static org.lwjgl.system.MemoryUtil.memAllocShort;
import static org.lwjgl.system.MemoryUtil.memFree;

@StringEncryption
@ControlFlowObfuscation
public class MP3Decoder extends SoundSample {
    public final short[] pcm_data;
    public int sampleIndex, samplesDecoded,totalSamplesDecoded;
    private final ByteBuffer mPlayload;
    private final BufferInputStream inputStream;
    private final Bitstream bitstream;
    private final Decoder decoder;
    public int div = 1;

    public MP3Decoder(ByteBuffer mPayload) {
        try {
            this.mPlayload = mPayload;
            mPlayload.position(0);
            BufferInputStream input = new BufferInputStream(mPayload);
            Bitstream bitstream = new Bitstream(input);
            Decoder decoder = new Decoder();

            int divider = 1;
            int frames = 0;

            Header header = bitstream.readFrame();
            if (header == null) throw new RuntimeException("no header");

            decoder.decodeFrame(header, bitstream);
            if (decoder.getOutputFrequency() <= 24000)
                divider *= 2;
            if (decoder.getOutputChannels() == 1)
                divider *= 2;

            var len = decoder.getOutputBlockSize() / div;

            frames++;
            while (bitstream.readFrame() != null) {
                bitstream.closeFrame();
                frames++;
            }
            input.close();
            bitstream.close();

            this.mChannels = decoder.getOutputChannels();
            this.mSampleRate = decoder.getOutputFrequency();
            var samples = frames * decoder.getOutputBlockSize() / divider;
            this.mTotalSamples = samples / 2;
            this.pcm_data = new short[samples];

            mPlayload.position(0);
            inputStream = new BufferInputStream(mPayload);
            this.bitstream = new Bitstream(inputStream);
            this.decoder = new Decoder();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("failed");
        }
    }

    @Override
    public boolean seek(int sampleOffset) {
        this.sampleIndex = sampleOffset * this.mChannels;
//        try {
//            mPlayload.position(0);
//            this.bitstream = new Bitstream(new BufferInputStream(mPlayload));
//            this.decoder = new Decoder();
//            int frames = calcFrame(sampleIndex);
//            frames--;
//            for (int i = 0; i < frames; i++) {
//                bitstream.readFrame();
//                bitstream.closeFrame();
//            }
//
//            samplesDecoded = sampleIndex;
//        } catch (Exception e) {
//            return false;
//        }
        return true;
    }

    @Override
    public int getSamplesShortInterleaved(ShortBuffer pcmBuffer) {
        int decoded = 0;
        int copyLen = pcmBuffer.remaining();

        try {
            var targetSamples = sampleIndex + copyLen;
            while (samplesDecoded < targetSamples && decode(targetSamples)) ;
            pcmBuffer.put(Arrays.copyOfRange(this.pcm_data, this.sampleIndex, this.sampleIndex + copyLen));
            this.sampleIndex += copyLen;
            decoded += copyLen;
        } catch (ArrayIndexOutOfBoundsException | BitstreamException | DecoderException ignored) {
        }
        return decoded / this.mChannels;
    }

    public boolean decode(int targetSamples) throws BitstreamException, DecoderException {
        List<ShortBuffer> frames = new ArrayList<>();
        Header header;
        int decoded = 0;
        while (samplesDecoded + decoded <= targetSamples && (header = bitstream.readFrame()) != null) {
            SampleBuffer sb = (SampleBuffer) decoder.decodeFrame(header, bitstream);
            if (div == 1) {
                if (decoder.getOutputFrequency() <= 24000)
                    div *= 2;
                if (decoder.getOutputChannels() == 1)
                    div *= 2;
            }
            ShortBuffer sbuf = memAllocShort(decoder.getOutputBlockSize());
            sbuf.put(sb.getBuffer());
            sbuf.flip();
            bitstream.closeFrame();
            var len = decoder.getOutputBlockSize() / div;
            decoded += len;
            frames.add(sbuf);
        }
        if (!frames.isEmpty()) {
            ShortBuffer _pcm_buffer = memAllocShort(decoded);
            for (ShortBuffer frame : frames) {
                for (int i = 0; i < decoder.getOutputBlockSize() / div; i++)
                    _pcm_buffer.put(frame.get(i));
                memFree(frame);
            }
            _pcm_buffer.flip();
            _pcm_buffer.get(pcm_data, samplesDecoded, decoded);
            samplesDecoded += decoded;
            memFree(_pcm_buffer);
        }
        return !frames.isEmpty();
    }

    private int calcFrame(int targetSamples) {
        return targetSamples * div / decoder.getOutputBlockSize();
    }

    @Override
    public void close() {
        try {
            bitstream.close();
        } catch (BitstreamException e) {
        }
    }
}

package dev.undefinedteam.gensh1n.codec.flac.sound.spi;

import dev.undefinedteam.gensh1n.codec.flac.metadata.StreamInfo;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;
import static dev.undefinedteam.gensh1n.codec.flac.sound.spi.FlacFileFormatType.FLAC;

class FlacAudioFileFormat extends AudioFileFormat {

    /**
     * Property key for minimum frame size. The value is of type Integer and
     * gives the minimum size of encoded frames in bytes.
     */
    public static final String KEY_FRAMESIZE_MIN = "flac.minframesize";
    /**
     * Property key for maximum frame size. The value is of type Integer and
     * gives the maximum size of encoded frames in bytes.
     */
    public static final String KEY_FRAMESIZE_MAX = "flac.maxframesize";
    /**
     * Property key for minimum block size. The value is of type Integer and
     * gives the minimum size of decoded frames in bytes.
     */
    public static final String KEY_BLOCKSIZE_MIN = "flac.minblocksize";
    /**
     * Property key for maximum block size. The value is of type Integer and
     * gives the maximum size of decoded frames in bytes.
     */
    public static final String KEY_BLOCKSIZE_MAX = "flac.maxblocksize";

    private final HashMap<String, Object> props;

    public FlacAudioFileFormat(long byteLength, StreamInfo streamInfo, AudioFormat format) {
        super(FLAC, byteLength > Integer.MAX_VALUE ? NOT_SPECIFIED : (int) byteLength,
                format, streamInfo.getTotalSamples() > Integer.MAX_VALUE ? NOT_SPECIFIED : (int) streamInfo.getTotalSamples());

        props = new HashMap<>();
        props.put(KEY_FRAMESIZE_MIN, streamInfo.getMinFrameSize());
        props.put(KEY_FRAMESIZE_MAX, streamInfo.getMaxFrameSize());
        props.put(KEY_BLOCKSIZE_MIN, streamInfo.getMinBlockSize());
        props.put(KEY_BLOCKSIZE_MAX, streamInfo.getMaxBlockSize());
    }

    /**
     * Java 5.0 compatible method to get the full map of properties. The
     * properties use the KEY_ keys defined in this class.
     */
    public Map<String, Object> properties() {
        Map<String, Object> ret;
        if (props == null) {
            ret = new HashMap<>(0);
        } else {
            ret = (Map<String, Object>) props.clone();
        }
        return Collections.unmodifiableMap(ret);
    }

    /**
     * Java 5.0 compatible method to get a property. As key use the KEY_ constants defined in this class.
     */
    public Object getProperty(String key) {
        if (props == null) {
            return null;
        }
        return props.get(key);
    }

}

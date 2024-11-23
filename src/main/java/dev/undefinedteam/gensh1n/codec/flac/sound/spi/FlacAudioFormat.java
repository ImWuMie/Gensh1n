/**
 *
 */
package dev.undefinedteam.gensh1n.codec.flac.sound.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import dev.undefinedteam.gensh1n.codec.flac.metadata.StreamInfo;

/**
 * An AudioFormat instance wrapping a FLAC StreamInfo object. With Java 5.0
 * style properties support.
 *
 * @author Florian Bomers
 */
public class FlacAudioFormat extends AudioFormat {

	private final HashMap<String, Object> props;

	public FlacAudioFormat(StreamInfo streamInfo) {
		super(FlacEncoding.FLAC, streamInfo.getSampleRate(),
				streamInfo.getBitsPerSample(), streamInfo.getChannels(),
				/* streamInfo.maxFrameSize */AudioSystem.NOT_SPECIFIED,
				streamInfo.getSampleRate(), false);
		props = new HashMap<>();
		props.put("bitrate", streamInfo.getBitsPerSample());
		props.put("vbr", true);
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

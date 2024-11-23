/*
 * Created on Jun 28, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package dev.undefinedteam.gensh1n.codec.flac;

import java.util.HashSet;
import java.util.Iterator;

import dev.undefinedteam.gensh1n.codec.flac.frame.Frame;
import dev.undefinedteam.gensh1n.codec.flac.metadata.Metadata;


/**
 * Class to handle frame listeners.
 * @author kc7bfi
 */
class FrameListeners implements FrameListener {
    private final HashSet frameListeners = new HashSet();

    /**
     * Add a frame listener.
     * @param listener  The frame listener to add
     */
    public void addFrameListener(FrameListener listener) {
        synchronized (frameListeners) {
            frameListeners.add(listener);
        }
    }

    /**
     * Remove a frame listener.
     * @param listener  The frame listener to remove
     */
    public void removeFrameListener(FrameListener listener) {
        synchronized (frameListeners) {
            frameListeners.remove(listener);
        }
    }

    /**
     * Process metadata records.
     * @param metadata the metadata block
     * @see dev.undefinedteam.gensh1n.codec.flac.FrameListener#processMetadata(dev.undefinedteam.gensh1n.codec.flac.metadata.Metadata)
     */
    public void processMetadata(Metadata metadata) {
        synchronized (frameListeners) {
            Iterator it = frameListeners.iterator();
            while (it.hasNext()) {
                FrameListener listener = (FrameListener)it.next();
                listener.processMetadata(metadata);
            }
        }
    }

    /**
     * Process data frames.
     * @param frame the data frame
     * @see dev.undefinedteam.gensh1n.codec.flac.FrameListener#processFrame(dev.undefinedteam.gensh1n.codec.flac.frame.Frame)
     */
    public void processFrame(Frame frame) {
        synchronized (frameListeners) {
            Iterator it = frameListeners.iterator();
            while (it.hasNext()) {
                FrameListener listener = (FrameListener)it.next();
                listener.processFrame(frame);
            }
        }
    }

    /**
     * Called for each frame error detected.
     * @param msg   The error message
     * @see dev.undefinedteam.gensh1n.codec.flac.FrameListener#processError(String)
     */
    public void processError(String msg) {
        synchronized (frameListeners) {
            Iterator it = frameListeners.iterator();
            while (it.hasNext()) {
                FrameListener listener = (FrameListener)it.next();
                listener.processError(msg);
            }
        }
    }

}

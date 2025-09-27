package com.boozer.nexus.voice;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.time.Duration;

/**
 * Captures audio from the default system microphone using a 16 kHz mono PCM format
 * optimised for Whisper transcription. The recorder returns raw PCM bytes which can
 * be wrapped into a WAV container before sending to remote services.
 */
public class AudioRecorder implements AutoCloseable {
    private static final AudioFormat FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            16_000F,
            16,
            1,
            2,
            16_000F,
            false
    );

    private TargetDataLine targetLine;
    private volatile boolean recording;

    public static boolean isSupported() {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
        return AudioSystem.isLineSupported(info);
    }

    public AudioFormat getFormat() {
        return FORMAT;
    }

    public synchronized void start() throws LineUnavailableException {
        if (recording) {
            return;
        }
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
        targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open(FORMAT);
        targetLine.start();
        recording = true;
    }

    public synchronized void stop() {
        recording = false;
        if (targetLine != null) {
            targetLine.stop();
            targetLine.close();
            targetLine = null;
        }
    }

    /**
     * Capture audio for the provided duration and return raw PCM bytes.
     */
    public byte[] capture(Duration duration) {
        if (!recording || targetLine == null) {
            return new byte[0];
        }

        int bytesPerMilli = (int) FORMAT.getFrameRate() * FORMAT.getFrameSize() / 1000;
        int totalBytes = Math.max(bytesPerMilli * (int) duration.toMillis(), bytesPerMilli);
        byte[] buffer = new byte[Math.min(totalBytes, 4096)];
        ByteArrayOutputStream output = new ByteArrayOutputStream(totalBytes);

        long end = System.currentTimeMillis() + duration.toMillis();
        while (System.currentTimeMillis() < end && recording) {
            int read = targetLine.read(buffer, 0, buffer.length);
            if (read > 0) {
                output.write(buffer, 0, read);
            }
        }
        return output.toByteArray();
    }

    @Override
    public void close() {
        stop();
    }
}

package com.boozer.nexus.desktop.voice;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/** Lightweight PCM recorder for capturing microphone audio. */
public final class VoiceRecorder implements AutoCloseable {
    private static final AudioFormat FORMAT = new AudioFormat(16000f, 16, 1, true, false);

    private final AtomicBoolean recording = new AtomicBoolean(false);
    private TargetDataLine line;
    private Thread captureThread;
    private ByteArrayOutputStream buffer;

    public void start() throws LineUnavailableException {
        if (recording.get()) {
            return;
        }
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(FORMAT);
        line.start();

        buffer = new ByteArrayOutputStream();
        recording.set(true);
        captureThread = new Thread(this::captureLoop, "voice-capture-thread");
        captureThread.setDaemon(true);
        captureThread.start();
    }

    private void captureLoop() {
        byte[] data = new byte[FORMAT.getFrameSize() * 1024];
        while (recording.get()) {
            int read = line.read(data, 0, data.length);
            if (read > 0) {
                buffer.write(data, 0, read);
            }
        }
    }

    public byte[] stopAndGetWav() throws IOException {
        if (!recording.compareAndSet(true, false)) {
            return new byte[0];
        }
        if (line != null) {
            line.stop();
            line.flush();
            line.close();
        }
        if (captureThread != null) {
            try {
                captureThread.join(250L);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        byte[] pcm = buffer == null ? new byte[0] : buffer.toByteArray();
        buffer = null;
        return convertToWav(pcm);
    }

    public boolean isRecording() {
        return recording.get();
    }

    private byte[] convertToWav(byte[] pcm) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(pcm);
        AudioInputStream stream = new AudioInputStream(input, FORMAT, pcm.length / FORMAT.getFrameSize());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, output);
        return output.toByteArray();
    }

    @Override
    public void close() throws IOException {
        if (recording.get()) {
            stopAndGetWav();
        }
    }
}

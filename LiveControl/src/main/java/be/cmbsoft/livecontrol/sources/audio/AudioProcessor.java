package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.livecontrol.LiveControl;
import processing.sound.Amplitude;
import processing.sound.AudioIn;
import processing.sound.FFT;
import processing.sound.Waveform;

public class AudioProcessor
{

    private final Waveform  waveform;
    private final FFT       fft;
    private final AudioIn   inputLeft;
    private final AudioIn   inputRight;
    private final Amplitude amplitudeLeft;
    private final Amplitude amplitudeRight;
    private final int       samples = 200;
    private final int       bands   = 256;


    public AudioProcessor(LiveControl parent)
    {
        inputLeft = new AudioIn(parent, 0);
        inputLeft.start();

        inputRight = new AudioIn(parent, 1);
        inputRight.start();

        amplitudeRight = new Amplitude(parent);
        amplitudeRight.input(inputRight);

        amplitudeLeft = new Amplitude(parent);
        amplitudeLeft.input(inputLeft);

        waveform = new Waveform(parent, samples);
        waveform.input(inputLeft);

        fft = new FFT(parent, bands);
        fft.input(inputLeft);
    }

    public Amplitude getAmplitudeLeft()
    {
        return amplitudeLeft;
    }

    public Amplitude getAmplitudeRight()
    {
        return amplitudeRight;
    }

    public FFT getFft()
    {
        return fft;
    }

    public AudioIn getInputLeft()
    {
        return inputLeft;
    }

    public AudioIn getInputRight()
    {
        return inputRight;
    }

    public Waveform getWaveform()
    {
        return waveform;
    }

    public float[] getAnalysedFftSpectrum()
    {
        fft.analyze();
        return fft.spectrum;
    }

    public int getSamplesAmount()
    {
        return samples;
    }

}

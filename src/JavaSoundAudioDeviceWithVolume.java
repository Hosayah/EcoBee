import javazoom.jl.player.JavaSoundAudioDevice;
public class JavaSoundAudioDeviceWithVolume extends JavaSoundAudioDevice {
    private float volume;

    public JavaSoundAudioDeviceWithVolume(float volume) {
        this.volume = volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    protected void writeImpl(short[] samples, int offs, int len) {
        // Apply volume scaling
        try{
            for (int i = offs; i < offs + len; i++) {
                samples[i] = (short) (samples[i] * volume);
            }
            super.writeImpl(samples, offs, len);
        } catch (Exception e){
            System.out.println(e.toString());
        }
        
    }
}


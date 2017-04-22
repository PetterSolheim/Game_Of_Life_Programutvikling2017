package model;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import view.DialogBoxes;

/**This class is responsible for handling everything related to audio.
 *
 */
public class AudioManager {
    
    static AudioManager instance;
    
    static Synthesizer synt;
    static Sequencer sequencer;
    
    private AudioManager (){
        instance = this;
        try {
        synt = MidiSystem.getSynthesizer();
        sequencer = MidiSystem.getSequencer();
        } catch (MidiUnavailableException exception){
            DialogBoxes.genericErrorMessage("No midi devices available", exception.getMessage());
        }
    }
    public static AudioManager getSingelton (){
        if(instance != null){
        return instance;
        }
        return new AudioManager();
    }
}

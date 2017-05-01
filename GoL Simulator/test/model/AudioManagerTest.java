/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.AudioSettingsWindowController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Even
 */
public class AudioManagerTest {

    private AudioManager instance;

    public AudioManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isCreated method, of class AudioManager.
     */
    @Test
    public void testIsCreatedTrue() {
        //arrange

        //act
        boolean isCrreated = AudioManager.isCreated();
        //assert
        assertTrue(isCrreated);
    }

    /**
     * Test of getSingelton method, of class AudioManager.
     */
    @Test
    public void testGetSingelton() {
        //arrange

        //act
        AudioManager audioManager = AudioManager.getSingelton();
        //assert
        assertNotNull(audioManager);
    }

    /**
     * Test of setController method, of class AudioManager.
     */
    @Test
    public void testSetController() {
        System.out.println("setController");
        //arrange
        AudioSettingsWindowController controller = new AudioSettingsWindowController();
        //act
        instance.setController(controller);
        //assert
        fail("The test case is a prototype.");
    }

    private void setUpClip() {
        AudioManager instance = AudioManager.getSingelton();
        File f = new File("test/sounds/cow_moo.wav");
        instance.addAbsolutePath(f);
        try {
            AudioInputStream testStream = AudioSystem.getAudioInputStream(f);
            System.out.println(instance.getActiveSong().toString());
            //instance.playPauseMusicPlayer();
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(AudioManagerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AudioManagerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of volume method, of class AudioManager.
     */
    @Test
    public void testVolumeMax() {
        System.out.println("volume");
        setUpClip();
        FloatControl activeSongGain = (FloatControl) instance.getActiveSong().getControl(FloatControl.Type.MASTER_GAIN);
        //act
        float volume = 100f;
        instance.volume(volume);
        float Gain = activeSongGain.getMaximum();
        //assert
        assertFalse(Gain + 1f < activeSongGain.getMaximum());
    }

    /**
     * Test of addAbsolutePath method, of class AudioManager.
     */
    @Test

    public void testAddAbsolutePath() {
        System.out.println("addAbsolutePath");
        File f = null;
        AudioManager instance = null;
        instance.addAbsolutePath(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllLoadedSongs method, of class AudioManager.
     */
    @Test
    public void testGetAllLoadedSongs() {
        System.out.println("getAllLoadedSongs");
        AudioManager instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getAllLoadedSongs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadSongFromAbsolutePath method, of class AudioManager.
     */
    @Test
    public void testLoadSongFromAbsolutePath() throws Exception {
        System.out.println("loadSongFromAbsolutePath");
        String songName = "";
        AudioManager instance = null;
        instance.loadSongFromAbsolutePath(songName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetSong method, of class AudioManager.
     */
    @Test
    public void testResetSong() {
        System.out.println("resetSong");
        AudioManager instance = null;
        instance.resetSong();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generateAudioSequence method, of class AudioManager.
     */
    @Test
    public void testGenerateAudioSequence() {
        System.out.println("generateAudioSequence");
        ArrayList<ArrayList> data = null;
        AudioManager instance = null;
        instance.generateAudioSequence(data);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActiveSong method, of class AudioManager.
     */
    @Test
    public void testGetActiveSong() {
        System.out.println("getActiveSong");
        AudioManager instance = null;
        Clip expResult = null;
        Clip result = instance.getActiveSong();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playPauseMusicPlayer method, of class AudioManager.
     */
    @Test
    public void testPlayPauseMusicPlayer() {
        System.out.println("playPauseMusicPlayer");
        AudioManager instance = null;
        instance.playPauseMusicPlayer();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closeLines method, of class AudioManager.
     */
    @Test
    public void testCloseLines() {
        System.out.println("closeLines");
        AudioManager instance = null;
        instance.closeLines();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}

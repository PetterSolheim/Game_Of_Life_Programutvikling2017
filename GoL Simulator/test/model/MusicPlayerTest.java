package model;

import controller.MusicPlayerWindowController;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MusicPlayerTest {

    static MusicPlayer instance;

    public MusicPlayerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        instance = MusicPlayer.getSingelton();
    }

    @After
    public void tearDown() {
        instance.getAllLoadedSongs().clear();
        instance.closeLines();
    }

    /**
     * Test of isCreated method, of class MusicPlayer.
     */
    @Test
    public void testIsCreatedTrue() {
        //arrange
        //act
        boolean isCrreated = MusicPlayer.isCreated();
        //assert
        assertTrue(isCrreated);
    }

    /**
     * Test of getSingelton method, of class MusicPlayer.
     */
    @Test
    public void testGetSingelton() {
        //arrange
        //act
        MusicPlayer audioManager = MusicPlayer.getSingelton();
        //assert
        assertNotNull(audioManager);
    }

    private void setUpClip() throws UnsupportedAudioFileException {
        File f = new File("test/sounds/cow_moo.wav");
        instance.addAbsolutePath(f);
        instance.loadSong(f.getName());
    }

    /**
     * Test of volume method, of class MusicPlayer.
     */
    @Test
    public void testVolumeMax() {
        try {
            System.out.println("volume max");
            setUpClip();
            FloatControl activeSongGain = (FloatControl) instance.getActiveSong().getControl(FloatControl.Type.MASTER_GAIN);
            //act
            instance.volume(50);
            float oldGain = activeSongGain.getValue();
            instance.volume(100f);
            float Gain = activeSongGain.getValue();
            //assert
            assertTrue(Gain > oldGain);
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }
    
        /**
     * Test of volume method, of class MusicPlayer.
     */
    @Test
    public void testVolumeMin() {
        try {
            System.out.println("volume min");
            setUpClip();
            FloatControl activeSongGain = (FloatControl) instance.getActiveSong().getControl(FloatControl.Type.MASTER_GAIN);
            //act
            instance.volume(50);
            float oldGain = activeSongGain.getValue();
            instance.volume(0f);
            float newGain = activeSongGain.getValue();
            //assert
            assertTrue(newGain < oldGain);
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test of addAbsolutePath method, of class MusicPlayer.
     */
    @Test
    public void testAddAbsolutePath() {
        System.out.println("addAbsolutePath");
        //arrage
        File f = new File("test/sounds/cow_moo.wav");
        //act
        instance.addAbsolutePath(f);
        //assert
        assertTrue(instance.getAllLoadedSongs().contains(f.getAbsolutePath()));
    }

    /**
     * Test of getAllLoadedSongs method, of class MusicPlayer.
     */
    @Test
    public void testGetAllLoadedSongs() {
        System.out.println("getAllLoadedSongs");
        //arrange
        instance.addAbsolutePath(new File("test/sounds/cow_moo.wav"));
        instance.addAbsolutePath(new File("test/sounds/test.wav"));
        //act
        ArrayList<String> result = instance.getAllLoadedSongs();
        //assert
        assertTrue(result.size() == 2);
        assertTrue(result.get(0).contains("cow_moo.wav"));
        assertTrue(result.get(1).contains("test.wav"));
    }

    /**
     * Test of loadSongFromAbsolutePath method, of class MusicPlayer.
     */
    @Test
    public void testLoadSong() {
        try {
            System.out.println("loadSongFromAbsolutePath");
            //arrange
            instance.addAbsolutePath(new File("test/sounds/cow_moo.wav"));
            ArrayList<String> songs = instance.getAllLoadedSongs();
            File file = new File(songs.get(0));
            //act
            instance.loadSong(file.getName());
            //instance.playPauseMusicPlayer();
            //assert
            assertTrue(instance.getActiveSong().isOpen());
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test of resetSong method, of class MusicPlayer.
     */
    @Test
    public void testResetSong() {
        try {
            System.out.println("resetSong");
            //arrange
            setUpClip();
            //act
            instance.resetSong();
            //assert
            assertTrue(instance.getActiveSong().getFramePosition() < 1);
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Test of getActiveSong method, of class MusicPlayer.
     */
    @Test
    public void testGetActiveSong() {
        try {
            System.out.println("getActiveSong");
            //aarrange
            setUpClip();
            //act
            Clip clip = instance.getActiveSong();
            //assert
            assertNotNull(clip);
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test of playPauseMusicPlayer method, of class MusicPlayer.
     */
    @Test
    public void testPlayPauseMusicPlayer() {
        try {
            System.out.println("playPauseMusicPlayer");
            //arrange
            setUpClip();
            //act
            instance.playPauseMusicPlayer();
            //assert
            assertFalse(instance.getActiveSong().isActive());
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test of closeLines method, of class MusicPlayer.
     */
    @Test
    public void testCloseLines() {
        try {
            System.out.println("closeLines");
            //arrange
            setUpClip();
            //act
            instance.closeLines();
            //asert
            assertFalse(instance.getActiveSong().isOpen());
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }

}

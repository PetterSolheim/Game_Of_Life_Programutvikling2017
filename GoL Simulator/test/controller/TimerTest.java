package controller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TimerTest {

    public Timer instance;

    public TimerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        instance = new Timer(new MainWindowController());
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of handle method, of class Timer.
     */
    @Test
    public void testHandle() throws Exception {
        System.out.println("testHandle");
        //arrange
        long currentTime = 4000000000l;
        //act
        instance.setFps(4);
        try {
            instance.handle(currentTime);
        } catch (Exception e) {
        }
        // assert
        assertTrue(instance.getNextGeneration() > currentTime);
        assertEquals(instance.getNextGeneration(), currentTime + instance.getTimeBetweenGenerations());
    }

    /**
     * Test of setFps method, of class Timer.
     */
    @Test
    public void testSetFps() {
        System.out.println("testSetFps");
        //arrange
        double desiredFramesPerSecond = 4;
        //act
        instance.setFps(desiredFramesPerSecond);
        //assert
        assertEquals(instance.getTimeBetweenGenerations(), 250000000);
    }
}

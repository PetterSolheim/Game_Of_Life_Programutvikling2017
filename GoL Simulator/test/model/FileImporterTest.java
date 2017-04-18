package model;

import java.util.ArrayList;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aleks
 */
public class FileImporterTest {

    Rules rules = Rules.getInstance();

    @Test
    public void testReadGameBoardFromDisk() throws Exception {
        System.out.println("readGameBoardFromDisk");

        // read the file used for testing
        FileImporter instance = new FileImporter();
        instance.setPadding(0);
        Path path = Paths.get("test/model/testPatterns/octagon2.rle");
        File f = path.toFile();
        Board result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        Board expResult = new Board();
        byte[][] board = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},};
        expResult.setBoard(board);

        // set game rules to the expected result.
        ArrayList<Integer> expSurvivalRules = new ArrayList<>();
        expSurvivalRules.add(2);
        expSurvivalRules.add(3);
        ArrayList<Integer> expBirthRules = new ArrayList<>();
        expBirthRules.add(3);

        // compare expected result with actual result.
        assertArrayEquals(expResult.getBoard(), result.getBoard());
        assertEquals(expSurvivalRules, rules.getSurviveRules());
        assertEquals(expBirthRules, rules.getBirthRules());
    }

    @Test // requires an internet connection to succeed. 
    public void testReadGameBoardFromUrl() throws Exception {
        System.out.println("readGameBoardFromUrl");

        // Board Test nr. 1 of 2.
        // read the file used for testing
        String url = "http://www.conwaylife.com/patterns/octagon2.rle";
        FileImporter instance = new FileImporter();
        instance.setPadding(0);
        Board result = instance.readGameBoardFromUrl(url);

        // create a board object representing the expected result.
        Board expResult = new Board();
        byte[][] board = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},};
        expResult.setBoard(board);

        // set game rules to the expected result.
        ArrayList<Integer> expSurvivalRules = new ArrayList<>();
        expSurvivalRules.add(2);
        expSurvivalRules.add(3);
        ArrayList<Integer> expBirthRules = new ArrayList<>();
        expBirthRules.add(3);

        // compare expected result with actual result.
        assertArrayEquals(expResult.getBoard(), result.getBoard());
        assertEquals(expSurvivalRules, rules.getSurviveRules());
        assertEquals(expBirthRules, rules.getBirthRules());

        // Board Test nr. 2 of 2.
        // read the file used for testing
        FileImporter instance2 = new FileImporter();
        instance2.setPadding(0);
        String url2 = "http://www.conwaylife.com/patterns/blinker.rle";
        Board result2 = instance2.readGameBoardFromUrl(url2);

        // create a board object representing the expected result.
        Board expResult2 = new Board();
        byte[][] board2 = {
            {1, 1, 1}
        };
        expResult2.setBoard(board2);

        // set game rules to the expected result.
        ArrayList<Integer> expSurvivalRules2 = new ArrayList<>();
        expSurvivalRules2.add(2);
        expSurvivalRules2.add(3);
        ArrayList<Integer> expBirthRules2 = new ArrayList<>();
        expBirthRules2.add(3);

        // compare expected result with actual result.
        assertArrayEquals(expResult2.getBoard(), result2.getBoard());
        assertEquals(expSurvivalRules2, rules.getSurviveRules());
        assertEquals(expBirthRules2, rules.getBirthRules());
    }

}

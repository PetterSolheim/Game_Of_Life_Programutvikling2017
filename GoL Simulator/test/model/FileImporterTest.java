package model;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
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
    public void testRledFromDisk() throws Exception {
        System.out.println("Read RLE file from disk.");

        // RLE file test.
        FileImporter instance = new FileImporter();

        // alter rules for test
        Rules rules = Rules.getInstance();
        rules.setBirthRules(0);
        rules.setSurviveRules(0);
        Path path = Paths.get("test/model/testPatterns/RLE/octagon2.rle");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        byte[][] boardPattern = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0}
        };
        expResult.setBoard(boardPattern);

        // set game rules to the expected result.
        ArrayList<Integer> expSurvivalRules = new ArrayList<>();
        expSurvivalRules.add(2);
        expSurvivalRules.add(3);
        ArrayList<Integer> expBirthRules = new ArrayList<>();
        expBirthRules.add(3);

        // compare expected result with actual result.
        assertEquals(expResult.getBoard(), result.getBoard());
        assertEquals(expSurvivalRules, rules.getSurviveRules());
        assertEquals(expBirthRules, rules.getBirthRules());
    }

    @Test
    public void testReadPlainTextFromDisk() throws Exception {
        System.out.println("Testing PlainText pattern file.");

        // cells file test.
        FileImporter instance = new FileImporter();
        Path path = Paths.get("test/model/testPatterns/PlainText/testbox.cells");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        byte[][] boardPattern = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        expResult.setBoard(boardPattern);
        assertEquals(expResult.getBoard(), result.getBoard());
    }

    @Test
    public void testReadLife105WithoutRules() throws Exception {
        System.out.println("Testing Life 1.05 file with no rules defined.");

        // alter rules before test
        Rules rules = Rules.getInstance();
        rules.setBirthRules(0);
        rules.setSurviveRules(0);

        // create expected rules
        ArrayList<Integer> expSurvive = new ArrayList<>();
        expSurvive.add(0);
        ArrayList<Integer> expBirth = new ArrayList<>();
        expBirth.add(0);

        // parse Life 1.05 file
        FileImporter instance = new FileImporter();
        Path path = Paths.get("test/model/testPatterns/Life105/testbox.LIF");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        byte[][] boardPattern = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        expResult.setBoard(boardPattern);
        assertEquals(result.getAuthor().trim(), "Test Author");
        assertEquals(result.getName().trim(), "Test Board");
        assertEquals(result.getComment().trim(), "Test Comment");
        assertEquals(expResult.getBoard(), result.getBoard());
        assertEquals(rules.getBirthRules(), expBirth);
        assertEquals(rules.getSurviveRules(), expSurvive);
    }

    @Test
    public void testReadLife105WithRules() throws Exception {
        System.out.println("Testing Life 1.05 file with S12/5 rules.");

        // alter rules before test
        Rules rules = Rules.getInstance();
        rules.setBirthRules(0);
        rules.setSurviveRules(0);

        // create expected rules
        ArrayList<Integer> expSurvive = new ArrayList<>();
        expSurvive.add(1);
        expSurvive.add(2);
        ArrayList<Integer> expBirth = new ArrayList<>();
        expBirth.add(5);

        // parse Life 1.05 file
        FileImporter instance = new FileImporter();
        Path path = Paths.get("test/model/testPatterns/Life105/testboxS12B5.LIF");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        byte[][] boardPattern = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        expResult.setBoard(boardPattern);
        assertEquals(expResult.getBoard(), result.getBoard());
        assertEquals(rules.getBirthRules(), expBirth);
        assertEquals(rules.getSurviveRules(), expSurvive);
    }

    @Test // requires an internet connection to succeed. 
    public void testReadGameBoardFromUrl() throws Exception {
        System.out.println("readGameBoardFromUrl");

        // RLE tests
        // Board Test nr. 1 of 2.
        // read the file used for testing
        String url = "http://www.conwaylife.com/patterns/octagon2.rle";
        FileImporter instance = new FileImporter();
        BoardDynamic result = new BoardDynamic();
        result = instance.readGameBoardFromUrl(url);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
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
        assertEquals(expResult.getBoard(), result.getBoard());
        assertEquals(expSurvivalRules, rules.getSurviveRules());
        assertEquals(expBirthRules, rules.getBirthRules());

        // Board Test nr. 2 of 2.
        // read the file used for testing
        FileImporter instance2 = new FileImporter();
        String url2 = "http://www.conwaylife.com/patterns/blinker.rle";
        BoardDynamic result2 = new BoardDynamic();
        result2 = instance2.readGameBoardFromUrl(url2);

        // create a board object representing the expected result.
        BoardDynamic expResult2 = new BoardDynamic();
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
        assertEquals(expResult2.getBoard(), result2.getBoard());
        assertEquals(expSurvivalRules2, rules.getSurviveRules());
        assertEquals(expBirthRules2, rules.getBirthRules());
    }

    // test file with unsuported character
    @Test
    public void testFileWithInvalidCharacter() {
        Throwable caught = null;
        FileImporter instance = new FileImporter();
        Path path = Paths.get("test/model/testPatterns/RLE/octagon2UnknownCharacter.rle");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        try {
            result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        } catch (IOException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }

    @Test
    public void testEarlyEndOfFile() throws Exception {
        FileImporter instance = new FileImporter();
        Path path = Paths.get("test/model/testPatterns/RLE/octagon2EarlyEOF.rle");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        result = instance.readGameBoardFromDisk(f);
        byte[][] expectedBoard = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
        };
        BoardDynamic expResult = new BoardDynamic();
        expResult.setBoard(expectedBoard);
        assertEquals(result.getBoard(), expResult.getBoard());
    }

    @Test
    public void testNoDimensions() {
        FileImporter instance = new FileImporter();
        Path path = Paths.get("test/model/testPatterns/RLE/octagon2NoDimensions.rle");
        File f = path.toFile();
        BoardDynamic result = new BoardDynamic();
        Throwable caught = null;
        try {
            result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        } catch (IOException e) {
            caught = e;
        }

        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }
}

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
    byte[][] octagon2 = {
        {0, 0, 0, 1, 1, 0, 0, 0},
        {0, 0, 1, 0, 0, 1, 0, 0},
        {0, 1, 0, 0, 0, 0, 1, 0},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {0, 1, 0, 0, 0, 0, 1, 0},
        {0, 0, 1, 0, 0, 1, 0, 0},
        {0, 0, 0, 1, 1, 0, 0, 0}
    };
    byte[][] testBoard = {
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

    @Test
    public void testRledFromDisk() throws Exception {
        System.out.println("Read RLE file from disk.");
        FileImporter instance = new FileImporter();

        // alter rules for test
        rules.setBirthRules(0);
        rules.setSurviveRules(0);

        File f = Paths.get("test/model/testPatterns/RLE/octagon2.rle").toFile();
        BoardDynamic result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        expResult.setBoard(octagon2);

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
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/PlainText/testbox.cells").toFile();

        BoardDynamic result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        expResult.setBoard(testBoard);

        assertEquals(expResult.getBoard(), result.getBoard());
    }

    @Test
    public void testPlainTextInvalidCharacter() throws Exception {
        System.out.println("Testing PlainText file with invalid character.");
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/PlainText/testboxInvalidCharacter.cells").toFile();

        Throwable caught = null;
        try {
            BoardDynamic result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        }

        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }

    @Test
    public void testReadLife106() throws Exception {
        System.out.println("Testing Life 1.06 file.");
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/Life106/testbox.LIF").toFile();

        BoardDynamic result = instance.readGameBoardFromDisk(f);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        byte[][] boardPattern = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1},
            {0, 0, 0},
            {1, 1, 1}
        };
        expResult.setBoard(boardPattern);
        assertEquals(expResult.getBoard(), result.getBoard());
    }

    @Test
    public void testLife106InvalidCharacter() throws Exception {
        System.out.println("Testing Life 1.06 with an illegal character.");
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/Life106/testboxInvalidCharacter.LIF").toFile();

        BoardDynamic result = new BoardDynamic();
        Throwable caught = null;
        try {
            result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        }

        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }

    @Test
    public void testLife106MissingCoordinate() throws Exception {
        System.out.println("Testing Life 1.06 with missing y coordinate.");
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/Life106/testboxMissingYCoordinate.LIF").toFile();

        Throwable caught = null;
        try {
            BoardDynamic result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        }

        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }

    @Test // requires an internet connection to succeed. 
    public void testReadRleFromUrl() throws Exception {
        System.out.println("Testing RLE quired from URL");
        FileImporter instance = new FileImporter();

        // alter game rules
        rules.setBirthRules(0);
        rules.setSurviveRules(0);
        String url = "http://www.conwaylife.com/patterns/octagon2.rle";
        BoardDynamic result = instance.readGameBoardFromUrl(url);

        // create a board object representing the expected result.
        BoardDynamic expResult = new BoardDynamic();
        expResult.setBoard(octagon2);

        // expected result for rules.
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
    public void testRleWithInvalidCharacter() {
        System.out.println("Test RLE file with an invalid character.");
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/RLE/octagon2UnknownCharacter.rle").toFile();

        Throwable caught = null;
        try {
            BoardDynamic result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        } catch (IOException e) {
            caught = e;
        }

        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }

    @Test
    public void testRleWithEarlyEndOfFile() throws Exception {
        System.out.println("Test RLE file with early end of file (!) indicator");
        FileImporter instance = new FileImporter();
        File f = Paths.get("test/model/testPatterns/RLE/octagon2EarlyEOF.rle").toFile();

        BoardDynamic result = instance.readGameBoardFromDisk(f);
        BoardDynamic expResult = new BoardDynamic();
        byte[][] earlyEnding = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},};

        expResult.setBoard(earlyEnding);
        assertEquals(expResult.getBoard(), result.getBoard());
    }

    @Test
    public void testRleNoDimensions() {
        System.out.println("Testing RLE without board dimensions defined.");
        FileImporter instance = new FileImporter();

        File f = Paths.get("test/model/testPatterns/RLE/octagon2NoDimensions.rle").toFile();
        Throwable caught = null;
        try {
            BoardDynamic result = instance.readGameBoardFromDisk(f);
        } catch (PatternFormatException e) {
            caught = e;
        } catch (IOException e) {
            caught = e;
        }

        assertNotNull(caught);
        assertSame(PatternFormatException.class, caught.getClass());
    }
}

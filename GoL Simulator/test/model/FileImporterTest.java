/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aleks
 */
public class FileImporterTest {

    @Test
    public void testReadGameBoardFromDisk() throws Exception {
        System.out.println("readGameBoardFromDisk");
        Path path = Paths.get("test/model/testPatterns/octagon2.rle");
        File f = path.toFile();
        FileImporter instance = new FileImporter();
        Board result = instance.readGameBoardFromDisk(f);
        instance.setPadding(0);
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
        expResult.setBirthRules(3);
        expResult.setSurviveRules(2, 3);
        assertArrayEquals(expResult.getBoard(), result.getBoard());
        assertEquals(expResult.getSurviveRules(), result.getSurviveRules());
        assertEquals(expResult.getBirthRules(), result.getBirthRules());
    }

    @Test
    public void testReadGameBoardFromUrl() throws Exception {
        System.out.println("readGameBoardFromUrl");
        String url = "http://www.conwaylife.com/patterns/octagon2.rle";
        FileImporter instance = new FileImporter();
        instance.setPadding(0);
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
        expResult.setBirthRules(3);
        expResult.setSurviveRules(2, 3);
        Board result = instance.readGameBoardFromUrl(url);
        assertArrayEquals(expResult.getBoard(), result.getBoard());
        assertEquals(expResult.getSurviveRules(), result.getSurviveRules());
        assertEquals(expResult.getBirthRules(), result.getBirthRules());

        String url2 = "http://www.conwaylife.com/patterns/blinker.rle";
        Board expResult2 = new Board();
        byte[][] board2 = {
            {1, 1, 1}
        };
        expResult2.setBoard(board2);
        expResult2.setBirthRules(3);
        expResult2.setSurviveRules(2, 3);
        Board result2 = instance.readGameBoardFromUrl(url2);
        assertArrayEquals(expResult2.getBoard(), result2.getBoard());
        assertEquals(expResult2.getSurviveRules(), result2.getSurviveRules());
        assertEquals(expResult2.getBirthRules(), result2.getBirthRules());
    }

}

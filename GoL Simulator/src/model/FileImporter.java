/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aleks
 */
public class FileImporter {

    private Board board;
    private Rules rules;
    private byte[][] boardArray;
    private int row;
    private int col;
    private int[] survivalRules;
    private int[] birthRules;

    public FileImporter() {
        board = new Board();
        rules = Rules.getInstance();
    }

    public Board readGameBoardFromDisk(File f) throws IOException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension); // parse the input file
        return board;
    }

    public Board readGameBoardFromUrl(String url) throws IOException {
        String fileExtension = getFileExtension(url);
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readGameBoard(new InputStreamReader(conn.getInputStream()), fileExtension);
        return board;
    }

    private void readGameBoard(Reader r, String fileExtension) {

        switch (fileExtension) {
            case "rle":
                rleReader(r);
                break;
            case "life":
                System.out.println("Not yet supported");
                break;
            case "lif":
                System.out.println("Not yet supported");
                break;
            case "cells":
                System.out.println("Not yet supported");
                break;
            default:
                System.out.println("Unsuported file format");
        }

        boardArray = addPadding(boardArray);
        board.setBoard(boardArray);
        rules.setBirthRules(birthRules);
        rules.setSurviveRules(survivalRules);

    }

    private void rleReader(Reader r) {
        BufferedReader bufferedReader = new BufferedReader(r);
        String line = null;
        String[] boardStringArray = null;
        Matcher m;

        Pattern rulePattern = Pattern.compile("rule{1}\\s*=\\s*(?:b|B)?(\\d+)/(?:s|S)?(\\d+)");
        Pattern colPattern = Pattern.compile("(?:x|X)\\s=\\s*(\\d+)");
        Pattern rowPattern = Pattern.compile("(?:y|Y)\\s=\\s*(\\d+)");

        StringBuilder boardStringBuilder = new StringBuilder();
        boolean headerParsed = false;
        boolean endOfFile = false;

        try {
            while (!endOfFile) {
                while (!headerParsed) {
                    line = bufferedReader.readLine();
                    if (!line.startsWith("#")) { // skip any lines that are comments

                        // parse the header
                        while (!headerParsed) {
                            // determin number of rows for board
                            m = rowPattern.matcher(line);
                            while (m.find()) {
                                row = Integer.parseInt(m.group(1));
                                System.out.println("Rows: " + row);
                            }

                            // determin number of cols for board
                            m = colPattern.matcher(line);
                            while (m.find()) {
                                col = Integer.parseInt(m.group(1));
                                System.out.println("Cols: " + col);
                            }

                            boardArray = new byte[row][col];

                            // determin ruleset for board
                            m = rulePattern.matcher(line);
                            while (m.find()) {

                                // determin rules for birth
                                String birth = m.group(1);
                                String[] birthStringArray = birth.split("");
                                birthRules = new int[birthStringArray.length];
                                for (int i = 0; i < birthStringArray.length; i++) {
                                    birthRules[i] = Integer.parseInt(birthStringArray[i]);
                                }

                                // determin rules for survival
                                String survive = m.group(2);
                                String[] surviveStringArray = survive.split("");
                                survivalRules = new int[surviveStringArray.length];
                                for (int i = 0; i < surviveStringArray.length; i++) {
                                    survivalRules[i] = Integer.parseInt(surviveStringArray[i]);
                                }
                            }
                            headerParsed = true;
                        }
                    }

                    // prepare the board for parsing
                    if (headerParsed && !endOfFile) {
                        while (!endOfFile) {
                            line = bufferedReader.readLine();

                            boardStringBuilder.append(line);

                            if (line.indexOf('!') != -1) {
                                endOfFile = true;
                            }
                        }
                    }

                    // split the board definition into an array of Strings,
                    // where each String in the array represents a row on the board.
                    if (headerParsed && endOfFile) {
                        String boardString = boardStringBuilder.toString();
                        boardStringArray = boardString.split("\\$");
                    }

                }
            }

            int rowOffsett = 0;
            for (int i = 0; i < boardStringArray.length; i++) {
                int cellPosition = 0;
                Pattern boardRowPattern = Pattern.compile("(\\d*)(b|o){1}");
                m = boardRowPattern.matcher(boardStringArray[i]);
                m.find();

                while (!m.hitEnd()) {
                    int numberOfCells;
                    if (!m.group(1).isEmpty()) {
                        numberOfCells = Integer.parseInt(m.group(1));
                    } else {
                        numberOfCells = 1;
                    }

                    for (int j = cellPosition; j < cellPosition + numberOfCells; j++) {
                        if (m.group(2).equals("o")) {
                            boardArray[i + rowOffsett][j] = 1;
                        } else if (m.group(2).equals("b")) {
                            boardArray[i + rowOffsett][j] = 0;
                        }

                    }
                    cellPosition += numberOfCells;
                    m.find();
                }
                Pattern blancLinesPattern = Pattern.compile("(\\d)*\\s*(?!.)");
                m = blancLinesPattern.matcher(boardStringArray[i]);
                m.find();
                if (!m.group().isEmpty()) {
                    int blancLines = Integer.parseInt(m.group()) - 1;
                    int counter = 1;

                    while (counter <= blancLines) {
                        for (int j = 0; j < boardArray[0].length; j++) {
                            boardArray[i + rowOffsett + counter][j] = 0;
                        }
                        counter++;
                    }
                    rowOffsett += blancLines;
                }
            }
        } catch (Exception e) {
        }
    }

    private String getFileExtension(File f) {
        int i = f.getName().lastIndexOf(".");
        String fileExtension = f.getName().substring(i + 1);
        return fileExtension;
    }

    private String getFileExtension(String s) {
        int i = s.lastIndexOf(".");
        String fileExtension = s.substring(i + 1);
        return fileExtension;
    }

    public byte[][] addPadding(byte[][] input) {
        byte[][] paddedBoard = new byte[input.length + 20][input[0].length + 20];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                paddedBoard[i + 10][j + 10] = input[i][j];
            }
        }
        return paddedBoard;
    }

}

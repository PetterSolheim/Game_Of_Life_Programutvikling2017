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

    private byte[][] board;
    private int row;
    private int col;
    int survive = 0;
    int birth = 0;

    public byte[][] readGameBoardFromDisk(File f) throws IOException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension); // parse the input file
        return board;
    }

    public byte[][] readGameBoardFromUrl(String url) throws IOException {
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
    }

    private void rleReader(Reader r) {
        BufferedReader bufferedReader = new BufferedReader(r);
        String line = null;
        String[] boardStringArray = null;
        Matcher m;

        Pattern colPattern = Pattern.compile("(?:x|X)\\s=\\s*(\\d+)");

        Pattern rowPattern = Pattern.compile("(?:y|Y)\\s=\\s*(\\d+)");

        Pattern rulePattern = Pattern.compile("rule{1}\\s*=\\s*B?(\\d+)/S?(\\d+)");

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

                            board = new byte[row][col];

                            // determin ruleset for board
                            m = rulePattern.matcher(line);
                            while (m.find()) {
                                birth = Integer.parseInt(m.group(1));
                                survive = Integer.parseInt(m.group(2));
                                System.out.println("Birth: " + birth);
                                System.out.println("Survive: " + survive);
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

            for (int i = 0; i < boardStringArray.length; i++) {
                int cellPosition = 0;
                Pattern boardRowPattern = Pattern.compile("(\\d*)(b|o){1}");
                m = rulePattern.matcher(line);
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
                            board[i][j] = 1;
                        } else if (m.group(2).equals("b")) {
                            board[i][j] = 0;
                        }

                    }
                    cellPosition += numberOfCells;
                    m.find();
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

}

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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aleks
 */
public class FileImporter {

    private Board board;
    private byte[][] boardArray;
    private int row;
    private int col;
    private int[] survivalRules;
    private int[] birthRules;

    public FileImporter() {
        board = new Board();
    }

    public Board readGameBoardFromDisk(File f) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension); // parse the input file
        return board;
    }

    public Board readGameBoardFromUrl(String url) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(url);
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readGameBoard(new InputStreamReader(conn.getInputStream()), fileExtension);
        return board;
    }

    private void readGameBoard(Reader r, String fileExtension) throws PatternFormatException {
        try {
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
            }

            boardArray = addPadding(boardArray);
            board.setBoard(boardArray);
            board.setBirthRules(birthRules);
            board.setSurviveRules(survivalRules);
        } catch (IOException e) {
        }
    }

    private void rleReader(Reader r) throws IOException, PatternFormatException {
        BufferedReader br = new BufferedReader(r);
        String line = null;
        boolean endOfFile = false;
        ArrayList<String> lineList = new ArrayList<>();
        while (!endOfFile) {
            line = br.readLine();
            if (line != null) {
                lineList.add(line);
            } else {
                endOfFile = true;
            }
        }
        readRleComments(lineList);
        readRleBoardSize(lineList);
        readRleRules(lineList);
        readRleBoard(lineList);
    }

    private void readRleComments(ArrayList<String> lineList) {
        Matcher m;
        Pattern commentPattern = Pattern.compile("(#.*)");
        for (int i = 0; i < lineList.size(); i++) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    System.out.println("Comment: " + m.group(1));
                }
            }
        }
    }

    private void readRleBoardSize(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        Pattern colPattern = Pattern.compile("(?:x|X)\\s=\\s*(\\d+)");
        Pattern rowPattern = Pattern.compile("(?:y|Y)\\s=\\s*(\\d+)");
        boolean boardRowsSet = false;
        boolean boardColumnsSet = false;

        // determin board size
        for (int i = 0; i < lineList.size(); i++) {
            if (!lineList.get(i).startsWith("#")) {
                m = colPattern.matcher(lineList.get(i));
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        col = Integer.parseInt(m.group(1));
                        boardColumnsSet = true;
                    }
                }

                m = rowPattern.matcher(lineList.get(i));
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        row = Integer.parseInt(m.group(1));
                        boardRowsSet = true;
                    }
                }
            }
        }
        if (!boardColumnsSet && !boardRowsSet) {
            throw new PatternFormatException("No dimensions defined by RLE-file.");
        } else if (!boardColumnsSet) {
            throw new PatternFormatException("No width defined by RLE-file.");
        } else if (!boardRowsSet) {
            throw new PatternFormatException("No height defined by RLE-file.");
        }

        boardArray = new byte[row][col];
        System.out.println("Rows: " + boardArray.length + " Columns: " + boardArray[0].length);
    }

    private void readRleRules(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        Pattern rulePattern = Pattern.compile("rule{1}\\s*=\\s*(b|B|s|S)?(\\d+)/(b|B|s|S)?(\\d+)");
        boolean boardRulesSet = false;
        String survive;
        String birth;

        for (int i = 0; i < lineList.size(); i++) {
            if (!lineList.get(i).startsWith("#")) {
                m = rulePattern.matcher(lineList.get(i));
                if (m.find()) {
                    // determin rules for survival
                    if (!m.group(1).isEmpty() && !m.group(2).isEmpty() && !m.group(3).isEmpty() && !m.group(4).isEmpty()) {
                        if (m.group(1).equals("s") || m.group(1).equals("S")) {
                            survive = m.group(2);
                        } else {
                            survive = m.group(4);
                        }

                        if (m.group(1).equals("b") || m.group(1).equals("B")) {
                            birth = m.group(2);
                        } else {
                            birth = m.group(4);
                        }

                        System.out.println(survive + " " + birth);

                        String[] surviveStringArray = survive.split("");
                        survivalRules = new int[surviveStringArray.length];
                        for (int j = 0; j < surviveStringArray.length; j++) {
                            survivalRules[j] = Integer.parseInt(surviveStringArray[j]);
                        }

                        String[] birthStringArray = birth.split("");
                        birthRules = new int[birthStringArray.length];
                        for (int j = 0; j < birthStringArray.length; j++) {
                            birthRules[j] = Integer.parseInt(birthStringArray[j]);
                        }
                    }
                } else {
                    throw new PatternFormatException();
                }
            }
        }
    }

    private void readRleBoard(ArrayList<String> lineList) throws IOException, PatternFormatException {
        Matcher m;
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < lineList.size(); i++) {
            boardString.append(lineList.get(i));
        }
        String[] boardStringArray = boardString.toString().split("\\$");

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

                try {
                    for (int j = cellPosition; j < cellPosition + numberOfCells; j++) {
                        if (m.group(2).equals("o")) {
                            boardArray[i + rowOffsett][j] = 1;
                        } else if (m.group(2).equals("b")) {
                            boardArray[i + rowOffsett][j] = 0;
                        }

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new PatternFormatException("Defined board size does not"
                            + " match the board definition!");
                }
                cellPosition += numberOfCells;
                m.find();
            }
            Pattern blankLinesPattern = Pattern.compile("(\\d)*\\s*(?!.)");
            m = blankLinesPattern.matcher(boardStringArray[i]);
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
        byte[][] paddedBoard = new byte[input.length + 100][input[0].length + 100];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                paddedBoard[i + 50][j + 50] = input[i][j];
            }
        }
        return paddedBoard;
    }

}

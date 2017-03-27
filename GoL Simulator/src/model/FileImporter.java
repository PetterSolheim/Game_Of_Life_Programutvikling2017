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
import view.DialogBoxes;

/**
 * Class for translating external files, such as RLE files, to a board object.
 * Class has been created so as to be easily extended to support new formats.
 * @author aleks
 */
public class FileImporter {

    private Board board;
    private byte[][] boardArray;
    private int row;
    private int col;
    private int padding = 20;
    private int[] survivalRules;
    private int[] birthRules;

    /**
     * File importers constructor.
     */
    public FileImporter() {
        board = new Board();
    }

    /**
     * Parses a file from disk, and returns a Board object based on this.
     * @param f
     * @return finished Board object.
     * @throws IOException
     * @throws PatternFormatException 
     */
    public Board readGameBoardFromDisk(File f) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension); // parse the input file
        return board;
    }

    /**
     * Parses a file from a given URL, and returns a Board object based on this.
     * @param url A Stream representing an URL.
     * @return finished Board object
     * @throws IOException
     * @throws PatternFormatException 
     */
    public Board readGameBoardFromUrl(String url) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(url);
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readGameBoard(new InputStreamReader(conn.getInputStream()), fileExtension);
        return board;
    }

    /**
     * Helper method. Passes the file to the correct parses based on file type,
     * and builds the actual board.
     * @param r
     * @param fileExtension
     * @throws PatternFormatException 
     */
    private void readGameBoard(Reader r, String fileExtension) throws PatternFormatException, IOException {
            switch (fileExtension) {
                case "rle":
                    rleReader(r);
                    break;
                case "life":
                    throw new PatternFormatException("Unsuported file type");
                case "lif":
                    throw new PatternFormatException("Unsuported file type");
                case "cells":
                    throw new PatternFormatException("Unsuported file type");
                default:
                    throw new PatternFormatException("Unsuported file type");
        }

            // build the board.
            boardArray = addPadding(boardArray);
            board.setBoard(boardArray);
            board.setBirthRules(birthRules);
            board.setSurviveRules(survivalRules);
    }

    /**
     * Parser for RLE files.
     * @param r
     * @throws IOException
     * @throws PatternFormatException 
     */
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

    /**
     * Helper method for parsing comments in RLE file. Currently, they are 
     * merely discarded.
     * @param lineList 
     */
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

        for (int i = lineList.size() - 1; i >= 0; i--) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    lineList.remove(i);
                }
            }
        }
    }

    /**
     * Helper method to detect the board size in an RLE file.
     * @param lineList
     * @throws PatternFormatException 
     */
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

    /**
     * Helper method for detecting game rules in RLE file.
     * @param lineList
     * @throws PatternFormatException 
     */
    private void readRleRules(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        Pattern rulePattern = Pattern.compile("rule{1}\\s*=\\s*(b|B|s|S)?(\\d+)/(b|B|s|S)?(\\d+)");
        boolean boardRulesSet = false;
        String survive;
        String birth;

        for (int i = 0; i < lineList.size(); i++) {

            m = rulePattern.matcher(lineList.get(i));
            if (m.find()) {
                // determin rules for survival
                if (m.group(1) != null && m.group(3) != null) {
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
                    boardRulesSet = true;
                    lineList.remove(i);
                } else if (m.group(2) != null && m.group(4) != null) {
                    survive = m.group(2);
                    birth = m.group(4);
                    boardRulesSet = true;
                    lineList.remove(i);
                } else {
                    throw new PatternFormatException("No rulesett defined!");
                }

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
        }

        if (!boardRulesSet) {
            throw new PatternFormatException("Rule definition in RLE file is not valid!");
        }
    }

    /**
     * Helper method for parsing the board from RLE file.
     * @param lineList
     * @throws IOException
     * @throws PatternFormatException 
     */
    private void readRleBoard(ArrayList<String> lineList) throws IOException, PatternFormatException {
        Matcher m;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lineList.size(); i++) {
            if (lineList.get(i).matches("^(?:\\d*[bo\\$\\!]{1})*$")) {
                stringBuilder.append(lineList.get(i));
            } else {
                throw new PatternFormatException("Invalid character found in board definition.");
            }
        }
        String readString = stringBuilder.toString().trim();
        
        //ensure file contains end of file indicator.
        if(!readString.contains("!")) {
            throw new PatternFormatException("No end of file character (!) found.");
        }
            
        String[] boardStringArray = readString.split("\\$");

        int rowOffsett = 0;
        boolean endOfFile = false;

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
                        } else {
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

    /**
     * Helper method for determining the file extension of a File object.
     * @param f
     * @return 
     */
    private String getFileExtension(File f) {
        int i = f.getName().lastIndexOf(".");
        String fileExtension = f.getName().substring(i + 1);
        return fileExtension;
    }

    /**
     * Helper method for determining the file extension of a file acquired from
     * an URL.
     * @param s
     * @return 
     */
    private String getFileExtension(String s) {
        int i = s.lastIndexOf(".");
        String fileExtension = s.substring(i + 1);
        return fileExtension;
    }

    /**
     * Helper method which adds padding round a opened Board to give the board
     * "room to breath".
     * @param input
     * @return 
     */
    private byte[][] addPadding(byte[][] input) {
        byte[][] paddedBoard = new byte[input.length + padding*2][input[0].length + padding*2];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                paddedBoard[i + (padding)][j + (padding)] = input[i][j];
            }
        }
        return paddedBoard;
    }
    
    public void setPadding(int padding) {
        this.padding = padding;
    }

}

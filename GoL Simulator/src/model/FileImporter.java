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
 * Class for converting external Game of Life pattern files to a byte[][] array
 * and loading of game rules. Class currently supports reading of RLE files, but
 * has been written so as to be easily extendable to support additional formats.
 */
public class FileImporter {

    private byte[][] boardArray;
    private Rules rules = Rules.getInstance();

    /**
     * Reads a pattern file from disk and parses it. Returns a byte[][] array
     * containing the pattern, and sets the rules specified by the pattern file.
     *
     * @param f the file object containing the pattern to be read.
     * @return a <code>byte[][]</code> containing the parsed pattern.
     * @throws IOException if there are errors reading the file.
     * @throws PatternFormatException if pattern could not be parsed.
     */
    public byte[][] readGameBoardFromDisk(File f) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension);
        return boardArray;
    }

    /**
     * Reads a pattern file from an URL and parses it. Returns a byte[][] array
     * containing the pattern, and sets the rules specified by the pattern file.
     *
     * @param url a <code>String</code> containing the URL of the pattern file.
     * @return a <code>byte[][]</code> containing the parsed pattern.
     * @throws IOException if there are errors reading the file.
     * @throws PatternFormatException if the pattern could not be parsed.
     */
    public byte[][] readGameBoardFromUrl(String url) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(url); // determin the filetype
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readGameBoard(new InputStreamReader(conn.getInputStream()), fileExtension);
        return boardArray;
    }

    /**
     * Pass the pattern file to the appropriate parser based on file type
     * extension.
     *
     * @param r
     * @param fileExtension the extension of the pattern file (for instance RLE)
     * @throws IOException if there are errors reading the file.
     * @throws PatternFormatException if the pattern could not be parsed.
     */
    private void readGameBoard(Reader r, String fileExtension) throws PatternFormatException, IOException {
        switch (fileExtension) {
            case "rle":
                rleReader(r);
                break;
            case "life":
                throw new PatternFormatException("LIFE files are not currently"
                        + "supported.");
            case "lif":
                throw new PatternFormatException("LIF files are not currently"
                        + "supported.");
            case "cells":
                throw new PatternFormatException("MCELL files are not currently"
                        + "supported.");
            default:
                throw new PatternFormatException("Unrecognized file type.");
        }

    }

    /**
     * RLE file parser.
     *
     * @param r
     * @throws IOException if there are errors reading the file.
     * @throws PatternFormatException if the pattern could not be parsed.
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
     * Extracts the comments from an RLE file. Comments are not currently used
     * for anything.
     *
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
     * Extracts the size of the board being parsed.
     *
     * @param lineList
     * @throws PatternFormatException if board dimensions are not defined, or if
     * parser is unable to interpret the size.
     */
    private void readRleBoardSize(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        Pattern colPattern = Pattern.compile("(?:x|X)\\s=\\s*(\\d+)");
        Pattern rowPattern = Pattern.compile("(?:y|Y)\\s=\\s*(\\d+)");
        int row = 0;
        int col = 0;

        // determin board size
        for (int i = 0; i < lineList.size(); i++) {
            if (!lineList.get(i).startsWith("#")) {
                m = colPattern.matcher(lineList.get(i));
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        col = Integer.parseInt(m.group(1));
                    }
                }

                m = rowPattern.matcher(lineList.get(i));
                if (m.find()) {
                    if (!m.group(1).isEmpty()) {
                        row = Integer.parseInt(m.group(1));
                    }
                }
            }
        }
        if (col < 1 && row < 1) {
            throw new PatternFormatException("No dimensions defined by RLE-file.");
        } else if (col < 1) {
            throw new PatternFormatException("No width defined by RLE-file.");
        } else if (row < 1) {
            throw new PatternFormatException("No height defined by RLE-file.");
        }

        boardArray = new byte[row][col];
        System.out.println("Rows: " + boardArray.length + " Columns: " + boardArray[0].length);
    }

    /**
     * Extracts the rules defined for the pattern.
     *
     * @param lineList
     * @throws PatternFormatException if no rules are defined.
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
                int[] survivalRules;
                survivalRules = new int[surviveStringArray.length];
                for (int j = 0; j < surviveStringArray.length; j++) {
                    survivalRules[j] = Integer.parseInt(surviveStringArray[j]);
                }

                rules.setSurviveRules(survivalRules);

                String[] birthStringArray = birth.split("");
                int[] birthRules;
                birthRules = new int[birthStringArray.length];
                for (int j = 0; j < birthStringArray.length; j++) {
                    birthRules[j] = Integer.parseInt(birthStringArray[j]);
                }

                rules.setBirthRules(birthRules);
            }
        }

        if (!boardRulesSet) {
            throw new PatternFormatException("Rule definition in RLE file is not valid!");
        }
    }

    /**
     * Creates a byte[][] array containing the defined board.
     *
     * @param lineList
     * @throws PatternFormatException if; unrecognized character in pattern, no
     * end of file indicator found or the pattern size does not match the size
     * defined by the pattern file.
     */
    private void readRleBoard(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lineList.size(); i++) {
            if (lineList.get(i).matches("^(?:\\d*[bo\\$\\!]{1})*$")) {
                stringBuilder.append(lineList.get(i));
            } else {
                throw new PatternFormatException("Unsuported character found in "
                        + "board definition.");
            }
        }
        String readString = stringBuilder.toString().trim();

        //ensure file contains end of file indicator.
        if (!readString.contains("!")) {
            throw new PatternFormatException("No end of file character (!) "
                    + "found.");
        }

        String[] boardStringArray = readString.split("\\$");

        int rowOffsett = 0;
        boolean endOfFile = false;

        for (int i = 0; i < boardStringArray.length; i++) {
            int cellPosition = 0;
            Pattern boardRowPattern = Pattern.compile("(\\d*)(b|o|!){1}");
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
                        if (m.group(2).equals("o") || m.group(2).equals("O")) {
                            boardArray[i + rowOffsett][j] = 1;
                        } else if (m.group(2).equals("b") || m.group(2).equals("B")) {
                            boardArray[i + rowOffsett][j] = 0;
                        } else if (m.group(2).equals("!")) {
                            i = (boardStringArray.length - 1);
                        } else {
                            throw new PatternFormatException("Unsuported "
                                    + "character found in board definition. "
                                    + "This application only supports two cell "
                                    + "states. Dead (symbolized by b or B and "
                                    + "alive, symbolized by o or O. Character "
                                    + "found was" + m.group(2));
                        }

                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new PatternFormatException("Board size defined by the"
                            + "RLE file is to small to accomodate the board"
                            + "described by the RLE file.");
                }
                cellPosition += numberOfCells;
                m.find();
            }

            // Handle blank lines
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
     * Takes a File object and determines its filet type extension.
     *
     * @param f the file for which one want to determine the extensions type.
     * @return a <code>String</code> specifying the file extension type.
     */
    private String getFileExtension(File f) {
        int i = f.getName().lastIndexOf(".");
        String fileExtension = f.getName().substring(i + 1);
        return fileExtension;
    }

    /**
     * Helper method for determining the file extension of a file acquired from
     * an URL.
     *
     * @param s the file for which one wants to determine the extension type.
     * @return a <code>String</code> specifying the file extension type.
     */
    private String getFileExtension(String s) {
        int i = s.lastIndexOf(".");
        String fileExtension = s.substring(i + 1);
        return fileExtension;
    }
}

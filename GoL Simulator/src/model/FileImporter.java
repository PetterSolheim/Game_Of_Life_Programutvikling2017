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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for converting external Game of Life pattern files to a byte[][] array
 * and loading of game rules. Class currently supports reading of RLE files, but
 * has been written so as to be easily extendable to support additional formats.
 */
public class FileImporter {

    private BoardDynamic board = new BoardDynamic();
    private byte[][] boardArray;
    private String author = "unknown";
    private String name = "unknown";
    private String comment = "";
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
    public BoardDynamic readGameBoardFromDisk(File f) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension);
        return board;
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
    public BoardDynamic readGameBoardFromUrl(String url) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(url); // determin the filetype
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readGameBoard(new InputStreamReader(conn.getInputStream()), fileExtension);
        return board;
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
        switch (fileExtension.toLowerCase()) {
            case "rle":
                rleReader(r);
                break;
            case "cells":
                cellsReader(r);
                break;
            case "lif":
                lifReader(r);
                break;
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
        board.setBoard(boardArray);
        board.setMetadata(author, name, comment);
    }

    /**
     * Extracts the comments from an RLE file. Comments are not currently used
     * for anything.
     *
     * @param lineList
     */
    private void readRleComments(ArrayList<String> lineList) {
        Matcher m;
        ArrayList<String> comments = new ArrayList<>();
        Pattern commentPattern = Pattern.compile("(#.*)");
        for (int i = 0; i < lineList.size(); i++) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    comments.add(m.group(1));
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

        // get name of game board
        commentPattern = Pattern.compile("([#]N)(.+)");
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                name = m.group(2);
                i = comments.size();
            }
        }

        // get author name
        commentPattern = Pattern.compile("([#]O)(.+)");
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                author = m.group(2);
                i = comments.size();
            }
        }

        // get comments
        commentPattern = Pattern.compile("([#]C)(.+)");
        StringBuilder commentStringBuilder = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                commentStringBuilder.append(m.group(2));
                commentStringBuilder.append("\n");
            }
        }
        comment = commentStringBuilder.toString();
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
     * @throws PatternFormatException if unrecognized character in pattern, no
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

    private void lifReader(Reader r) throws IOException, PatternFormatException {
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

        Matcher m;
        boolean detectedFileType = false;

        Pattern fileType105 = Pattern.compile("(#Life 1.05)");
        m = fileType105.matcher(lineList.get(0));
        if (m.find()) {
            if (!m.group(1).isEmpty()) {
                detectedFileType = true;
                lif105Reader(lineList);
            }
        }

        if (!detectedFileType) {
            Pattern fileType106 = Pattern.compile("(#Life 1.06)");
            m = fileType106.matcher(lineList.get(0));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    detectedFileType = true;
                    lif106Reader(lineList);
                }
            }
        }

        if (!detectedFileType) {
            throw new PatternFormatException("Lif file does not specify file"
                    + "type. Unable to read file.");
        }
    }

    private void lif106Reader(ArrayList<String> lineList) throws PatternFormatException {
        clearLif106Comments(lineList);
        int[] startingLocation = readLif106Size(lineList);
        readLif106Board(lineList, startingLocation);
        board.setBoard(boardArray);
    }

    private void clearLif106Comments(ArrayList<String> lineList) {
        Matcher m;
        Pattern commentPattern = Pattern.compile("(#)");

        for (int i = 0; i < lineList.size(); i++) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                lineList.remove(i);
                i--; // indexes have shifted. Compensate.
            }
        }
    }

    private int[] readLif106Size(ArrayList<String> lineList) throws PatternFormatException {
        // find lowest and highest point for x and y, and calculate board size
        // based on this.
        Matcher m;
        Pattern coordinates = Pattern.compile("(-?[0-9]+)\\s(-?[0-9]+)");
        int capturedX;
        int capturedY;
        int lowestX = 0;
        int highestX = 0;
        int lowestY = 0;
        int highestY = 0;

        for (int i = 0; i < lineList.size(); i++) {
            m = coordinates.matcher(lineList.get(i));
            if (m.find()) {
                if (!m.group(1).isEmpty() && !m.group(2).isEmpty()) {
                    try {
                        capturedX = Integer.parseInt(m.group(1));
                        capturedY = Integer.parseInt(m.group(2));
                    } catch (NumberFormatException e) {
                        throw new PatternFormatException("Unable to interpret"
                                + "cell coordinates.");
                    }
                    if (capturedX > highestX) {
                        highestX = capturedX;
                    }
                    if (capturedX < lowestX) {
                        lowestX = capturedX;
                    }
                    if (capturedY > highestY) {
                        highestY = capturedY;
                    }
                    if (capturedY < lowestY) {
                        lowestY = capturedY;
                    }
                } else {
                    throw new PatternFormatException("Unable to interpret cell"
                            + "coordinates.");
                }
            }
        }
        int[] lowest = {lowestX, lowestY};
        if (lowestX < 0) {
            lowestX--;
        }
        if (lowestY < 0) {
            lowestY--;
        }

        int rows = Math.abs(lowestY) + Math.abs(highestY);
        int cols = Math.abs(lowestX) + Math.abs(highestX);
        boardArray = new byte[rows][cols];
        return lowest;
    }

    private void readLif106Board(ArrayList<String> lineList, int[] lowest) throws PatternFormatException {
        int startX = lowest[0];
        if (startX < 0) {
            startX = startX * -1;
        }
        int startY = lowest[1];
        if (startY < 0) {
            startY = startY * -1;
        }

        Matcher m;
        Pattern coordinates = Pattern.compile("(-?[0-9]+)\\s(-?[0-9]+)");
        int capturedX, capturedY;

        for (int i = 0; i < lineList.size(); i++) {
            m = coordinates.matcher(lineList.get(i));
            if (m.find()) {
                System.out.println("Starting");
                if (!m.group(1).isEmpty() && !m.group(2).isEmpty()) {
                    try {
                        capturedX = Integer.parseInt(m.group(1));
                        capturedY = Integer.parseInt(m.group(2));
                    } catch (NumberFormatException e) {
                        throw new PatternFormatException("Unable to interpret"
                                + "cell coordinates.");
                    }
                    boardArray[capturedY + startY][capturedX + startX] = 1;
                } else {
                    throw new PatternFormatException("Unable to interpret cell"
                            + "coordinates.");
                }
            }
        }
    }

    private void lif105Reader(ArrayList<String> lineList) throws PatternFormatException {
        readLif105Comments(lineList);
        readLif105Rules(lineList);
        readLif105Size(lineList);
        readLif105Board(lineList);
        board.setBoard(boardArray);
        board.setMetadata(author, name, comment);
    }

    private void readLif105Comments(ArrayList<String> lineList) {
        Matcher m;
        ArrayList<String> comments = new ArrayList<>();
        Pattern commentPattern = Pattern.compile("(#D.*)");
        for (int i = 0; i < lineList.size(); i++) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    comments.add(m.group(1));
                }
            }
        }

        // get name of game board
        commentPattern = Pattern.compile("(#D Name:|#D name:)(.+)");
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                if (!m.group(2).isEmpty()) {
                    name = m.group(2).trim();
                    i = comments.size();
                }
            }
        }

        // get author of game board
        commentPattern = Pattern.compile("(#D Author:|#D author:)(.+)");
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                if (!m.group(2).isEmpty()) {
                    author = m.group(2).trim();
                    comments.remove(i);
                    i = comments.size();
                }
            }
        }

        // get comments
        commentPattern = Pattern.compile("(#D)(.+)");
        StringBuilder commentStringBuilder = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                if (!m.group(2).isEmpty()) {
                    commentStringBuilder.append(m.group(2));
                    commentStringBuilder.append("\n");
                }
            }
            this.comment = commentStringBuilder.toString();
        }
    }

    private void readLif105Rules(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        Pattern rulePattern = Pattern.compile("(#N|#R)+\\s*([0-8]*)\\/([0-8]*)");
        for (int i = 0; i < lineList.size(); i++) {
            m = rulePattern.matcher(lineList.get(i));
            if (m.find()) {
                if (m.group(1).equals("#N")) {
                    rules.setSurviveRules(2, 3);
                    rules.setBirthRules(3);
                } else if (m.group(1).equals("#R")) {
                    if (!m.group(2).isEmpty() && !m.group(3).isEmpty()) {
                        try {
                            String survive = m.group(2);
                            String birth = m.group(3);
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

                        } catch (NumberFormatException e) {
                            throw new PatternFormatException("Failed to determin"
                                    + "rules.");
                        }
                    } else {
                        throw new PatternFormatException("Failed to determin rules.");
                    }
                } else {
                    throw new PatternFormatException("Failed to determin rules.");
                }
            }
        }

        Pattern commentPattern = Pattern.compile("(#.*)");
        // clear ArrayList of all comments
        for (int i = 0; i < lineList.size(); i++) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                lineList.remove(i);
                i--; // removal shifted index by negative one, compensate.
            }
        }

    }

    private void readLif105Size(ArrayList<String> lineList) throws PatternFormatException {
        // remove blanc lines
        lineList.removeAll(Arrays.asList(null, ""));

        int rows = lineList.size();
        int cols = 0;

        for (int i = 0; i < lineList.size(); i++) {
            String lineLength = lineList.get(i).trim();
            if (cols < lineLength.length()) {
                cols = lineLength.length();
            }
        }

        if (cols == 0) {
            throw new PatternFormatException("Error reading board size");
        }

        boardArray = new byte[rows][cols];
    }

    private void readLif105Board(ArrayList<String> lineList) throws PatternFormatException {
        for (int row = 0; row < boardArray.length; row++) {
            String[] boardRow = lineList.get(row).split("(?!^)");
            for (int col = 0; col < boardRow.length; col++) {
                if (boardRow[col].equals(".")) {
                    boardArray[row][col] = 0;
                } else if (boardRow[col].equals("*")) {
                    boardArray[row][col] = 1;
                } else {
                    throw new PatternFormatException("Unrecognized character in"
                            + "board definition " + boardRow[col]);
                }
            }
        }
    }

    private void cellsReader(Reader r) throws IOException, PatternFormatException {
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
        readCellsComments(lineList);
        readCellsSize(lineList);
        readCellsBoard(lineList);
        board.setBoard(boardArray);
        rules.setSurviveRules(2, 3);
        rules.setBirthRules(3);
        board.setMetadata(author, name, comment);
    }

    private void readCellsComments(ArrayList<String> lineList) {
        Matcher m;
        ArrayList<String> comments = new ArrayList<>();
        Pattern commentPattern = Pattern.compile("(!.*)");
        for (int i = 0; i < lineList.size(); i++) {
            m = commentPattern.matcher(lineList.get(i));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    comments.add(m.group(1));
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

        // get name of game board
        commentPattern = Pattern.compile("([!]Name:|[!]name:)(.+)");
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                if (!m.group(2).isEmpty()) {
                    name = m.group(2).trim();
                    comments.remove(i);
                    i = comments.size();
                }
            }
        }

        // get author of game board
        commentPattern = Pattern.compile("([!]Author:|[!]author:)(.+)");
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                if (!m.group(2).isEmpty()) {
                    author = m.group(2).trim();
                    comments.remove(i);
                    i = comments.size();
                }
            }
        }

        // get comments
        commentPattern = Pattern.compile("([!])(.+)");
        StringBuilder commentStringBuilder = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            m = commentPattern.matcher(comments.get(i));
            if (m.find()) {
                if (!m.group(2).isEmpty()) {
                    commentStringBuilder.append(m.group(2));
                    commentStringBuilder.append("\n");
                }
            }
            this.comment = commentStringBuilder.toString();
        }
    }

    private void readCellsSize(ArrayList<String> lineList) throws PatternFormatException {
        int rows = lineList.size();
        int cols = 0;

        for (int i = 0; i < lineList.size(); i++) {
            String lineLength = lineList.get(i).trim();
            if (cols < lineLength.length()) {
                cols = lineLength.length();
            }
        }

        if (cols == 0) {
            throw new PatternFormatException("Error reading board size");
        }

        boardArray = new byte[rows][cols];
    }

    private void readCellsBoard(ArrayList<String> lineList) throws PatternFormatException {
        for (int row = 0; row < boardArray.length; row++) {
            if (lineList.get(row) != null && !lineList.get(row).equals("")) {
                String[] boardRow = lineList.get(row).split("(?!^)");
                for (int col = 0; col < boardRow.length; col++) {
                    if (boardRow[col].equals(".")) {
                        boardArray[row][col] = 0;
                    } else if (boardRow[col].equals("O")) {
                        boardArray[row][col] = 1;
                    } else {
                        throw new PatternFormatException("Unrecognized character in"
                                + "board definition");
                    }
                }
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

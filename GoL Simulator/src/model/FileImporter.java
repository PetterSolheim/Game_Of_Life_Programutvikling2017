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
 * <p>
 * Class for converting external Game of Life pattern files to
 * {@link model.BoardDynamic} objects. Parses both the board and the pattern
 * files metadata. Class currently supports PlainText, Life 1.06 and RLE file
 * formats.</p>
 * <p>
 * Game rules stored in the {@link model.Rules} class object will also be
 * changed if the pattern file defines rules for the pattern.</p>
 *
 * <p>
 * Class was based on the file definitions outlined at
 * <a href="http://www.conwaylife.com" target="_blank">conwaylife.com</a>. Links
 * to file definitions included bellow.</p>
 *
 * @see
 * <a href="http://www.conwaylife.com/wiki/Plaintext" target="_blank">PlainText</a>
 * @see <a href="http://www.conwaylife.com/wiki/Life_1.06" target="_blank">Life
 * 1.06</a>
 * @see <a href="http://www.conwaylife.com/wiki/Rle" target="_blank">RLE</a>
 */
public class FileImporter {

    private BoardDynamic board = new BoardDynamic();
    private byte[][] boardArray;
    private String author = "unknown";
    private String name = "unknown";
    private String comment = "";
    private Rules rules = Rules.getInstance();
    
    /**
     * Constructor takes no arguments.
     */
    public FileImporter() {
        
    }

    /**
     * Reads a pattern file from disk, and returns a {@link model.BoardDynamic}
     * object containing the pattern and its metadata. If rules are defined in
     * the pattern file, these will be applied to the Rules class object.
     *
     * @param f the File object containing the pattern file.
     * @return a {@link model.BoardDynamic} object containing the pattern and
     * its metadata. Values in {@link model.Rules} are also set according to the
     * rules defined in the pattern file.
     *
     * @throws IOException which where thrown by its helper methods during file
     * reading.
     * @throws PatternFormatException if FileImporter was unable to parse the
     * given file.
     */
    public BoardDynamic readGameBoardFromDisk(File f) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(f); // determine the filetype
        readGameBoard(new FileReader(f), fileExtension);
        return board;
    }

    /**
     * Reads a pattern file from disk, and returns a {@link model.BoardDynamic}
     * object containing the pattern and its metadata. If rules are defined in
     * the pattern file, these will be applied to the Rules class object.
     *
     * @param url a <code>String</code> specifying the URL to the pattern file.
     * @return a {@link model.BoardDynamic} object containing the pattern and
     * its metadata.
     * @throws IOException thrown by its helper methods.
     * @throws PatternFormatException if FileImporter was unable to parse the
     * given file.
     */
    public BoardDynamic readGameBoardFromUrl(String url) throws IOException, PatternFormatException {
        String fileExtension = getFileExtension(url); // determin the filetype
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        readGameBoard(new InputStreamReader(conn.getInputStream()), fileExtension);
        return board;
    }

    /**
     * Selects the appropriate file parser based on file type.
     *
     * @param r the Reader object used to read the file.
     * @param fileExtension a String specifying the file extension of the file
     * being parsed.
     * @throws IOException
     * @throws PatternFormatException if the file type extension is not one of
     * the recognised types.
     */
    private void readGameBoard(Reader r, String fileExtension) throws IOException, PatternFormatException {
        switch (fileExtension) {
            case "rle":
                rleReader(createPatternArray(r));
                break;
            case "cells":
                plainTextReader(createPatternArray(r));
                break;
            case "lif":
                lifeReader(createPatternArray(r));
                break;
            default:
                throw new PatternFormatException("The *." + fileExtension
                        + " file format is not supported.");
        }
    }

    /**
     * Converts a pattern file from a Reader object into a String ArrayList
     * where each line of the pattern file is an index of the ArrayList.
     *
     * @param r
     * @return a String ArrayList containing the pattern.
     * @throws IOException
     */
    private ArrayList<String> createPatternArray(Reader r) throws IOException {
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
        return lineList;
    }

    /**
     * RLE file parser.
     *
     * @param r the Reader object.
     * @throws PatternFormatException if the pattern could not be parsed.
     */
    private void rleReader(ArrayList<String> lineList) throws PatternFormatException {
        readRleComments(lineList);
        readRleBoardSize(lineList);
        readRleRules(lineList);
        readRleBoard(lineList);
        board.setBoard(boardArray);
        board.setMetadata(author, name, comment);
    }

    /**
     * Reads and sets the patterns comments (name of author, board, etc).
     *
     * @param lineList the pattern.
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
     * Determnine and sets the game board dimensions.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if no board dimensions could be
     * determined.
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
     * Determine and set the game rules.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if no rules could be determined.
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
     * Reads and sets the cell state.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if the pattern uses unsupported cell,
     * states, or missing end of file (!) indicator, or described pattern does
     * not fit inside the patterns described board dimensions.
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
     * Determin which Life format (either 1.05 or 1.06) is being used, and pass
     * the pattern on to the correct file parser.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if the Life type could not be determined,
     * and any PatternFormatExceptions thrown by this methods helper methods.
     */
    private void lifeReader(ArrayList<String> lineList) throws PatternFormatException {
        Matcher m;
        boolean detectedFileType = false;

        Pattern fileType105 = Pattern.compile("(#Life 1.05)");
        m = fileType105.matcher(lineList.get(0));
        if (m.find()) {
            if (!m.group(1).isEmpty()) {
                detectedFileType = true;
                throw new PatternFormatException("Life 1.05 files are not"
                        + " currently supported!");
            }
        }

        if (!detectedFileType) {
            Pattern fileType106 = Pattern.compile("(#Life 1.06)");
            m = fileType106.matcher(lineList.get(0));
            if (m.find()) {
                if (!m.group(1).isEmpty()) {
                    detectedFileType = true;
                    life106Reader(lineList);
                }
            }
        }

        if (!detectedFileType) {
            throw new PatternFormatException("Lif file does not specify file"
                    + "type. Unable to read file.");
        }
    }

    /**
     * Parses Life 1.06 (*.lif, *.life) pattern files.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException thrown by this methods helper methods.
     */
    private void life106Reader(ArrayList<String> lineList) throws PatternFormatException {
        clearLife106Comments(lineList);
        int[] startingLocation = readLif106BoardSize(lineList);
        readLife106Board(lineList, startingLocation);
        board.setBoard(boardArray);
    }

    /**
     * Removes comment lines from the pattern.
     *
     * @param lineList the pattern.
     */
    private void clearLife106Comments(ArrayList<String> lineList) {
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

    /**
     * Determines and sets the game boards dimensions and returns the lowest x
     * and y values, which sybolize the starting coordinates for the board.
     *
     * @return an int[] where int[0] is the lowest x coordinate and int[1] is
     * the lowest y coordinate of the board definition.
     * @param lineList the pattern.
     * @throws PatternFormatException if no board size can be determined.
     */
    private int[] readLif106BoardSize(ArrayList<String> lineList) throws PatternFormatException {
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
        lowestX--;
        lowestY--;

        int rows = Math.abs(lowestY) + Math.abs(highestY);
        int cols = Math.abs(lowestX) + Math.abs(highestX);
        boardArray = new byte[rows][cols];
        return lowest;
    }

    /**
     * Reads and sets the cell state..
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if unable to determine coordinates of a
     * live cell.
     */
    private void readLife106Board(ArrayList<String> lineList, int[] lowest) throws PatternFormatException {
        int startX = lowest[0];
        if (startX < 0) {
            startX = startX * -1;
        }
        int startY = lowest[1];
        if (startY < 0) {
            startY = startY * -1;
        }

        Matcher m;
        Pattern coordinates = Pattern.compile("(-?[0-9]*)?\\s(-?[0-9]*)?");
        int capturedX, capturedY;

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
                    int printY = capturedY + startY;
                    int printX = capturedX + startX;
                    boardArray[capturedY + startY][capturedX + startX] = 1;
                } else {
                    throw new PatternFormatException("Unable to interpret cell"
                            + "coordinates.");
                }
            }
        }
    }

    /**
     * Parses PlainText (*.cells) pattern files.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException thrown by this methods helper methods.
     */
    private void plainTextReader(ArrayList<String> lineList) throws PatternFormatException {
        readPlainTextComments(lineList);
        readPlainTextBoardSize(lineList);
        readPlainTextBoard(lineList);
        board.setBoard(boardArray);
        rules.setSurviveRules(2, 3);
        rules.setBirthRules(3);
        board.setMetadata(author, name, comment);
    }

    /**
     * Reads and sets the patterns comments (name of author, board, etc).
     *
     * @param lineList the pattern.
     */
    private void readPlainTextComments(ArrayList<String> lineList) {
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

        // remove empty comment lines.
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

    /**
     * Determines and sets the game boards dimensions.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if no board size can be determined.
     */
    private void readPlainTextBoardSize(ArrayList<String> lineList) throws PatternFormatException {
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

    /**
     * Reads and sets the cell state.
     *
     * @param lineList the pattern.
     * @throws PatternFormatException if unrecognized characters are used to
     * describe cell states.
     */
    private void readPlainTextBoard(ArrayList<String> lineList) throws PatternFormatException {
        for (int row = 0; row < boardArray.length; row++) {
            // empty lines symbolize a row of dead cells.
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
     * Takes a String and returns a file type extensions based on that String.
     * Used to determine file type extensions of patterns acquired from an URL.
     *
     * @param f a File object containing the file to get the extension from.
     * @return a String specifying the file type extension.
     */
    private String getFileExtension(File f) {
        return getFileExtension(f.getName());
    }

    /**
     * Takes a String and returns a file type extensions based on that String.
     * Used to determine file type extensions of patterns acquired from an URL.
     *
     * @param fileName a String specifying the file name.
     * @return a String specifying the file type extension.
     */
    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(".");
        String fileExtension = fileName.substring(i + 1);
        fileExtension = fileExtension.toLowerCase();
        return fileExtension;
    }
}

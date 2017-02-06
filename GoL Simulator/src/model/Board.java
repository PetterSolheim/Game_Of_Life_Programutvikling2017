/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author alehel
 */
public class Board {
    private static Board instance; // brettet er en singleton
    private byte[][] currentBoard;
    private byte[][] originalBoard;
    private int generationCount;
    private int cellCount;
    private int minToSurvive;
    private int maxToSurvive;
    private int birth;
    private boolean isPause;
    
    // Klassen er en singleton, og derfor er konstruktøren privat.
    private Board() {
    }
    
    // muligjør andre klasser å få tak i brett objektet.
    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }
    
    public byte[][] getBoard() {
        return currentBoard;
    }
    
    // metode for å sette det nye brettet. To kopier lages. originalBoard
    // opprettes slik at brettet enkelt kan tilbakesetilles til start.
    public void setBoard(byte[][] newBoard) {
        originalBoard = duplicateBoard(newBoard);
        currentBoard = duplicateBoard(newBoard);
    }
    
    public void setRules(int min, int max, int birth) {
        if (min >= 0 && min <= 8 && max >= 0 && max <= 8 && birth >= 0 && birth <= 8) {
            minToSurvive = min;
            maxToSurvive = max;
            this.birth = birth;
        }
    }
    
    public void nextGeneration() {
        // spillreglene testes mot en kopi av brettet, mens endringer utføres
        // mot det faktiske brettet. Dette for å unngå at endringer på brettet
        // påvirker reglene.
        byte[][] testPattern = duplicateBoard(currentBoard);
        
        // itterer igjennom hvert punkt i brettet. For hvert punkt, tell antall 
        // naboer, og test antall naboer mot spillets regler.
        for (int i = 0; i < testPattern.length; i++) {
            for (int j = 0; j < testPattern[0].length; j++) {
                int neighbours = countNeighbours(testPattern, i, j);

                // levende celler med < 2 eller > 3 levende naboceller, dør.
                if (neighbours < minToSurvive || neighbours > maxToSurvive) {
                    currentBoard[i][j] = 0;
                }

                // en død celle med nøyaktig 3 levende naboceller, blir levende.
                if (neighbours == birth) {
                    currentBoard[i][j] = 1;
                }
            }
        }
        
    }
    
    // metode som teller antall naboer. Brukes av nextGeneration metoden
    // for å avgjøre cellers nye tilstand. Tar i mote brettet som skal brukes,
    // og koordinatene (i form av x og y akser) på cellen som skal ha sine 
    // naboer telt. Try-catch brukes for å håndtere situasjoner der cellen som
    // telles befinner seg utenfor brettets array.
    private int countNeighbours(byte[][] cell, int x, int y) {
        int neighbours = 0;
        
        try {
            if (cell[x][y + 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x - 1][y + 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x - 1][y] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x - 1][y - 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x][y - 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x + 1][y - 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x + 1][y] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        try {
            if (cell[x + 1][y + 1] == 1) {
                neighbours++;
            }
        } catch (Exception e) {
        }

        return neighbours;
    }
    
    public int getMinToSurvive() {
        return minToSurvive;
    }
    
    public int getMaxToSurvive() {
        return maxToSurvive;
    }
    
    public int getBirth() {
        return birth;
    }
    
    public void setCellCount(int newValue) {
        if (newValue >= 0) {
            cellCount = newValue;
        }
    }
    
    public int getCellCount() {
        return cellCount;
    }
    
    public int getGenerationCount() {
        return generationCount;
    }
    
    public void increaseGenerationCount() {
        generationCount++;
    }
    
    public void resetGenerationCount() {
        generationCount = 1;
    }
    
    // metoden kopierer verdiene fra brettets opprinnelige utgangspunkt inn
    // i den nåværende tabellen.
    public void resetBoard() {
        currentBoard = duplicateBoard(originalBoard);
        
    }
    
    // metode som brukes for å lage kopier av brettet. Brettet som skal kopieres
    // sendes inn som parameter. Kopi av brettet sendes ut som returverdi.
    private byte[][] duplicateBoard(byte[][] original) {
        byte[][] boardCopy = new byte[original.length-1][original[0].length-1];
        
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                boardCopy[i][j] = original[i][j];
            }
        }
        return boardCopy;
    }
    
}

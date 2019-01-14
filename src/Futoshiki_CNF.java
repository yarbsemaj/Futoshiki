/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author Ilias
 */
public class Futoshiki_CNF {

    static final int N = 4;
    static final int EQUALITIES = 3;

    static final int BOARD = N*N;
    static final int BOARDN = BOARD*N;
    static final int EQUALITY_SPACES = (N-1)*(N);
    static final int EQUALITS = EQUALITY_SPACES *EQUALITIES;
    static final int VARS = BOARDN+EQUALITS+EQUALITS;
    static int clauses = 0;
    static ArrayList<String> DIMACS_CNF = new ArrayList();
    static int[] solution ={};
    static final int NO_EQUALITY = 1;
    static final int GREATER_THAN_EQUALITY = 3;
    static final int LESS_THAN_EQUALITY = 2;
    /*
     * Generate DIMACS CNF format and print any given SAT solution
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        // Generate DIMACS CNF format 
        addFacts();
        atLeastOneDigitInCell();
        atLeastOneEqualityInCellHorizontal();
        atLeastOneEqualityInCellVertically();
        eachDigitAtMostOnesInRow();
        eachDigitAtMostOnesInColumn();
        equalitiesGreaterThanHorizontal();
        equalitiesLessThanHorizontal();
        equalitiesGreaterThanVertical();
        equalitiesLessThanVertical();
        onlyOneEqualityHorizontalPerCell();
        onlyOneEqualityVerticalPerCell();

        print_DIMACS_CNF_format();
        
        // Print a SAT solution        
        printSATSolutionBoard(solution);
    }

    /**
     *
     * @param digit [1...N]
     * @param row [1...N]
     * @param column [1...N]
     * @return
     */
    private static int toBoardVariable(int digit, int row, int column){
        return (BOARD*(digit-1) + N*(row-1) + (column-1) + 1);
    }

    private static int toEqualitiesVariableHorizontal(int equalities, int row, int column){
        return (EQUALITY_SPACES *(equalities-1) + EQUALITIES*(row-1) + (column-1) + 1)+BOARDN;
    }

    private static int toEqualitiesVariableVertical(int equalities, int row, int column){
        return (EQUALITY_SPACES *(equalities-1) + N*(row-1) + (column-1) + 1)+BOARDN+EQUALITS;
    }

    private static void addFacts(){        
        // Facts
        DIMACS_CNF.add("c Pre-assigned entries");
        // Update the number of facts according to the number of added DIMACS CNF clauses
        int facts=0;
        //DIMACS_CNF.add(toEqualitiesVariableHorizontal(LESS_THAN_EQUALITY, 4, 1) + " 0");
        //DIMACS_CNF.add(toEqualitiesVariableVertical(GREATER_THAN_EQUALITY, 3, 1) + " 0");
        //DIMACS_CNF.add(toBoardVariable(4, 4, 1) + " 0");
        clauses += facts; 
    }

    private static void atLeastOneDigitInCell(){
        DIMACS_CNF.add("c Each number appears once per number cell:");
        for(int cell = 1; cell <= BOARD ; cell++){
            String output ="";
            for(int num = cell; num <= BOARDN ; num+=BOARD){
                output += String.valueOf(num)+" ";
            }
            output += "0";
            DIMACS_CNF.add(output);
            clauses++;
        }
    }

    private static void atLeastOneEqualityInCellHorizontal(){
        DIMACS_CNF.add("c Each equality appears once per equality cell horizontally:");
        for(int cell = 1; cell <= EQUALITY_SPACES; cell++){
            String output ="";
            for(int num = cell; num <= EQUALITS ; num+= EQUALITY_SPACES){
                output += String.valueOf(num+BOARDN)+" ";
            }
            output += "0";
            DIMACS_CNF.add(output);
            clauses++;
        }
    }

    private static void atLeastOneEqualityInCellVertically(){
        DIMACS_CNF.add("c Each equality appears once per equality cell vertically:");
        for(int cell = 1; cell <= EQUALITY_SPACES; cell++){
            String output ="";
            for(int num = cell; num <= EQUALITS ; num+= EQUALITY_SPACES){
                output += String.valueOf(num+BOARDN+EQUALITS)+" ";
            }
            output += "0";
            DIMACS_CNF.add(output);
            clauses++;
        }
    }

    private static void eachDigitAtMostOnesInRow(){
        // Each digit appears at most once in each row
        DIMACS_CNF.add("c Each digit appears at most once in each row:");
        for (int digit = 1; digit <= N; digit++) {
            for (int row = 1; row < N; row++) {
                for (int columnLow = 1; columnLow <= N-1; columnLow++) {
                    for (int columnHigh = columnLow+1; columnHigh <= N; columnHigh++) {
                        DIMACS_CNF.add("-" + toBoardVariable(digit,row,columnLow) + " -" + toBoardVariable(digit,row,columnHigh) + " 0");
                        clauses++;
                    }
                }
            }
        }
    }

    private static void onlyOneEqualityHorizontalPerCell(){
        // Each digit appears at most once in each row
        DIMACS_CNF.add("c Only one equality per horizontal cell:");
            for (int column = 1; column <= N-1; column++) {
                for (int row = 1; row <= N; row++) {
                    for (int equality = 1; equality <= EQUALITIES; equality++) {
                    String clause = toEqualitiesVariableHorizontal(equality,row,column)+" ";
                    for(int num = 1; num<=EQUALITIES; num++)
                    {
                        if(num!=equality)
                        clause+="-"+String.valueOf(toEqualitiesVariableHorizontal(num,row,column))+" ";
                    }
                    DIMACS_CNF.add(clause+"0");
                }
            }
        }
    }

    private static void onlyOneEqualityVerticalPerCell(){
        // Each digit appears at most once in each row
        DIMACS_CNF.add("c Only one equality per vertical cell:");
        for (int column = 1; column <= N; column++) {
            for (int row = 1; row <= N-1; row++) {
                for (int equality = 1; equality <= EQUALITIES; equality++) {
                    String clause = toEqualitiesVariableVertical(equality,row,column)+" ";
                    for(int num = 1; num<=EQUALITIES; num++)
                    {
                        if(num!=equality)
                            clause+="-"+String.valueOf(toEqualitiesVariableVertical(num,row,column))+" ";
                    }
                    DIMACS_CNF.add(clause+"0");
                }
            }
        }
    }

    private static void equalitiesLessThanHorizontal(){
        DIMACS_CNF.add("c If an less than equality exists horizontally, then that should be honored:");
        for (int digit = 1; digit <= N; digit++) {
            for (int column = 1; column <= N-1; column++) {
                for (int row = 1; row <= N; row++) {
                    String clause = "-" + toEqualitiesVariableHorizontal(LESS_THAN_EQUALITY,row,column) + " -" + toBoardVariable(digit,row,column)+" ";
                    for(int num = digit+1; num<=N; num++)
                    {
                        clause+=String.valueOf(toBoardVariable(num,row,column+1))+" ";
                    }
                    DIMACS_CNF.add(clause+"0");
                }
                }
        }
    }

    private static void equalitiesGreaterThanHorizontal(){
        DIMACS_CNF.add("c If an greater than equality exists horizontally, then that should be honored:");
        for (int digit = 1; digit <= N; digit++) {
            for (int column = 1; column <= N-1; column++) {
                for (int row = 1; row <= N; row++) {
                    String clause = "-" + toEqualitiesVariableHorizontal(GREATER_THAN_EQUALITY,row,column) + " -" + toBoardVariable(digit,row,column)+" ";
                    for(int num = 1 ; num<=digit-1; num++)
                    {
                        clause+=String.valueOf(toBoardVariable(num,row,column+1))+" ";
                    }
                    DIMACS_CNF.add(clause+"0");
                }
            }
        }
    }

    private static void equalitiesLessThanVertical(){
        DIMACS_CNF.add("c If an less than equality exists vertical, then that should be honored:");
        for (int digit = 1; digit <= N; digit++) {
            for (int column = 1; column <= N; column++) {
                for (int row = 1; row <= N-1; row++) {
                    String clause = "-" + toEqualitiesVariableVertical(LESS_THAN_EQUALITY,row,column) + " -" + toBoardVariable(digit,row,column)+" ";
                    for(int num = digit+1; num<=N; num++)
                    {
                        clause+=String.valueOf(toBoardVariable(num,row+1,column))+" ";
                    }
                    DIMACS_CNF.add(clause+"0");
                }
            }
        }
    }

    private static void equalitiesGreaterThanVertical(){
        DIMACS_CNF.add("c If an greater than equality exists vertical, then that should be honored:");
        for (int digit = 1; digit <= N; digit++) {
            for (int column = 1; column <= N; column++) {
                for (int row = 1; row <= N-1; row++) {
                    String clause = "-" + toEqualitiesVariableVertical(GREATER_THAN_EQUALITY,row,column) + " -" + toBoardVariable(digit,row,column)+" ";
                    for(int num = 1 ; num<=digit-1; num++)
                    {
                        clause+=String.valueOf(toBoardVariable(num,row+1,column))+" ";
                    }
                    DIMACS_CNF.add(clause+"0");
                }
            }
        }
    }

    private static void eachDigitAtMostOnesInColumn(){
        // Each number appears at most once in each column
        DIMACS_CNF.add("c Each number appears at most once in each column:");
        for (int digit = 1; digit <= N; digit++) {
            for (int column = 1; column <= N; column++) {
                for (int rowLow = 1; rowLow <= N-1; rowLow++) {
                    for (int rowHigh = rowLow+1; rowHigh <= N; rowHigh++) {
                        DIMACS_CNF.add("-" + toBoardVariable(digit,rowLow,column) + " -" + toBoardVariable(digit,rowHigh,column) + " 0");
                        clauses++;
                    }
                }
            }
        }
    }


    private static void print_DIMACS_CNF_format(){
        // Print DIMACS CNF format 
        System.out.println("==========================================");
        System.out.println("===== Beginning of DIMACS CNF format =====");
        System.out.println("==========================================");
        System.out.println("c numbers board");
        System.out.println("c digit range [1..." + N + "]");
        System.out.println("c row range: [1..." + N + "]");
        System.out.println("c column range: [1..." + N + "]");
        System.out.println("c board[digit][row][column]: variable");
        for (int digit = 1; digit <= N; digit++) {
            for (int row = 1; row <= N; row++) {
                for (int column = 1; column <= N; column++) {
                    System.out.println("c board[" + digit + "][" + row + "][" + column + "]: " + toBoardVariable(digit,row,column));
                }
            }
        }
        System.out.println("c equalities horizontal board");
        System.out.println("c digit range [1..." + EQUALITIES + "]");
        System.out.println("c row range: [1..." + (N) + "]");
        System.out.println("c column range: [1..." + (N-1) + "]");
        System.out.println("c equalitiesH[equality][row][column]: variable");
        for (int equality = 1; equality <= EQUALITIES; equality++) {
            for (int row = 1; row <= N; row++) {
                for (int column = 1; column <= N-1; column++) {
                    System.out.println("c equalitiesH[" + equality + "][" + row + "][" + column + "]: " + toEqualitiesVariableHorizontal(equality,row,column));
                }
            }
        }
        System.out.println("c equalities vertical board");
        System.out.println("c digit range [1..." + EQUALITIES + "]");
        System.out.println("c row range: [1..." + (N-1) + "]");
        System.out.println("c column range: [1..." + (N) + "]");
        System.out.println("c equalitiesV[equality][row][column]: variable");
        for (int equality = 1; equality <= EQUALITIES; equality++) {
            for (int row = 1; row <= N-1; row++) {
                for (int column = 1; column <= N; column++) {
                    System.out.println("c equalitiesH[" + equality + "][" + row + "][" + column + "]: " + toEqualitiesVariableVertical(equality,row,column));
                }
            }
        }

        System.out.println("c #vars: " + VARS);
        System.out.println("c #clauses: " + clauses);
        System.out.println("p cnf " + VARS + " " + clauses);
        for (int i = 0; i < DIMACS_CNF.size(); i++) {
            System.out.println(DIMACS_CNF.get(i));            
        }
        System.out.println("====================================");
        System.out.println("===== End of DIMACS CNF format =====");
        System.out.println("====================================");
        System.out.println("");
    }
    
    /**
     * Print resulting board based on a set of variables from a SAT solution
     * @param variables 
     */
    private static void printSATSolutionBoard(int[] variables){
        int digit;
        int tmp;
        int row;
        int column;             
        
        int[][] tmpBoard = new int [N][N];
        for (row = 0; row < N; row++) {
            for (column = 0; column < N; column++) {
                tmpBoard[row][column] = -1;
            }
        }
        for (int i = 0; i < BOARDN; i++) {
            if(variables[i] > 0){
                digit = (variables[i]-1)/BOARD;
                tmp = (variables[i]-1)%BOARD;
                row = tmp/N;
                column = tmp%N;

                tmpBoard[row][column] = digit;
            }
        }

        String[][] equalisesBoardHorizontal = new String [N][N-1];
        for (row = 0; row < N; row++) {
            for (column = 0; column < N-1; column++) {
                equalisesBoardHorizontal[row][column] = "Error";
            }
        }
        for (int i = BOARDN; i < BOARDN+EQUALITS; i++) {
            if(variables[i] > 0){
                int equality = (variables[i]-1-BOARDN)/EQUALITY_SPACES;
                String equalityDisplay = "E";
                switch (equality+1){
                    case NO_EQUALITY:
                        equalityDisplay = " ";
                        break;
                    case LESS_THAN_EQUALITY:
                        equalityDisplay = "<";
                        break;
                    case GREATER_THAN_EQUALITY:
                        equalityDisplay = ">";
                        break;
                }
                tmp = (variables[i]-1-BOARDN)% EQUALITY_SPACES;
                row = tmp/(N-1);
                column = tmp%(N-1);
                equalisesBoardHorizontal[row][column] = equalityDisplay;
            }
        }

        String[][] equalisesBoardVertical = new String [N-1][N];
        for (row = 0; row < N-1; row++) {
            for (column = 0; column < N; column++) {
                equalisesBoardVertical[row][column] = "Error";
            }
        }
        for (int i = BOARDN+EQUALITS; i < BOARDN+(EQUALITS*2); i++) {
            if(variables[i] > 0){
                int equality = (variables[i]-1-BOARDN-EQUALITS)/EQUALITY_SPACES;
                String equalityDisplay = String.valueOf(equality);
                switch (equality+1){
                    case NO_EQUALITY:
                        equalityDisplay = " ";
                        break;
                    case LESS_THAN_EQUALITY:
                        equalityDisplay = "^";
                        break;
                    case GREATER_THAN_EQUALITY:
                        equalityDisplay = "V";
                        break;
                }
                tmp = (variables[i]-1-BOARDN-EQUALITS)% EQUALITY_SPACES;
                row = tmp/(N);
                column = tmp%N;
                equalisesBoardVertical[row][column] = equalityDisplay;
            }
        }
        
        System.out.println("=======================");
        System.out.println("===== Given board =====");
        System.out.println("=======================");

        for (row = 0; row < N; row++) {
            System.out.print("   ");
            for (column = 0; column < N; column++) {
                System.out.print(((tmpBoard[row][column])>=0? (tmpBoard[row][column]+1):"-"));

                if(column < N-1) {
                    System.out.print(((!equalisesBoardHorizontal[row][column].equals("E")) ?
                            (equalisesBoardHorizontal[row][column]) : " "));
                }

            }
            System.out.println();
            System.out.print("   ");
            if(row < N-1) {
                for (column = 0; column < N; column++) {
                    System.out.print(
                            (equalisesBoardVertical[row][column])+" ");
                }
            }
            System.out.println();
        }
        System.out.println("=======================");
    }
}

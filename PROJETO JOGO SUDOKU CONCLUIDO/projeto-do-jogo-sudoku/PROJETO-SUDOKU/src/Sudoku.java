import java.util.Scanner;

public class Sudoku {
    public static void main(String[] args) {
        // Inicializar o jogo
        SudokuGame game = new SudokuGame();
        
        // Configurar tabuleiro inicial com os argumentos passados
        if (args.length > 0) {
            game.initializeFromArguments(args);
        } else {
            System.out.println("Nenhum argumento fornecido. Inicializando jogo padrão.");
            game.initializeDefaultBoard();
        }
        
        // Iniciar o jogo
        game.play();
    }
}

class SudokuGame {
    private SudokuBoard board;
    private Scanner scanner;
    private boolean gameOver;
    
    public SudokuGame() {
        this.board = new SudokuBoard();
        this.scanner = new Scanner(System.in);
        this.gameOver = false;
    }
    
    public void initializeFromArguments(String[] args) {
        for (String arg : args) {
            try {
                String[] parts = arg.split(";");
                String[] position = parts[0].split(",");
                String[] valueAndLocked = parts[1].split(",");
                
                int row = Integer.parseInt(position[0]);
                int col = Integer.parseInt(position[1]);
                int value = Integer.parseInt(valueAndLocked[0]);
                boolean locked = Boolean.parseBoolean(valueAndLocked[1]);
                
                board.setCellValue(row, col, value, locked);
            } catch (Exception e) {
                System.out.println("Erro ao processar argumento: " + arg);
                System.out.println("Formato esperado: row,col;value,locked");
            }
        }
        System.out.println("Tabuleiro inicializado com sucesso!");
    }
    
    public void initializeDefaultBoard() {
        // Inicializa um tabuleiro de Sudoku padrão
        String[] defaultArgs = {
            "0,0;5,true", "1,0;3,true", "4,0;7,true",
            "0,1;6,true", "3,1;1,true", "4,1;9,true", "5,1;5,true",
            "1,2;9,true", "2,2;8,true", "7,2;6,true",
            "0,3;8,true", "4,3;6,true", "8,3;3,true",
            "0,4;4,true", "8,4;1,true",
            "0,5;7,true", "4,5;2,true", "8,5;6,true",
            "1,6;6,true", "6,6;2,true", "7,6;8,true",
            "3,7;4,true", "4,7;1,true", "5,7;9,true", "8,7;5,true",
            "4,8;8,true", "7,8;7,true", "8,8;9,true"
        };
        
        initializeFromArguments(defaultArgs);
    }
    
    public void play() {
        System.out.println("Bem-vindo ao Sudoku!");
        System.out.println("Comandos:");
        System.out.println("- Para jogar: digite 'linha coluna valor'");
        System.out.println("- Para verificar o tabuleiro: digite 'verificar'");
        System.out.println("- Para sair: digite 'sair'");
        
        while (!gameOver) {
            board.displayBoard();
            System.out.print("Digite seu comando: ");
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("sair")) {
                gameOver = true;
                System.out.println("Obrigado por jogar!");
            } else if (input.equals("verificar")) {
                if (board.isBoardFull()) {
                    if (board.isValid()) {
                        System.out.println("Parabéns! Você completou o Sudoku corretamente!");
                        gameOver = true;
                    } else {
                        System.out.println("O tabuleiro está preenchido, mas há erros. Verifique novamente.");
                    }
                } else {
                    System.out.println("O tabuleiro ainda não está completo.");
                }
            } else {
                try {
                    String[] parts = input.split(" ");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);
                    
                    if (row < 0 || row > 8 || col < 0 || col > 8) {
                        System.out.println("Posição inválida. Use valores entre 0 e 8.");
                        continue;
                    }
                    
                    if (value < 1 || value > 9) {
                        System.out.println("Valor inválido. Use valores entre 1 e 9.");
                        continue;
                    }
                    
                    if (board.isCellLocked(row, col)) {
                        System.out.println("Esta célula está bloqueada e não pode ser modificada.");
                        continue;
                    }
                    
                    board.setCellValue(row, col, value, false);
                    
                    // Verificar se o jogo foi concluído após a jogada
                    if (board.isBoardFull() && board.isValid()) {
                        board.displayBoard();
                        System.out.println("Parabéns! Você completou o Sudoku corretamente!");
                        gameOver = true;
                    }
                } catch (Exception e) {
                    System.out.println("Comando inválido. Use o formato: 'linha coluna valor'");
                }
            }
        }
    }
}

class SudokuBoard {
    private SudokuCell[][] cells;
    private static final int BOARD_SIZE = 9;
    private static final int BOX_SIZE = 3;
    
    public SudokuBoard() {
        cells = new SudokuCell[BOARD_SIZE][BOARD_SIZE];
        
        // Inicializar todas as células vazias
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                cells[row][col] = new SudokuCell();
            }
        }
    }
    
    public void setCellValue(int row, int col, int value, boolean locked) {
        cells[row][col].setValue(value);
        cells[row][col].setLocked(locked);
    }
    
    public boolean isCellLocked(int row, int col) {
        return cells[row][col].isLocked();
    }
    
    public void displayBoard() {
        System.out.println("\n    0 1 2   3 4 5   6 7 8");
        System.out.println("  -------------------------");
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print(row + " | ");
            
            for (int col = 0; col < BOARD_SIZE; col++) {
                int value = cells[row][col].getValue();
                
                if (value == 0) {
                    System.out.print("  ");
                } else {
                    System.out.print(value + " ");
                }
                
                if ((col + 1) % BOX_SIZE == 0 && col < BOARD_SIZE - 1) {
                    System.out.print("| ");
                }
            }
            
            System.out.println("|");
            
            if ((row + 1) % BOX_SIZE == 0 && row < BOARD_SIZE - 1) {
                System.out.println("  |-------+-------+-------|");
            }
        }
        
        System.out.println("  -------------------------");
    }
    
    public boolean isBoardFull() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (cells[row][col].getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isValid() {
        // Verificar linhas
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (!isRowValid(row)) {
                return false;
            }
        }
        
        // Verificar colunas
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (!isColumnValid(col)) {
                return false;
            }
        }
        
        // Verificar quadrantes 3x3
        for (int boxRow = 0; boxRow < BOX_SIZE; boxRow++) {
            for (int boxCol = 0; boxCol < BOX_SIZE; boxCol++) {
                if (!isBoxValid(boxRow * BOX_SIZE, boxCol * BOX_SIZE)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean isRowValid(int row) {
        boolean[] used = new boolean[BOARD_SIZE + 1]; // +1 porque os valores vão de 1 a 9
        
        for (int col = 0; col < BOARD_SIZE; col++) {
            int value = cells[row][col].getValue();
            
            if (value != 0) {
                if (used[value]) {
                    return false; // Valor duplicado
                }
                used[value] = true;
            }
        }
        
        return true;
    }
    
    private boolean isColumnValid(int col) {
        boolean[] used = new boolean[BOARD_SIZE + 1];
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            int value = cells[row][col].getValue();
            
            if (value != 0) {
                if (used[value]) {
                    return false; // Valor duplicado
                }
                used[value] = true;
            }
        }
        
        return true;
    }
    
    private boolean isBoxValid(int startRow, int startCol) {
        boolean[] used = new boolean[BOARD_SIZE + 1];
        
        for (int row = 0; row < BOX_SIZE; row++) {
            for (int col = 0; col < BOX_SIZE; col++) {
                int value = cells[startRow + row][startCol + col].getValue();
                
                if (value != 0) {
                    if (used[value]) {
                        return false; // Valor duplicado
                    }
                    used[value] = true;
                }
            }
        }
        
        return true;
    }
}

class SudokuCell {
    private int value;
    private boolean locked;
    
    public SudokuCell() {
        this.value = 0;  // 0 representa uma célula vazia
        this.locked = false;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
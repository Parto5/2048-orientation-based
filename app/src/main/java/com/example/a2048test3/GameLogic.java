package com.example.a2048test3;

import android.util.Log;
import android.widget.TextView;

import com.example.a2048test3.database.*;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameLogic {

    private MainActivity mainActivity;
    private GameApi gameApi;
    private TextView[][] tiles;  // Tablica przechowująca referencje do pól
    private int[][] board;       // Plansza do przechowywania wartości
    private boolean isMoving = false;  // Flaga kontrolująca, czy ruch jest w trakcie
    private boolean canMove = true; // Flaga sprawdzająca, czy ruchy są dozwolone

    private String username = "Player1";
    public GameLogic(TextView[][] tiles, GameApi gameApi, MainActivity mainActivity) {
        this.tiles = tiles;
        this.board = new int[4][4];  // Plansza 4x4
        this.gameApi = gameApi;
        this.mainActivity = mainActivity; // Przechowujemy referencję do MainActivity
    }

    // Inicjalizacja planszy
    public void initBoard() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                tiles[row][col].setText("");  // Ustawienie pustych pól
                board[row][col] = 0;  // Inicjalizacja planszy jako pustej
            }
        }
    }

    // Dodawanie nowego bloczka (2 lub 4) w losowym miejscu na planszy
    public void addNewTile() {
        Random rand = new Random();
        int value = rand.nextBoolean() ? 2 : 4;
        int row, col;
        do {
            row = rand.nextInt(4);
            col = rand.nextInt(4);
        } while (board[row][col] != 0);  // Dopóki nie znajdziemy pustego miejsca

        board[row][col] = value;
        tiles[row][col].setText(String.valueOf(value));
    }

    public void incrementMoveCount() {
        // Pobranie aktualnej liczby ruchów z serwera
        gameApi.getMoveCount(username).enqueue(new Callback<MoveCountResponse>() {
            @Override
            public void onResponse(Call<MoveCountResponse> call, Response<MoveCountResponse> response) {
                if (response.isSuccessful()) {
                    int currentMoveCount = response.body().getMove_count();
                    currentMoveCount++;
                    Log.d("GameLogic", "Received move count: " + currentMoveCount);

                    // Zaktualizowanie liczby ruchów na serwerze
                    int finalCurrentMoveCount = currentMoveCount;
                    gameApi.updateMoveCount(new MoveCountRequest(username, currentMoveCount)).enqueue(new Callback<MoveCountResponse>() {
                        @Override
                        public void onResponse(Call<MoveCountResponse> call, Response<MoveCountResponse> response) {
                            if (response.isSuccessful()) {
                                Log.d("GameLogic", "Move count updated successfully.");
                                // Po udanej aktualizacji liczby ruchów, wywołujemy metodę z MainActivity
                                mainActivity.updateMoveCountText(finalCurrentMoveCount);
                            } else {
                                Log.e("GameLogic", "Failed to update move count on server.");
                            }
                        }

                        @Override
                        public void onFailure(Call<MoveCountResponse> call, Throwable t) {
                            Log.e("GameLogic", "Failed to update move count on server: " + t.getMessage());
                        }
                    });
                } else {
                    Log.e("GameLogic", "Failed to fetch move count from server.");
                }
            }

            @Override
            public void onFailure(Call<MoveCountResponse> call, Throwable t) {
                Log.e("GameLogic", "Failed to fetch move count from server: " + t.getMessage());
            }
        });
    }



    // Metoda wykonująca ruch w odpowiednim kierunku
    public void moveBlocks(String direction) {
        if (isMoving || !canMove) {

            return;  // Nie wykonuj ruchu, jeśli już trwa ruch lub jeśli nie można wykonać ruchu
        }

        isMoving = true;  // Ustawienie flagi, żeby zablokować kolejny ruch

        // Przekształcanie wartości planszy do tablicy tymczasowej
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                String tileText = tiles[row][col].getText().toString();
                board[row][col] = tileText.isEmpty() ? 0 : Integer.parseInt(tileText);
            }
        }

        // Wykonanie odpowiedniego ruchu
        if (direction.equals("RIGHT")) {
            for (int row = 0; row < 4; row++) {
                int[] newRow = slideRowRight(board[row]);
                for (int col = 0; col < 4; col++) {
                    board[row][col] = newRow[col];
                }
            }
        } else if (direction.equals("LEFT")) {
            for (int row = 0; row < 4; row++) {
                int[] newRow = slideRowLeft(board[row]);
                for (int col = 0; col < 4; col++) {
                    board[row][col] = newRow[col];
                }
            }
        } else if (direction.equals("UP")) {
            for (int col = 0; col < 4; col++) {
                int[] newCol = slideColumnUp(board, col);
                for (int row = 0; row < 4; row++) {
                    board[row][col] = newCol[row];
                }
            }
        } else if (direction.equals("DOWN")) {
            for (int col = 0; col < 4; col++) {
                int[] newCol = slideColumnDown(board, col);
                for (int row = 0; row < 4; row++) {
                    board[row][col] = newCol[row];
                }
            }
        }

        // Przypisanie wartości z powrotem do widocznych pól na planszy
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col] != 0) {
                    tiles[row][col].setText(String.valueOf(board[row][col]));
                } else {
                    tiles[row][col].setText("");
                }
            }
        }

        // Dodanie nowego bloczka po każdym ruchu
        addNewTile();
        incrementMoveCount(); //Zwiększenie liczby ruchów
    }

    // Ustawienie flagi 'canMove' na true, gdy brak pochyłu
    public void enableMoveWhenFlat(float pitch, float roll) {
        // Jeśli brak pochyłu (pitch i roll bliskie zeru), umożliwiamy kolejny ruch
        if (Math.abs(pitch) < 0.2 && Math.abs(roll) < 0.2) {
            canMove = true;
            isMoving = false;
        }
    }

    // Metody przesuwania
    public int[] slideRowLeft(int[] row) {
        int[] newRow = new int[4];
        int insertPos = 0;
        for (int i = 0; i < 4; i++) {
            if (row[i] != 0) {
                newRow[insertPos] = row[i];
                insertPos++;
            }
        }
        return newRow;
    }

    public int[] slideRowRight(int[] row) {
        int[] newRow = new int[4];
        int insertPos = 3;
        for (int i = 3; i >= 0; i--) {
            if (row[i] != 0) {
                newRow[insertPos] = row[i];
                insertPos--;
            }
        }
        return newRow;
    }

    public int[] slideColumnUp(int[][] board, int col) {
        int[] newCol = new int[4];
        int insertPos = 0;
        for (int row = 0; row < 4; row++) {
            if (board[row][col] != 0) {
                newCol[insertPos] = board[row][col];
                insertPos++;
            }
        }
        return newCol;
    }

    public int[] slideColumnDown(int[][] board, int col) {
        int[] newCol = new int[4];
        int insertPos = 3;
        for (int row = 3; row >= 0; row--) {
            if (board[row][col] != 0) {
                newCol[insertPos] = board[row][col];
                insertPos--;
            }
        }
        return newCol;
    }

    // Pobranie liczby ruchów z serwera
    public void getMoveCount() {
        // Zmieniamy sposób pobierania liczby ruchów, teraz korzystamy z serwera
        gameApi.getMoveCount(username).enqueue(new Callback<MoveCountResponse>() {
            @Override
            public void onResponse(Call<MoveCountResponse> call, Response<MoveCountResponse> response) {
                if (response.isSuccessful()) {
                    int moveCount = response.body().getMove_count();
                    // Po pobraniu liczby ruchów, wywołujemy metodę z MainActivity
                    mainActivity.updateMoveCountText(moveCount);
                } else {
                    Log.e("GameLogic", "Failed to fetch move count from server.");
                }
            }

            @Override
            public void onFailure(Call<MoveCountResponse> call, Throwable t) {
                // Obsługujemy błąd
                Log.e("GameLogic", "Failed to fetch move count from server: " + t.getMessage());
            }
        });
    }

}

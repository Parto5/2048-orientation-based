package com.example.a2048test3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.a2048test3.database.GameApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private TextView statusText;
    private TextView moveCountText;
    private GridLayout gameBoard;
    private TextView[][] tiles = new TextView[4][4];  // Tablica do przechowywania referencji do pól

    private GameLogic gameLogic;  // Obiekt klasy GameLogic
    private GameApi gameApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        moveCountText = findViewById(R.id.moveCountText);  // Znajdź TextView dla liczby ruchów
        gameBoard = findViewById(R.id.gameBoard);

        // Inicjalizacja Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")  // Zmieniony adres dla emulatora.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gameApi = retrofit.create(GameApi.class);

        // Inicjalizacja planszy 4x4
        initBoard();
        initButtonGoToLogin();

        // Inicjalizacja bazy danych
        //GameScoreDatabase gameScoreDatabase = new GameScoreDatabase(this);

        // Inicjalizacja GameLogic
        gameLogic = new GameLogic(tiles, gameApi, this);  // Przekazujemy referencje do pól

        // Inicjalizacja SensorManager i czujników
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Rejestracja nasłuchiwaczy dla czujników
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        // Pobranie początkowej liczby ruchów z bazy danych i wyświetlenie
        //int initialMoveCount = gameScoreDatabase.getMoveCount("Player1");  // Pobranie liczby ruchów z bazy
        //moveCountText.setText("Liczba ruchów: " + initialMoveCount);

        // Dodanie początkowych bloczków
        gameLogic.addNewTile();
        gameLogic.addNewTile();

        gameLogic.getMoveCount();
    }

    private void initButtonGoToLogin() {
        Button button = findViewById(R.id.goToLoginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Przechodzimy do ekranu logowania
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initBoard() {
        // Inicjalizowanie pól w planszy
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                TextView tile = new TextView(this);
                tile.setLayoutParams(new GridLayout.LayoutParams());
                tile.setGravity(Gravity.CENTER);
                tile.setBackground(getResources().getDrawable(R.drawable.tile_background)); // Tło dla bloczków
                tile.setTextSize(32); // Rozmiar tekstu
                tile.setTextColor(getResources().getColor(android.R.color.white)); // Kolor tekstu
                tile.setText(""); // Początkowo puste

                tiles[row][col] = tile;
                gameBoard.addView(tile);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Twoja logika z pochyłem
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        // Obliczanie orientacji urządzenia
        if (gravity != null && geomagnetic != null) {
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientation);

                float pitch = orientation[1];
                float roll = orientation[2];

                if (Math.abs(pitch) < 0.2 && Math.abs(roll) < 0.2) {
                    statusText.setText("Brak pochyłu");
                    gameLogic.enableMoveWhenFlat(pitch,roll);
                    return;  // Brak ruchu, nie robimy nic
                }

                // Określenie kierunku ruchu
                if (pitch > 0.5) {
                    statusText.setText("Pochylenie w górę");
                    gameLogic.moveBlocks("UP");
                } else if (pitch < -0.5) {
                    statusText.setText("Pochylenie w dół");
                    gameLogic.moveBlocks("DOWN");
                } else if (roll > 0.5) {
                    statusText.setText("Pochylenie w prawo");
                    gameLogic.moveBlocks("RIGHT");
                } else if (roll < -0.5) {
                    statusText.setText("Pochylenie w lewo");
                    gameLogic.moveBlocks("LEFT");
                }

                //Aktualizacja liczby ruchów
                updateMoveCount();
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nie musimy nic robić
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // Funkcja aktualizująca licznik ruchów na UI
    private void updateMoveCount() {
        //int moveCount = gameLogic.getMoveCount();  // Pobieramy liczbę ruchów z GameLogic
        //moveCountText.setText("Liczba ruchów: " + moveCount);
    }

    // Funkcja aktualizująca licznik ruchów na UI
    public void updateMoveCountText(int moveCount) {
        moveCountText.setText("Liczba ruchów: " + moveCount);
    }
}

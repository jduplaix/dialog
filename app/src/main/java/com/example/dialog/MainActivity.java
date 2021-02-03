package com.example.dialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ViewAnimator;

import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //private AlertDialog dialog; // Variable globale = pas top

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* findViewById(R.id.button).setOnClickListener(v -> {
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Attention")
                    .setMessage("Voulez-vous changer la couleur?")
                    .setView(R.layout.color_dialog)
                    .setPositiveButton("Valider",(d, which) -> {
                        findViewById(R.id.root).setBackgroundColor(Color.rgb(
                                ((SeekBar) dialog.findViewById(R.id.r)).getProgress(),
                                ((SeekBar) dialog.findViewById(R.id.g)).getProgress(),
                                ((SeekBar) dialog.findViewById(R.id.b)).getProgress()
                        ));
                    })
                    .setNegativeButton("Annuler", null)
                    .create();
        });
         */

        // Dans cette version on instancie la vue seekbars lors du clic bouton
        findViewById(R.id.color_button).setOnClickListener(v -> {
            View contentView = LayoutInflater.from(this).inflate(R.layout.color_dialog, null);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Attention")
                    .setMessage("Voulez-vous changer la couleur?")
                    .setView(contentView)
                    .setPositiveButton("Valider",(d, which) -> {
                        findViewById(R.id.root).setBackgroundColor(Color.rgb(
                                ((SeekBar) contentView.findViewById(R.id.r)).getProgress(),
                                ((SeekBar) contentView.findViewById(R.id.g)).getProgress(),
                                ((SeekBar) contentView.findViewById(R.id.b)).getProgress()
                        ));
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        findViewById(R.id.download_button).setOnClickListener(v -> startDownload());
    }

    private void startDownload(){
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> {
                    //simulation de dl durée 1s
                    View root = findViewById(R.id.root);
                    if (new Random().nextBoolean()){
                        Snackbar.make(root, "Erreur lors du téléchargement", Snackbar.LENGTH_SHORT)
                                .setAction("Recommencer", v -> startDownload())
                                .show();
                    } else {
                        Snackbar.make(root, "Téléchargement terminé", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }, 1000);
    }
}
package com.example.dialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);

        // vérification couleur enregistrée précédemment + application si oui
        int savedColor = getSharedPreferences("MesPreferences", MODE_PRIVATE).getInt("mySavedBackgroundColor",0);
        if (savedColor != 0){
            findViewById(R.id.root).setBackgroundColor(savedColor);
        }

        // récupération + application couleur si changement orientation (= changement activity)
        if (savedInstanceState != null){
            int color = savedInstanceState.getInt("color", 0);
            if (color != 0){
                root.setBackgroundColor(color);
            }
        }

        // Dans cette version on instancie la vue seekbars lors du clic bouton
        findViewById(R.id.color_button).setOnClickListener(v -> {
            View contentView = LayoutInflater.from(this).inflate(R.layout.color_dialog, null);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Attention")
                    .setMessage("Voulez-vous changer la couleur?")
                    .setView(contentView)
                    .setPositiveButton("Valider",(d, which) -> {
                        root.setBackgroundColor(Color.rgb(
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

    // persistance de la couleur dans savedInstance pour la garder en changeant d'activity
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Drawable background = root.getBackground();
        if (background instanceof ColorDrawable){
            outState.putInt("color",((ColorDrawable) background).getColor());
        }
    }

    // Proposition récupération bgd color en cliquant sur back pour quitter
    @Override
    public void onBackPressed() {
        Drawable background = findViewById(R.id.root).getBackground();
        if (background instanceof ColorDrawable){
            new AlertDialog.Builder(this)
                    .setTitle("Au revoir")
                    .setMessage("Enregistrer la couleur de fond avant de quitter?")
                    .setPositiveButton("oui",(dialog, which) -> {
                        getSharedPreferences("MesPreferences", MODE_PRIVATE)
                                .edit()
                                .putInt("mySavedBackgroundColor", ((ColorDrawable) background).getColor())
                                .apply();
                        finish();
                    })
                    .setNegativeButton("non", (dialog, which) -> {
                        // Si on revient à la couleur par défaut, supprimer ColorDrawable de shared pref cf. plus haut
                        finish();
                    })
                    .show();
        }
    }
}
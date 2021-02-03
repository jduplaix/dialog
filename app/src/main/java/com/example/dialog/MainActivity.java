package com.example.dialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        String CHANNEL_ID = "canal notif";

        // création canal de notif (ne fait rien si existe déjà)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Nom de canal", importance);
            channel.setDescription("description de mon canal");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

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

        // Clic bouton couleur : appel seekbars + récup et changement bg color
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

        //Clic bouton dl : emulation dl avec usage Snackbar pour notif OK/KO
        findViewById(R.id.download_button).setOnClickListener(v -> startDownload());

        //Clic bouton Notif : emission notif avec le channel défini plus haut
        findViewById(R.id.notif_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("NOTIF")
                    .setContentText("Notification envoyée")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
        });
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
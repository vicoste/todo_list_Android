package com.projet.vicoste.todo;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.projet.vicoste.todo.modele.Objectif;
import com.projet.vicoste.todo.metier.ObjectifManager;

import java.text.SimpleDateFormat;

/**
 * Created by Lou on 07/02/2017.
 * Page descriptive d'un objectif précis
 */

public class DescriptionActivity extends AppCompatActivity {

    //***************************PARAMS**********************************
    /**
     * représente le code requis pour l'envoit de notifications
     */
    private static final int REQUEST_CODE_NOTIFICATION = 0;

    /**
     * Objectif vu en détail sur cette vue
     */
    private Objectif objectif;

    /**
     * EditText qui va contenir la description de l'objectif sur laquelle est basée la vue.
     * C'est ici que pourra être modifié le contenu de l'objectif
     */
    private EditText description;

    /**
     * TextView qui vont simplement contenir la date de début de l'événement et le titre de celui-ci
     */
    private TextView date, titre;

    private int calendarID;

    //****************************METHODS*******************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //calendarID =  getPreferences(MODE_PRIVATE).getInt(getString(R.string.calendarPreferences), -1);
        calendarID = 1;

        objectif = (ObjectifManager.getObjectifs(this, calendarID).get((Integer)(getIntent().getExtras().get("position"))));
        setContentView(R.layout.description_layout);
        description = (EditText)findViewById(R.id.et_description_objectif_contenu);
        description.setText(objectif.getDescription());
        date = (TextView) findViewById(R.id.tv_description_objectif_date);
        date.setText(SimpleDateFormat.getDateInstance().format(objectif.getDateDebut()).toString());
        titre = (TextView)findViewById(R.id.tv_description_objectif_titre);
        titre.setText(objectif.getNom());
        setListerners();
    }

    /**
     * Methode qui va mettre en place tout les listeners des buttons de la vue
     */
    private void setListerners(){
        FloatingActionButton buttonValidReturn = (FloatingActionButton) findViewById(R.id.bt_description_valid_obj);
        FloatingActionButton buttonDeleteReturn = (FloatingActionButton) findViewById(R.id.bt_description_delete_obj);
        buttonDeleteReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteObjInCalendar())
                createValidateNotification();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        buttonValidReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateObjInCalendar();
                finish();
            }
        });
    }

    /**
     * Methode qui va supprimer l'evenement correspondant à l'objectif dans le calendrier principal
     */
    private boolean deleteObjInCalendar(){
            Uri deleteUri = null;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, objectif.getId());
            int rows = getContentResolver().delete(deleteUri, null, null);
            Log.e("Row deleted ", String.valueOf(rows));
            return ObjectifManager.deleteObjectif(objectif);
    }

    /**
     * Methode qui va mettre à jour l'evenement correspondant a l'objectif dans le calendrier principal
     */
    private void updateObjInCalendar(){
            objectif.setDescription(description.getText().toString());
            ContentValues values = new ContentValues();
            Uri updateUri = null;
            values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString());
            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, objectif.getId());
            getContentResolver().update(updateUri, values, null, null); //retourne le nombre de rows updated
    }

    /**
     * Methode qui cree une notification lors de la creation d'un objectif
     */
    private void createValidateNotification(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_NOTIFICATION,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.clock)
                        .setContentTitle("Quel talent !")
                        .setContentText("Encore un objectif de réussit ? Tu ne t'arrêtes plus !")
                        .setContentIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(42, mBuilder.build());
    }




}


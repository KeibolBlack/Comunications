package com.sdm.seminarios.comunicaciones;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;


public class ProgressBarActivity_handlerconRunnable extends Activity {
    ProgressBar barraProgreso = null;
    boolean isThreadRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        barraProgreso = (ProgressBar) findViewById(R.id.progressBar);
        barraProgreso.setMax(100);
    }

    /** A COMPLETAR **
     * Definir si fuera necesario un Handler
     */




    /**
     * Código que debería activarse fcuando lo solicite el hilo secundario
     */
    private void ActualizarBarraProgreso(){
        barraProgreso.incrementProgressBy(10);
        if (barraProgreso.getProgress()==barraProgreso.getMax()){
            Toast.makeText(getApplicationContext(), "Hemos llegado al 100%", Toast.LENGTH_LONG).show();
            isThreadRunning = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        barraProgreso.setProgress(0);
        isThreadRunning = true;

        new Thread(){
            public void run(){
                try{

                    while (isThreadRunning){
                        Thread.sleep(1000);
                        /** A COMPLETAR **
                         * Solicitar al hilo principal la actualización de la barra de progreso
                         */

                    }

                }catch(Throwable t){

                }
            }

        }.start();


    }
}

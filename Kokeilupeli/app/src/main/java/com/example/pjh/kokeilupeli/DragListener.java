package com.example.pjh.kokeilupeli;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.TreeMap;

/**
 * Created by Mohog on 29.9.2015 && MSa on 2.10.2015
 */
public class DragListener implements View.OnDragListener, View.OnTouchListener {

    private static View siirtoKuvaView;
    MainActivity paaohjelma;
    TextView txt;
    RelativeLayout paaleiska;
    ImageView alkukuva, loppukuva;

    public DragListener(MainActivity paaohjelma) {
        paaleiska = (RelativeLayout) paaohjelma.findViewById(R.id.paaleiska);
        this.paaohjelma = paaohjelma;
        txt = (TextView) paaohjelma.findViewById(R.id.text);
    }

    private void siirra(View vMista, View vMihin) {
        // pelaajaa ei tarvita parametrina, mehän tiedetään kumpi on pelivuorossa
        paaohjelma.setImage((ImageView) vMihin, paaohjelma.pelaaja);
        paaohjelma.setImage((ImageView) vMista, paaohjelma.TYHJA);
        paaohjelma.vaihdaPelaaja();
    }

    private int sijaintiGridilla(View v) {
        // palauttaa tiedon siitä monesko ko. kuva on gridillä
        int koko = paaohjelma.gridi.getChildCount();
        for (int i = 0; i < koko; i++) {
            if (paaohjelma.gridi.getChildAt(i) == v) {
                return i;
            }
        }
        return -1; // kaatuu ohjelmointivirheeseen, mutta eihän tänne koskaan tulla ....
    }

    public boolean kelpaakoSiirto(View mista, View mihin) {
        int kohdeId = sijaintiGridilla(mihin);
        int siirtokuvaId = sijaintiGridilla(mista);
        // vertailussa käytetään gridin rivien tai sarakkeiden määrää
        int sarakemaara = paaohjelma.gridi.getColumnCount();
        final int VINOTTAIN = 1;
        if (mista.getTag().equals(paaohjelma.PELAAJA1)) {
            if ((siirtokuvaId + sarakemaara) == kohdeId) {
                // Suora siirto pitää tehdä tyhjään ruutuun
                if (mihin.getTag().equals(paaohjelma.TYHJA)) {
                    return true;
                } else {
                    return false;
                }
            } else if (((siirtokuvaId + sarakemaara - VINOTTAIN) == kohdeId || (siirtokuvaId + sarakemaara + VINOTTAIN) == kohdeId)) {
                // Siirto vinottain pitää tehdä vastustajan napin päälle
                if (mihin.getTag().equals(paaohjelma.PELAAJA2)) {
                    return true;
                } else {
                    return false;
                }
            } else
                return false;
        } else {
            // on vain kaksi pelaajaa, joten täytyy olla PELAAJA2
            if ((siirtokuvaId - sarakemaara) == kohdeId) {
                if (mihin.getTag().equals(paaohjelma.TYHJA)) {
                    return true;
                } else {
                    return false;
                }
            } else if (((siirtokuvaId - sarakemaara + VINOTTAIN) == kohdeId || (siirtokuvaId - sarakemaara - VINOTTAIN) == kohdeId)) {
                if (mihin.getTag().equals(paaohjelma.PELAAJA1)) {
                    return true;
                } else {
                    return false;
                }
            } else
                return false;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        String tag;
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                paaleiska.invalidate();
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                return true;
            case DragEvent.ACTION_DROP:
                if (kelpaakoSiirto(siirtoKuvaView, v)) {
                    siirra(siirtoKuvaView, v);
                    return true;
                } else {
                    paaohjelma.setImage((ImageView) siirtoKuvaView, paaohjelma.pelaaja);  // siirrettävä nappula tyhjästä takaisin
                    return false;
                }
        }
        return false;
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData dragData = ClipData.newPlainText("", "");
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

        if (view.getTag().equals(paaohjelma.TYHJA)) {
            return false;
        } else {
            if (paaohjelma.pelaaja.equals(view.getTag())) {
                siirtoKuvaView = view;
                view.startDrag(dragData, myShadow, null, 0);
                ((ImageView) view).setImageResource(R.drawable.tyhja); // siirrettävä nappula tyhjäksi
                return true;
            } else
                return false;
        }

    }
}

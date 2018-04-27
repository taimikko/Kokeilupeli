package com.example.pjh.kokeilupeli;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "kokeilupeli";
    public GridLayout gridi;

    static final String STATUS = "status";
    static final String PELAAJA = "pelaaja";
    static final String PELAAJA1 = "1";
    static final String PELAAJA2 = "2";
    static final String TYHJA = " ";
    static final String KOKO = "Ruudukon koko";
    int ruudukonKoko = 6; // Ruudukon yhden sivun koko

    ImageView[] ruutu; // Iaget (nappulat ja tyhjät ruudut) taulukossa
    DragListener listener;

    public String pelaaja = PELAAJA1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize two SharedPreferences objects
        prefs = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = prefs.edit();
        listener = new DragListener(this);
        setImageObjects();
        setImagesToDefault();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String status = getStatus();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.save1: // talleta tilanne
                editor.putString(STATUS, status);
                editor.putString(PELAAJA, pelaaja);
                editor.putInt(KOKO, ruudukonKoko);
                editor.commit();
                break;
            case R.id.reset1: // alusta peli
                startGame(ruutu[0]); // mikä tahansa View käy parametriksi, kun sitä ei kuitenkaan käytetä.
                break;
            case R.id.restore1: // palauta
                ruudukonKoko = prefs.getInt(KOKO, 6);
                status = prefs.getString(STATUS, "111111                    222222");
                restoreImages(status);
                pelaaja = prefs.getString(PELAAJA, PELAAJA1);
                break;
            case R.id.more1: // lisää nappuloita
                ruudukonKoko++;
                // alustetaan koko peli uudestaan
                tyhjennaRuudukko();
                setImagesToDefault();
                break;
            case R.id.less1: // lisää nappuloita
                ruudukonKoko--;
                // alustetaan koko peli uudestaan
                tyhjennaRuudukko();
                setImagesToDefault();
                break;
            case R.id.hello: // hello world lopettaa ohjelman
                finish();
                break;
            default: // tänne ei pitäisi tulla
                break;
        }
        return true;
    }

    private String getStatus() {
        // käydään näyttö läpi ja katsotaan kenen nappula on missäkin ruudussa.
        // kunkin ruudun kohdalle annetaaan joko PELAAJA1, PELAAJA2 tai  " "(tyhjä)
        String status = "";
        for (int i = 0; i < ruutu.length; i++) {
            status += ruutu[i].getTag();
        }
        return status;
    }

    public void startGame(View v) {
        setImagesToDefault();
        pelaaja = PELAAJA1;
    }

    public void vaihdaPelaaja() {
        if (pelaaja.equals(PELAAJA1))
            pelaaja = PELAAJA2;
        else
            pelaaja = PELAAJA1;
    }

    public void setListener(ImageView iv) {
        iv.setOnTouchListener(listener);
        iv.setOnDragListener(listener);
    }

    public void setListeners() {
        for (int i = 0; i < ruutu.length; i++) {
            setListener(ruutu[i]);
        }
    }

    public void setImage(ImageView iv, String tag) {
        iv.setTag("" + tag);
        switch (tag) {
            case PELAAJA1:
                iv.setImageResource(R.drawable.pelaaja1); // R.drawable.goldbutton
                break;
            case PELAAJA2:
                iv.setImageResource(R.drawable.pelaaja2); // R.drawable.silverbutton
                break;
            default:
                iv.setImageResource(R.drawable.tyhja); // abctyhjä =  @drawable/abc_btn_colored_material

                break;
        }
    }

    public void restoreImages(String str) {
        // jos ruudukon kokoa on muutettu, nin pitää alustaa koko taulukko ensin ja sitten vasta palauttaa
        tyhjennaRuudukko();
        for (int i = 0; i < ruutu.length; i++) {
            setImage(ruutu[i], "" + str.charAt(i));
        }
    }

    public void tyhjennaRuudukko() {
        if (ruutu.length != ruudukonKoko) {
            gridi.removeAllViews(); // toivottavasti tyhjentää kaikki kuvat
            gridi.setColumnCount(ruudukonKoko);
            gridi.setRowCount(ruudukonKoko);
            setImageObjects();
        }
    }

    private int laskeKuvanKoko() {
        // nappuloiden koko pitäisi laskea käytettävissä olevasta ruudun tilasta: minkä kokoisia nappeja mahtuu?
        DisplayMetrics displaymetrics = new DisplayMetrics();
        gridi = (GridLayout) findViewById(R.id.gridLayout1);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.paaleiska);

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int koko;
        int pad = rl.getPaddingLeft()+rl.getPaddingRight(); // pitäisi vielä vähentää padding left ja padding right

        if (width < height) {
            koko = width - pad;
        } else {
            koko = height -pad;
        }
        koko = koko / ruudukonKoko;
        return koko;
    }

    public void setImageObjects() {
        // lisätään gridille kuvia (ImageView) joihin tulee joskus nappula tai tyhjä
        ruutu = new ImageView[ruudukonKoko * ruudukonKoko];  // vanha jää jonnekin roikkumaan roskana...
        gridi = (GridLayout) findViewById(R.id.gridLayout1);
        GridLayout.LayoutParams lp;
        // nappuloiden koko pitäisi laskea käytettävissä olevasta ruudun tilasta: minkä kokoisia nappeja mahtuu?
        int koko = laskeKuvanKoko();

        for (int i = 0; i < ruutu.length; i++) {
            ruutu[i] = new ImageView(this);
            lp = new GridLayout.LayoutParams();
            lp.width = koko;
            lp.height = koko;
            ((ImageView) ruutu[i]).setLayoutParams(lp);
            setListener(ruutu[i]);
            gridi.addView(ruutu[i]);
        }
    }

    public void setImagesToDefault() {
        for (int i = 0; i < ruudukonKoko; i++) {
            setImage(ruutu[i], PELAAJA1);
        }
        for (int i = ruudukonKoko; i < ruutu.length - ruudukonKoko; i++) {
            setImage(ruutu[i], TYHJA);
        }
        for (int i = ruutu.length - ruudukonKoko; i < ruutu.length; i++) {
            setImage(ruutu[i], PELAAJA2);
        }
    }

}

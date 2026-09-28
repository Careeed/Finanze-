package it.simone.financeapp;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, content; android.content.SharedPreferences prefs;
    int blue=Color.rgb(21,101,192), green=Color.rgb(46,125,50), red=Color.rgb(198,40,40);
    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("finance",0); showHome();}
    TextView title(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(22);t.setTextColor(Color.DKGRAY);t.setPadding(16,20,16,12);return t;}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);return b;}
    void base(String name){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.WHITE);
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(8,0,8,0);bar.setBackgroundColor(blue);
        TextView t=new TextView(this);t.setText(name);t.setTextColor(Color.WHITE);t.setTextSize(20);t.setPadding(8,14,8,14);bar.addView(t,new LinearLayout.LayoutParams(0,-2,1));
        Button home=btn("⌂"); home.setTextColor(Color.WHITE); home.setBackgroundColor(Color.TRANSPARENT); home.setOnClickListener(v->showHome()); bar.addView(home,new LinearLayout.LayoutParams(55,55));
        root.addView(bar); ScrollView sv=new ScrollView(this); content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(12,8,12,24);sv.addView(content);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
    }
    void addButton(String text, View.OnClickListener l){Button b=btn(text);b.setOnClickListener(l);content.addView(b,new LinearLayout.LayoutParams(-1,60));}
    void showHome(){base("Entrate & Spese");content.addView(title("Gestione familiare"));
        TextView info=new TextView(this);info.setText("Dati importati dal file Excel. Le nuove operazioni salvate nell'app restano sul telefono in questa versione.");info.setPadding(16,8,16,20);content.addView(info);
        addButton("➕ Nuova spesa",v->newExpense()); addButton("➕ Nuova entrata",v->newIncome());
        addButton("📊 Entrate",v->showSheet("Income")); addButton("🧾 Spese Casa",v->showSheet("Expenses da aprile 2025"));
        addButton("👤 Spese Simo",v->showSheet("Expenses Simo")); addButton("👤 Spese Paola",v->showSheet("Expenses Pao"));
        addButton("🔒 Resoconto Simo",v->locked("Simo","Resoconto Simo")); addButton("🔒 Resoconto Paola",v->locked("Paola","Resoconto Pao"));
        addButton("⚙️ Impostazioni password",v->passwordSettings());
    }
    void showSheet(String sheet){base(sheet); try{String json=readAsset("workbook.json"); String marker="\""+sheet.replace("\"","\\\"")+"\":["; int p=json.indexOf(marker); if(p<0){content.addView(title("Foglio non trovato"));return;} int start=p+marker.length(); int end=json.indexOf("]],\"",start); if(end<0) end=Math.min(json.length(),start+200000); String raw=json.substring(start,Math.min(end+2,json.length()));
            TextView h=new TextView(this);h.setText("Dati importati — visualizzazione semplificata");h.setPadding(8,8,8,12);content.addView(h);
            // Extract rows approximately from JSON; display readable row text.
            String[] rows=raw.split("\\],\\["); int shown=0; for(String r:rows){ if(shown++>180)break; String x=r.replace("[["," ").replace("]]"," ").replace("\\\"","").replace("null","").replace(",", "   "); if(x.trim().length()>1){TextView tv=new TextView(this);tv.setText(x.trim());tv.setTextSize(13);tv.setPadding(8,7,8,7);content.addView(tv);}}
        }catch(Exception e){content.addView(title("Errore lettura dati: "+e.getMessage()));}}
    String readAsset(String f)throws Exception{InputStream is=getAssets().open(f);ByteArrayOutputStream o=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=is.read(b))>0)o.write(b,0,n);return o.toString("UTF-8");}
    String hash(String s)throws Exception{byte[] d=MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"));StringBuilder x=new StringBuilder();for(byte q:d)x.append(String.format("%02x",q));return x.toString();}
    void locked(String who,String sheet){String key="pwd_"+who.toLowerCase();if(!prefs.contains(key)){passwordSettings();return;} final EditText in=new EditText(this);in.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);in.setHint("Password");new AlertDialog.Builder(this).setTitle("Resoconto "+who).setMessage("Inserisci la password").setView(in).setNegativeButton("Annulla",null).setPositiveButton("Apri",(d,w)->{try{if(hash(in.getText().toString()).equals(prefs.getString(key,"")))showSheet(sheet);else Toast.makeText(this,"Password errata",Toast.LENGTH_SHORT).show();}catch(Exception e){}}).show();}
    void passwordSettings(){base("Password");content.addView(title("Proteggi i due resoconti separatamente")); addPasswordField("Simo","pwd_simo");addPasswordField("Paola","pwd_paola");}
    void addPasswordField(String who,String key){Button b=btn("Imposta / cambia password Resoconto "+who);b.setOnClickListener(v->{EditText in=new EditText(this);in.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);in.setHint("Nuova password");new AlertDialog.Builder(this).setTitle("Password "+who).setView(in).setNegativeButton("Annulla",null).setPositiveButton("Salva",(d,w)->{try{String p=in.getText().toString();if(p.length()<4){Toast.makeText(this,"Usa almeno 4 caratteri",Toast.LENGTH_SHORT).show();return;}prefs.edit().putString(key,hash(p)).apply();Toast.makeText(this,"Password salvata",Toast.LENGTH_SHORT).show();}catch(Exception e){}}).show();});content.addView(b,new LinearLayout.LayoutParams(-1,60));}
    void newExpense(){transactionDialog(false);} void newIncome(){transactionDialog(true);}
    void transactionDialog(boolean income){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(20,0,20,0);EditText amount=new EditText(this);amount.setHint("Importo €");amount.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);EditText cat=new EditText(this);cat.setHint(income?"Tipo entrata":"Categoria / descrizione");EditText person=new EditText(this);person.setHint("Simo / Paola / Casa");l.addView(amount);l.addView(cat);l.addView(person);new AlertDialog.Builder(this).setTitle(income?"Nuova entrata":"Nuova spesa").setView(l).setNegativeButton("Annulla",null).setPositiveButton("Salva",(d,w)->{String rec=(income?"ENTRATA":"SPESA")+" | "+person.getText()+" | "+cat.getText()+" | € "+amount.getText();String old=prefs.getString("transactions","");prefs.edit().putString("transactions",old+rec+"\n").apply();Toast.makeText(this,"Salvato",Toast.LENGTH_SHORT).show();}).show();}
}

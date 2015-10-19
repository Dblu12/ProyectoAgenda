/*
    ME QUDÉ INTENTANDO ACTUALIZAR LOS NUMEROS DE UN CONTACTO EN LA ACTIVIDAD PRINCIPAL
    PREGUNTAR LA DUDA DE COMO HACER QUE LA ACTIVIDAD PARE CUANDO OTRA ENTRA EN EJECUCION.
 */


package com.example.dam.listadecontactos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Principal extends AppCompatActivity {

    private ListView lv;
    private ClaseAdaptadorContactos a,b;
    private List<Contacto> l;
   // private ImageView iv1, iv2;
    private Intent i;
    private EditText etBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menucontextual, menu);
        }
    }
    public boolean onContextItemSelected(MenuItem item) {

        long id = item.getItemId();
        AdapterView.AdapterContextMenuInfo vistaInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion = vistaInfo.position;

        if (id == R.id.mnborrar) {


            borrarDeAdaptador(posicion, (ClaseAdaptadorContactos) lv.getAdapter());
            return true;
        } else if (id == R.id.mneditar) {
            
            Intent intent= new Intent(this, EditarContacto.class);
            Bundle bundle= new Bundle();
            bundle.putInt("contacto", posicion);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
           // this.onPause();
            // a.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void borrarDeAdaptador(int pos, ClaseAdaptadorContactos c){
        c.borrar(pos);
        if(!c.equals(a))
            a.borrar(pos);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent in= new Intent(this, AnadirContacto.class);
            startActivityForResult(in, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<Contacto> getListaContactos() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1", "1"};
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        while (cursor.moveToNext()) {
            contacto = new Contacto();
            contacto.setId((int) cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));
            lista.add(contacto);
        }
        return lista;
    }

    public Contacto getContacto(int pos){
        Contacto a= getListaContactos().get(pos);
        return a;
    }

    public List<String> getListaTelefonos(long id) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = null;
        String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos[] = new String[]{id + ""};
        String orden = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        List<String> lista = new ArrayList<>();
        String numero;
        while (cursor.moveToNext()) {
            numero = cursor.getString(indiceNumero);
            lista.add(numero);
        }
        return lista;
    }

    private void init() {


        lv = (ListView) findViewById(R.id.listView);

        l = getListaContactos();
        Collections.sort(l);

        for(Contacto c: l){
            c.setTelefonos(getListaTelefonos(c.getId()));
            Log.v("Contacto ", c.getNombre());
        }

        a = new ClaseAdaptadorContactos(this, R.layout.item, l);

        lv.setAdapter(a);


        registerForContextMenu(lv);
        i = new Intent(this, EditarTelefonosContacto.class);


        lv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    a.notifyDataSetChanged();
                }

            }
        });
        etBus= (EditText) findViewById(R.id.etBuscar);

    }


    public void mostrar(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle(R.string.telefonos);
        LayoutInflater inflater= LayoutInflater.from(this);
        int res= R.layout.numeros_contacto;

        ArrayList<String> telefonos_contacto= (ArrayList<String>) l.get((Integer) v.getTag()).getTelefonos();

        Log.v("",v.getTag()+"");
        Bundle bun= new Bundle();
        // bun.putStringArrayList("telefonos", telefonos_contacto);
        // bun.putInt("telefonos", (Integer) v.getTag());
        bun.putSerializable("contacto", l.get((Integer) v.getTag()));
        i.putExtras(bun);

        final View vista = inflater.inflate(R.layout.numeros_contacto, null);

        final TextView b= (TextView) vista.findViewById(R.id.tvInfo);
        for(String num: telefonos_contacto){

            b.append(num + "\n");
        }
        alert.setView(vista);

        alert.setNeutralButton(R.string.str_editar,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                        startActivityForResult(i, 1);
                        //this.onPause();

                        // a.notifyDataSetChanged();
                        // La actividad continua aun cuando se ha lanzado otra actividad
                    }
                });
        alert.setNegativeButton(R.string.cancelarbt, null);



        alert.show();
    }

    public List<Contacto> getListaContactosPublic(){
        return l;
    }

    public ClaseAdaptadorContactos getClaseAdaptadorContatos(){
        return a;
    }


    public void adaptadorCambio(){
        a = new ClaseAdaptadorContactos(this, R.layout.item, l);

        lv.setAdapter(a);
    }
/*
    public void actualizar(View v){
        a.notifyDataSetChanged();
        for(Contacto la: l){
            Log.v("aa",la.getNombre());
        }

        Log.v("a", "a");
    }

    public void cambiarNombreContacto(String nom, int pos){
        l.get(pos).setNombre(nom);
    }
*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Contacto  contacto = (Contacto) data.getExtras().getSerializable("contacto");
                System.out.println(contacto);
                for(Contacto c : l){
                    if(c.getId()==contacto.getId()) {
                        c.setTelefonos(contacto.getTelefonos());
                        c.setNombre(contacto.getNombre());
                        a.notifyDataSetChanged();
                        //a = new ClaseAdaptadorContactos(this, R.layout.activity_principal, (ArrayList<Contacto>) l);
                        //lv.setAdapter(a);
                    }
                    System.out.println(c);
                }

            }else if(resultCode == 2){
                Contacto  contacto = (Contacto) data.getExtras().getSerializable("contacto");
                l.add(contacto);
                Collections.sort(l);
                a.notifyDataSetChanged();
            }
        }
    }

    /*@Override
    protected void onResume(){
        super.onResume();
        a.notifyDataSetChanged();
        a = new ClaseAdaptadorContactos(this, R.layout.activity_principal, (ArrayList<Contacto>) l);
        lv.setAdapter(a);
    }
    */

    public void buscar(View v){
        String buscar= etBus.getText().toString();
        ArrayList<Contacto> copia= new ArrayList<>();
        if(buscar.isEmpty()){
            a = new ClaseAdaptadorContactos(this, R.layout.item, l);

            lv.setAdapter(a);
        }else{
            for(Contacto con:l){
                if(con.getNombre().equalsIgnoreCase(buscar)){
                    copia.add(con);
                }
            }
            b = new ClaseAdaptadorContactos(this, R.layout.item, copia);

            lv.setAdapter(b);
        }
    }
}

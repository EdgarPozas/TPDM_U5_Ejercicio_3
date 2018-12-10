package com.example.edgar.tpdm_u5_ejercicio_3_edgarefrenpozasbogarin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class Lista_Dispositivos extends AppCompatActivity {

    private BluetoothAdapter adaptador;
    public ArrayList<BluetoothDevice> dispositivos;
    private ListView lista;
    private CheckBox check;
    private Lista_Dispositivos_Receiver lista_dispositivos_receiver;
    private static Lista_Dispositivos lista_dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_dispositivos);
        lista=findViewById(R.id.elementos);
        check=findViewById(R.id.servidor);
        lista_dispositivos_receiver=new Lista_Dispositivos_Receiver();
        lista_dis=this;
        dispositivos=new ArrayList<>();
        listar_dispositivos();
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice dispositivo=dispositivos.get(position);

                if(dispositivo.getBondState()==BluetoothDevice.BOND_NONE){
                    mensaje("Intentando parear");
                    try{
                        Class c=Class.forName("android.bluetooth.BluetoothDevice");
                        Method m=c.getMethod("createBond");
                        Boolean b=(Boolean) m.invoke(dispositivo);
                        if(b){
                            mensaje("Pareado correcto");
                            actualizar_lista();
                        }else{
                            mensaje("Pareado incorrecto");
                        }
                    }catch(Exception ex){

                    }
                    return;
                }

                Intent intent= new Intent(Lista_Dispositivos.this,Enviar_Mensajes.class);
                Bundle b=new Bundle();
                b.putParcelable("device",dispositivo);
                b.putBoolean("servidor",check.isChecked());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(lista_dispositivos_receiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(lista_dispositivos_receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(adaptador.isEnabled()){
                Toast.makeText(this,"Bluetooth encendido",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Bluetooth no encendido",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validaciones(){
        adaptador = BluetoothAdapter.getDefaultAdapter();
        if (adaptador == null) {
            mensaje("No hay bluetooth");
            return false;
        }
        if(!adaptador.isEnabled()){
            Intent enblue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enblue,1);
            return false;
        }
        return true;
    }

    private void listar_dispositivos() {
        if (validaciones()) {
            dispositivos.clear();
            lista.setAdapter(null);
            Set<BluetoothDevice> dispos= adaptador.getBondedDevices();
            for (BluetoothDevice de:dispos){
                dispositivos.add(de);
            }
            if (adaptador.isDiscovering()) {
                mensaje("Ya se encuentra buscando dispositivos");
                return;
            }
            actualizar_lista();
            adaptador.startDiscovery();
        }
    }
    public void actualizar_lista() {
        ArrayList<String> datos=new ArrayList<>();
        for (BluetoothDevice dev:dispositivos){
            datos.add(dev.getAddress()+"\n"+dev.getName()+(dev.getBondState()==BluetoothDevice.BOND_BONDED?"-PAREADO":""));
        }
        ArrayAdapter<String> arr_adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,datos);
        lista.setAdapter(arr_adapter);
    }

    private void mensaje(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void actualizar(View view) {
        listar_dispositivos();
    }

    public static Lista_Dispositivos getInstance(){
        return lista_dis;
    }
}

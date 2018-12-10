package com.example.edgar.tpdm_u5_ejercicio_3_edgarefrenpozasbogarin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class Enviar_Mensajes extends AppCompatActivity {

    private BluetoothDevice dispositivo;
    private BluetoothServerSocket server;
    private BluetoothSocket socket;
    private BluetoothAdapter adapter;
    public ArrayList<String> mensajes;
    private boolean servidor;
    private ListView lista;
    private EditText mensaje;
    private static final String aplicacion = "TPDM_U5_Ejercicio_3_EdgarEfrenPozasBogarin";
    private static final UUID mi_uuid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private Manejador_Mensajes manejador_mensajes;
    private static Enviar_Mensajes enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_mensajes);
        lista=findViewById(R.id.lista_mensajes);
        adapter=BluetoothAdapter.getDefaultAdapter();
        mensaje=findViewById(R.id.mensaje);
        servidor=getIntent().getExtras().getBoolean("servidor");
        enviar=this;
        mensajes=new ArrayList<>();
        try {
            if(servidor) {
                server = adapter.listenUsingRfcommWithServiceRecord(aplicacion, mi_uuid);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            try {
                                socket = server.accept();
                                if(socket.isConnected()) {
                                    manejador_mensajes = new Manejador_Mensajes(socket,Enviar_Mensajes.this);
                                    manejador_mensajes.start();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (socket != null) {
                                try {
                                    server.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                        }
                    }
                }).start();
                //System.out.println(socket);
                //manejador_mensajes=new Manejador_Mensajes(socket);
                mensaje("Servidor creado");
            }
            else{
                dispositivo=getIntent().getExtras().getParcelable("device");
                socket=dispositivo.createRfcommSocketToServiceRecord(mi_uuid);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.connect();
                            if(socket.isConnected()) {
                                manejador_mensajes = new Manejador_Mensajes(socket,Enviar_Mensajes.this);
                                manejador_mensajes.start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                //manejador_mensajes=new Manejador_Mensajes(socket);

                mensaje("Cliente conectado");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void enviar(View view) {
        String msg=mensaje.getText().toString();
        manejador_mensajes.mandar_mensaje(msg);
        mensajes.add("YO:\n"+msg);
        actualizar_lista();
        mensaje.setText("");
    }
    public void actualizar_lista(){
        System.out.println(lista);
        lista.setAdapter(null);
        ArrayAdapter adap=new ArrayAdapter(this,android.R.layout.simple_list_item_1,mensajes);
        lista.setAdapter(adap);
        seleccionar_final();
    }
    private void seleccionar_final() {
        lista.post(new Runnable() {
            @Override
            public void run() {
                lista.setSelection(lista.getCount() - 1);
            }
        });
    }
    private void mensaje(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public static Enviar_Mensajes getInstance(){
        return enviar;
    }
}

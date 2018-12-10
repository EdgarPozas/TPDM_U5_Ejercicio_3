package com.example.edgar.tpdm_u5_ejercicio_3_edgarefrenpozasbogarin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;

public class Manejador_Mensajes extends Thread {

    private OutputStream salida;
    private InputStream entrada;
    private BluetoothSocket socket;
    private BluetoothAdapter adaptador;
    private Enviar_Mensajes enviar_mensajes;

    public Manejador_Mensajes(BluetoothSocket socket,Enviar_Mensajes enviar_mensajes) {
        this.socket = socket;
        try {
            entrada = socket.getInputStream();
            salida = socket.getOutputStream();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = entrada.read(buffer);
                final String msg=new String(buffer,0,bytes);
                Enviar_Mensajes.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Enviar_Mensajes.getInstance().mensajes.add(socket.getRemoteDevice().getName()+":\n"+ msg);
                        Enviar_Mensajes.getInstance().actualizar_lista();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void mandar_mensaje(String mensaje) {
        try {
            salida.write(mensaje.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

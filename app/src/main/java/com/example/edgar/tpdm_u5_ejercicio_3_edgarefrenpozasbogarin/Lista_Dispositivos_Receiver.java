package com.example.edgar.tpdm_u5_ejercicio_3_edgarefrenpozasbogarin;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class Lista_Dispositivos_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
            BluetoothDevice dev=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Lista_Dispositivos.getInstance().dispositivos.add(dev);
            Lista_Dispositivos.getInstance().actualizar_lista();
        }
    }
}

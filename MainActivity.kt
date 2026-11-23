package com.haskenlink.offline

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.UUID
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private val adapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val uuid: UUID = UUID.fromString("6d6f6269-6c65-6861-736b-656e6c696e6b")
    private lateinit var status: TextView
    private lateinit var messages: TextView
    private lateinit var deviceList: ListView
    private lateinit var input: EditText
    private lateinit var devices: List<BluetoothDevice>
    @Volatile private var socket: BluetoothSocket? = null
    @Volatile private var writer: PrintWriter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        requestBluetoothPermissions()
        startListening()
    }

    private fun buildUi() {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24,24,24,24) }
        layout.addView(TextView(this).apply { text = "HASKEN LINK • Offline chat"; textSize = 24f })
        status = TextView(this).apply { text = "Bluetooth: check permission / enable"; textSize = 16f }
        layout.addView(status)
        layout.addView(Button(this).apply { text = "Refresh paired devices"; setOnClickListener { showPairedDevices() } })
        deviceList = ListView(this); layout.addView(deviceList, LinearLayout.LayoutParams(-1,0,1f))
        deviceList.setOnItemClickListener { _, _, position, _ -> connect(devices[position]) }
        messages = TextView(this).apply { text = "Messages will appear here\n"; textSize = 16f }
        val scroll = ScrollView(this).apply { addView(messages) }; layout.addView(scroll, LinearLayout.LayoutParams(-1,0,1f))
        input = EditText(this).apply { hint = "Write a message" }
        layout.addView(input)
        layout.addView(Button(this).apply { text = "Send"; setOnClickListener { sendMessage() } })
        setContentView(layout)
    }

    private fun requestBluetoothPermissions() {
        val required = if (Build.VERSION.SDK_INT >= 31) arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val missing = required.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), 10) else showPairedDevices()
    }

    private fun hasConnectPermission() = Build.VERSION.SDK_INT < 31 || checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

    private fun showPairedDevices() {
        val bt = adapter
        if (bt == null) { status.text = "This phone has no Bluetooth adapter"; return }
        if (!hasConnectPermission()) { status.text = "Bluetooth permission required"; return }
        if (!bt.isEnabled) { status.text = "Turn Bluetooth on, then refresh"; return }
        try {
            devices = bt.bondedDevices.toList()
            deviceList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devices.map { "${it.name ?: "Unnamed"}\n${it.address}" })
            status.text = "Ready • ${devices.size} paired devices. Tap a device to connect."
        } catch (e: SecurityException) { status.text = "Bluetooth permission denied" }
    }

    private fun startListening() {
        val bt = adapter ?: return
        thread {
            try {
                if (Build.VERSION.SDK_INT >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return@thread
                val server: BluetoothServerSocket = bt.listenUsingRfcommWithServiceRecord("HASKEN LINK", uuid)
                while (!isFinishing) {
                    val incoming = server.accept()
                    attachSocket(incoming)
                }
            } catch (_: Exception) { runOnUiThread { status.text = "Listener stopped; reopen app or refresh" } }
        }
    }

    private fun connect(device: BluetoothDevice) {
        thread {
            try {
                if (Build.VERSION.SDK_INT >= 31 && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return@thread
                adapter?.cancelDiscovery()
                val s = device.createRfcommSocketToServiceRecord(uuid)
                s.connect(); attachSocket(s)
            } catch (e: Exception) { runOnUiThread { status.text = "Connection failed: ${e.localizedMessage}" } }
        }
    }

    private fun attachSocket(s: BluetoothSocket) {
        socket = s
        writer = PrintWriter(s.outputStream, true)
        runOnUiThread { status.text = "Connected to ${s.remoteDevice.name ?: "device"}"; addMessage("Connected") }
        thread {
            try {
                val reader = BufferedReader(InputStreamReader(s.inputStream))
                while (true) {
                    val line = reader.readLine() ?: break
                    runOnUiThread { addMessage("Friend: $line") }
                }
            } catch (_: Exception) { runOnUiThread { addMessage("Connection ended") } }
        }
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        val w = writer
        if (w == null) { status.text = "Connect to a paired device first"; return }
        thread {
            try { w.println(text); runOnUiThread { addMessage("You: $text"); input.setText("") } }
            catch (e: Exception) { runOnUiThread { status.text = "Send failed" } }
        }
    }

    private fun addMessage(text: String) { messages.append(text + "\n") }
}

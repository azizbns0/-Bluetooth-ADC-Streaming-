# ESP32 Bluetooth ADC Monitor

This project implements an **ESP32-based Bluetooth ADC streaming system**. The firmware reads analog voltage samples from a floating GPIO pin (e.g., GPIO34), processes the data, and streams the results via Bluetooth to a mobile device. A mobile plotting app or terminal can be used to visualize the streamed values.

---

## 🚀 Features

- Samples ADC at **10Hz** (every 100 ms)
- Averages 4 readings per sample to reduce noise
- Sends packets of 10 samples every second as **JSON over Bluetooth**
- Real-time plotting support via compatible mobile app
- Serial debug output includes raw values and statistics

---

## 🔧 Hardware Requirements

- ESP32 Dev Board
- Floating analog input pin (e.g., GPIO34)
- Android phone with Bluetooth terminal app

---

## 📱 Mobile App

The Bluetooth stream can be visualized using:
- **Serial Bluetooth Terminal** (recommended for Android)
- Other apps that support classic Bluetooth and real-time data streaming

---

## 🔌 Getting Started

1. **Flash the Firmware**
   - Clone the repository:
     ```bash
     git clone [https://github.com/azizbns0/ESP32ADCMonitor.git](https://github.com/azizbns0/-Bluetooth-ADC-Streaming-.git)
     ```
   - Open the project in Arduino IDE or PlatformIO
   - Connect ESP32 via USB and flash the code

2. **Connect via Bluetooth**
   - Pair with device named `ESP32_ADC_Streamer`
   - Use terminal app to receive and plot the JSON packets

3. **Sample JSON Output**
   ```json
   {
     "timestamp": 123456,
     "samples": [0.031, 0.045, 0.033, 0.028, ...]
   }
   ```

---

## 🛠️ Firmware Overview

- `BluetoothSerial.h` used for Bluetooth Classic
- `analogReadResolution(12)` with `ADC_11db` for full 0–3.3V range
- JSON packets created every 10 samples
- Includes transmission statistics (min, max, avg, range)

---


watch the video:  
🔗 *[Insert link to screen recording]*

---

## 📂 Repository Structure

```
ESP32ADCMonitor/
├── ESP32ADCMonitor         # Main app file
├── Firmware                # Main firmware file
├── README.md               # Project instructions (this file)
└── demo_video.mp4          

## 📄 License

This project is under the MIT License.

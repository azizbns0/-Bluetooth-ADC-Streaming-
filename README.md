# 🔌 Bluetooth ADC Streaming System


A complete end-to-end Bluetooth ADC monitoring solution featuring real-time data streaming from ESP32 to mobile devices.

**Key Features:**
- 📟 ESP32 firmware that continuously samples ADC data and transmits over Bluetooth
- 📱 Mobile-compatible JSON data format for real-time visualization  
- 🔄 Robust noise reduction through sample averaging
- 📊 Built-in statistics and debug monitoring

🎥 **[Watch Demo Video](https://drive.google.com/drive/folders/1TVJAYx-mW3U3cvdKZBaI1Kia0P8LT4Jr?usp=drive_link)**  

---

## 📚 Table of Contents

- [Project Overview](#-project-overview)
- [Project Structure](#-project-structure)  
- [Hardware Requirements](#-hardware-requirements)
- [Firmware Features](#-firmware-features)
- [Mobile App Compatibility](#-mobile-app-compatibility)
- [Installation & Setup](#-installation--setup)
- [Usage Instructions](#-usage-instructions)
- [Technical Specifications](#-technical-specifications)
- [Data Format](#-data-format)
- [Troubleshooting](#-troubleshooting)
- [License](#-license)

---

## 🎯 Project Overview

This project implements a minimal yet robust system that:

1. **Reads** analog voltage from a floating ADC pin (GPIO34)
2. **Processes** the data with noise reduction techniques  
3. **Streams** formatted JSON packets via Bluetooth Classic
4. **Visualizes** real-time data on mobile devices or terminals


---

## 📁 Project Structure

```
-Bluetooth-ADC-Streaming-/
├── ESP32ADCMonitor/          # Main Arduino project folder
├── Firmware/                 # Core firmware implementation  
├── README.md                 # Project documentation
└── demo_video.mp4           # Demo recording
```

---

## 🔧 Hardware Requirements

| Component | Specification |
|-----------|---------------|
| **Microcontroller** | ESP32 Development Board |
| **ADC Pin** | GPIO34 (floating analog input) |
| **Power Supply** | USB or 3.3V/5V external |
| **Mobile Device** | Android with Bluetooth Classic support |

---

## 📟 Firmware Features

### Core Functionality
- **🔄 10Hz Sampling Rate**: Consistent 100ms intervals for stable data acquisition
- **📊 Noise Reduction**: 4-sample averaging per measurement point
- **📦 Batch Processing**: Groups 10 samples per JSON packet (1-second intervals)
- **🔗 Bluetooth Classic**: Uses `BluetoothSerial.h` for reliable communication
- **📈 Statistics**: Real-time min/max/avg/range calculations

### ADC Configuration
- **Resolution**: 12-bit precision (0-4095 digital range)
- **Attenuation**: `ADC_11db` for full 0-3.3V analog range
- **Reference**: Internal ESP32 voltage reference
- **Pin**: GPIO34 (ADC1_CH6) - hardware optimized

### Debug Features
- Serial monitor output with detailed statistics
- Bluetooth connection status indicators
- Sample transmission confirmations
- Error handling and recovery mechanisms

---

### Supported Features
- Real-time JSON packet reception
- Data parsing and timestamp handling
- Graphical plotting capabilities
- Connection management and auto-reconnect

---

## 🛠️ Installation & Setup

### 1. Flash the Firmware

```bash
# Clone the repository
git clone https://github.com/azizbns0/-Bluetooth-ADC-Streaming-.git
cd -Bluetooth-ADC-Streaming-
```

**Using Arduino IDE:**
1. Open `ESP32ADCMonitor/ESP32ADCMonitor.ino`
2. Install ESP32 board support package
3. Select board: "ESP32 Dev Module"
4. Set upload speed: 115200 baud
5. Upload to your ESP32

**Using PlatformIO:**
1. Import project folder
2. Build and upload via PlatformIO interface

### 2. Verify Installation

1. Open Serial Monitor (115200 baud)
2. Confirm ADC sampling initialization
3. Check Bluetooth device advertising as `ESP32_ADC_Streamer`

---

## 🚀 Usage Instructions

### Step 1: Power Up & Connect
1. Power your ESP32 via USB or external supply
2. Wait for Bluetooth initialization (check Serial Monitor)
3. ESP32 will advertise as `ESP32_ADC_Streamer`

### Step 2: Mobile Connection
1. Enable Bluetooth on your mobile device
2. Pair with `ESP32_ADC_Streamer`
3. Open your preferred Bluetooth terminal app
4. Connect to the paired ESP32 device

### Step 3: Data Monitoring
- JSON packets will stream every second
- Each packet contains 10 voltage samples
- Monitor Serial output for debug information
- Use terminal app's plotting features for visualization

---

## ⚙️ Technical Specifications

| Parameter | Value | Notes |
|-----------|-------|-------|
| **Sampling Frequency** | 10 Hz | 100ms per sample |
| **Averaging** | 4 readings/sample | Noise reduction |
| **Packet Rate** | 1 Hz | 10 samples per packet |
| **ADC Resolution** | 12-bit | 0-4095 digital range |
| **Voltage Range** | 0-3.3V | With 11dB attenuation |
| **Bluetooth Protocol** | Classic SPP | Serial Port Profile |
| **Data Format** | JSON | Human & machine readable |

---

## 📊 Data Format

### JSON Packet Structure
```json
{
  "timestamp": 123456789,
  "samples": [0.031, 0.045, 0.033, 0.028, 0.052, 0.041, 0.038, 0.029, 0.047, 0.035]
}
```

### Field Descriptions
- **`timestamp`**: System uptime in milliseconds
- **`samples`**: Array of 10 voltage readings (float, volts)
- **Range**: Each sample represents averaged voltage over 4 ADC readings

### Transmission Statistics
The firmware also outputs debug statistics via Serial:
- **Min/Max Values**: Voltage range in current packet  
- **Average**: Mean voltage across 10 samples
- **Range**: Difference between max and min values

---

## 🐛 Troubleshooting

### Common Issues

**Bluetooth Connection Fails**
- Verify ESP32 is powered and advertising
- Clear Bluetooth cache on mobile device
- Ensure no other device is connected to ESP32

**No Data Received**
- Check Serial Monitor for ADC sampling activity
- Verify JSON formatting in debug output
- Confirm mobile app supports Classic Bluetooth (not BLE)

**Erratic Readings**
- Ensure GPIO34 is properly floating or connected to signal source
- Check power supply stability (USB vs battery)
- Verify ADC reference voltage configuration

**Performance Issues**  
- Monitor Serial output for transmission delays
- Check mobile device Bluetooth buffer settings
- Reduce sampling rate if needed (modify firmware)

### Debug Commands
```cpp
// Enable verbose debugging (add to setup())
Serial.setDebugOutput(true);

// Monitor ADC raw values
Serial.println("Raw ADC: " + String(analogRead(ADC_PIN)));
```

---

## 🧰 Tech Stack Summary

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Hardware** | ESP32, GPIO34 | MCU and ADC interface |
| **Firmware** | Arduino C++, BluetoothSerial | Data acquisition & transmission |
| **Communication** | Bluetooth Classic (SPP) | Wireless data streaming |
| **Data Format** | JSON | Structured, parseable packets |
| **Mobile** | Android/iOS Terminal Apps | Data reception & visualization |
| **Development** | Arduino IDE, PlatformIO | Code development & deployment |

---


---

## 🙌 Author

**Mohamed Aziz Ben Souissi**  
*Embedded Systems Engineer*

[![LinkedIn](https://www.linkedin.com/in/mohamed-aziz-ben-souissi/)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black?style=flat&logo=github)](https://github.com/azizbns0)
[![Email](https://img.shields.io/badge/Email-Contact-red?style=flat&logo=gmail)](mohamedazizbensouissi@gmail.com)


---

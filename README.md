# 🔌 Bluetooth ADC Streaming System

[![Platform](https://img.shields.io/badge/Platform-ESP32%20%7C%20Android-blue)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)]()
[![Status](https://img.shields.io/badge/Status-Completed-brightgreen)]()
[![Language](https://img.shields.io/badge/Language-C%2B%2B%20%7C%20Java-orange)]()

A complete end-to-end Bluetooth ADC monitoring solution featuring real-time data streaming from ESP32 to Android mobile application.

**🎯 2-Hour Challenge Solution:**
- ✅ ESP32 firmware with floating ADC pin sampling (100ms intervals)
- ✅ Custom Android app with real-time plotting and data visualization
- ✅ Bluetooth Classic communication with JSON data packets
- ✅ Professional UI with live statistics and comprehensive logging

🎥 **[Watch Demo Video](https://drive.google.com/drive/folders/1TVJAYx-mW3U3cvdKZBaI1Kia0P8LT4Jr?usp=drive_link)**  


## 📚 Table of Contents

- [Project Overview](#-project-overview)
- [Project Structure](#-project-structure)  
- [Task Compliance](#-task-compliance)
- [Features](#-features)
- [Installation & Setup](#-installation--setup)
- [Usage Instructions](#-usage-instructions)
- [Technical Specifications](#-technical-specifications)
- [Screenshots](#-screenshots)
- [Demo Video](#-demo-video)
- [License](#-license)

---

## 🎯 Project Overview

This project implements the complete **2-Hour Bluetooth ADC Streaming Challenge** requirements:

### **Challenge Requirements Met:**
1. **✅ Embedded Code**: ESP32 with floating ADC pin, 100ms sampling, 1-second packet transmission
2. **✅ Mobile App**: Custom Android application with Bluetooth connectivity and live data visualization  
3. **✅ Real-time Display**: Professional UI with charts, statistics, and data tables
4. **✅ Demo Video**: Complete demonstration of functionality

### **System Architecture:**
```
ESP32 (GPIO34) → [ADC Sampling] → [JSON Packets] → [Bluetooth Classic] → Android App → [Live Plot]
     ↑                ↑                 ↑                    ↑              ↑
  100ms timer    Noise reduction   10 samples/packet   SPP Protocol   Real-time UI
```

---

## 📁 Project Structure

```
-Bluetooth-ADC-Streaming-/
├── ESP32ADCMonitor/              # Complete Android Studio Project
│   ├── app/
│   │   ├── src/main/java/com/Aziz/esp32adcmonitor/
│   │   │   └── MainActivity.java # Main Android application
│   │   ├── src/main/res/
│   │   │   ├── layout/          # UI layouts and designs
│   │   │   ├── drawable/        # Icons and visual elements
│   │   │   ├── values/          # Colors, strings, styles
│   │   │   └── mipmap/          # App icons
│   │   ├── build.gradle         # Android dependencies
│   │   └── AndroidManifest.xml  # App permissions & config
│   ├── gradle/                  # Gradle wrapper
│   ├── build.gradle             # Project build configuration
│   └── settings.gradle          # Project settings
├── Firmware/                    # ESP32 Arduino firmware
│   └── ESP32_ADC_Streamer.ino   # Main firmware file
├── demo_video.mp4              # Project demonstration
└── README.md                   # This documentation
```

---

## ✅ Task Compliance

### **1. Embedded Code Requirements**
- **✅ Bluetooth-compatible board**: ESP32 Development Board
- **✅ Floating ADC pin**: GPIO34 (nothing connected, reads electrical noise)
- **✅ 100ms sampling**: Precise timer-based 10Hz data acquisition
- **✅ 10-sample bundling**: JSON packets sent every 1 second via Bluetooth Classic
- **✅ Noise reduction**: 4-reading average per sample for stability

### **2. Mobile App Requirements**  
- **✅ Android application**: Native Java implementation using Android Studio
- **✅ Bluetooth connectivity**: Full Classic Bluetooth (SPP) implementation
- **✅ Real-time data reception**: JSON packet parsing and processing
- **✅ Live visualization**: Professional charts AND data tables with statistics

### **3. Additional Features (Beyond Requirements)**
- **📊 Professional UI**: Modern Material Design with dark theme
- **📈 Advanced Charts**: MPAndroidChart with smooth animations and zooming
- **📋 Comprehensive Statistics**: Min/Max/Average/Range/Data Rate calculations
- **🔄 Connection Management**: Robust pairing, connection, and error handling
- **📝 System Logging**: Real-time debug information and status updates
- **🎨 Visual Indicators**: Connection status, live data indicators, and animations

---

## 🚀 Features

### **ESP32 Firmware Features:**
- **🔄 Precise Timing**: Hardware timer-based 100ms sampling intervals
- **📊 Noise Reduction**: 4-sample averaging for each measurement
- **📦 Efficient Packetization**: Bundles 10 samples into JSON format
- **🔗 Bluetooth Classic**: Reliable SPP (Serial Port Profile) communication
- **📈 Built-in Statistics**: Real-time min/max/average calculations
- **🐛 Debug Output**: Comprehensive serial monitor logging

### **Android App Features:**
- **📱 Modern UI**: Material Design 3 with professional dark theme
- **🔄 Bluetooth Management**: Device scanning, pairing, and connection handling
- **📊 Real-time Plotting**: Smooth line charts with 500+ data points
- **📈 Live Statistics**: Packet count, voltage range, averages, and data rates
- **🎨 Visual Feedback**: Connection indicators, animations, and status updates
- **📝 System Logging**: Timestamped debug information with log levels
- **⚡ Performance Optimized**: Efficient data processing and UI updates
- **🔧 Error Handling**: Graceful disconnection and reconnection management

---

## 🛠️ Installation & Setup

### **Prerequisites:**
- ESP32 Development Board
- Android device with Bluetooth Classic support
- Arduino IDE or PlatformIO (for firmware)
- Android Studio (for mobile app development)

### **1. Flash ESP32 Firmware**

```bash
# Clone the repository
git clone https://github.com/azizbns0/-Bluetooth-ADC-Streaming-.git
cd -Bluetooth-ADC-Streaming-
```

**Using Arduino IDE:**
1. Open `Firmware/ESP32_ADC_Streamer.ino`
2. Install ESP32 board support package
3. Select board: "ESP32 Dev Module"
4. Configure: Upload Speed 115200, Flash Size 4MB
5. Upload to your ESP32 device

**Using PlatformIO:**
1. Import the firmware folder as a new project
2. Build and upload via PlatformIO interface

### **2. Install Android Application**

**Option A: Build from Source (Recommended)**
1. Open `ESP32ADCMonitor/` folder in Android Studio
2. Sync Gradle dependencies (automatically prompts)
3. Build and run on connected Android device
4. Grant Bluetooth permissions when prompted

**Option B: Direct APK Installation**
1. Build APK using Android Studio: `Build → Build Bundle(s)/APK(s) → Build APK(s)`
2. Install generated APK on Android device
3. Enable "Install from Unknown Sources" if needed

### **3. Required Dependencies**
The Android app uses these key libraries:
- **MPAndroidChart**: Professional charting library
- **AndroidX**: Modern Android UI components  
- **Material Design**: Google's design system
- **Bluetooth Classic**: Android Bluetooth APIs

---

## 🚀 Usage Instructions

### **Step 1: Prepare Hardware**
1. Connect ESP32 to power (USB or external 5V)
2. Ensure GPIO34 pin is **completely unconnected** (floating)
3. Open Serial Monitor (115200 baud) to verify operation
4. Confirm ESP32 advertises as `ESP32_ADC_Streamer`

### **Step 2: Android App Setup**
1. Install and launch the ESP32 ADC Monitor app
2. Grant Bluetooth permissions when prompted
3. Ensure Bluetooth is enabled on your Android device
4. Pair with `ESP32_ADC_Streamer` in Android Bluetooth settings

### **Step 3: Connect and Monitor**
1. Launch the app and tap **"Connect"** button
2. Wait for successful connection (green indicator)
3. Observe real-time data streaming in the chart
4. Monitor statistics: packet count, voltage values, data rates
5. Use **"Clear Plot"** to reset the visualization

### **Step 4: Understanding the Data**
- **Floating ADC readings**: Random electrical noise (typically 0-3.3V)
- **Sample rate**: 10 samples per second (100ms intervals)
- **Packet rate**: 1 packet per second (containing 10 samples)
- **Data format**: JSON with timestamp and voltage array

---

## ⚙️ Technical Specifications

| **Parameter** | **Value** | **Notes** |
|---------------|-----------|-----------|
| **ESP32 Sampling** | 10 Hz | 100ms intervals with hardware timer |
| **ADC Resolution** | 12-bit | 0-4095 digital range |
| **Voltage Range** | 0-3.3V | 11dB attenuation for full range |
| **Averaging** | 4 readings/sample | Noise reduction technique |
| **Packet Rate** | 1 Hz | 10 samples bundled per transmission |
| **Communication** | Bluetooth Classic | SPP (Serial Port Profile) |
| **Data Format** | JSON | `{"timestamp": ms, "samples": [v1,v2...]}` |
| **Android UI** | Material Design 3 | Modern dark theme with animations |
| **Chart Library** | MPAndroidChart | Professional plotting with 500+ points |
| **Performance** | 50ms UI updates | Smooth real-time visualization |

---

## 📊 Data Format & Protocol

### **JSON Packet Structure:**
```json
{
  "timestamp": 123456789,
  "samples": [0.031, 0.045, 0.033, 0.028, 0.052, 0.041, 0.038, 0.029, 0.047, 0.035]
}
```

### **Field Descriptions:**
- **`timestamp`**: ESP32 system uptime in milliseconds
- **`samples`**: Array of 10 voltage readings (float values in volts)
- **Transmission**: One packet every 1000ms via Bluetooth Classic SPP

### **Statistical Processing:**
The Android app calculates:
- **Real-time Min/Max**: Voltage range across all received data
- **Running Average**: Cumulative mean of all samples
- **Packet Statistics**: Per-packet range and characteristics  
- **Data Rate**: Actual samples received per second
- **Connection Metrics**: Packet count and transmission timing

---

## 📱 Screenshots

### **Main Application Interface:**
- **Connection Status**: Visual indicator with ESP32 pairing status
- **Real-time Chart**: Smooth line plot with voltage vs. time
- **Statistics Dashboard**: Live min/max/average/range values
- **System Log**: Timestamped debug information with color coding
- **Control Buttons**: Connect/Disconnect and Clear Plot functionality

### **Professional Features:**
- **Dark Theme**: Futuristic UI optimized for embedded monitoring
- **Responsive Design**: Adapts to different Android screen sizes
- **Smooth Animations**: Connection indicators and value transitions
- **Error Handling**: Graceful bluetooth disconnection and reconnection

---

## 🎥 Demo Video

**📹 Full Demonstration Available**: `demo_video.mp4`

**Video Content (< 2 minutes):**
1. **App Launch**: Starting the ESP32 ADC Monitor application
2. **Bluetooth Connection**: Pairing and connecting to ESP32_ADC_Streamer
3. **Real-time Data**: Live voltage plotting with floating ADC readings
4. **Statistics Display**: Min/max/average calculations updating in real-time
5. **System Logging**: Debug information showing packet reception
6. **Feature Demo**: Connection management and plot clearing functionality

**What the video demonstrates:**
- ✅ Successful Bluetooth pairing and connection
- ✅ Real-time data streaming at 10Hz with 1-second packet intervals
- ✅ Professional UI with live chart updates and statistics
- ✅ Robust error handling and connection management
- ✅ Complete end-to-end system functionality

---

## 🐛 Troubleshooting

### **Common Issues & Solutions:**

**🔴 ESP32 Not Found in Bluetooth Devices**
- Verify ESP32 is powered and running firmware
- Check Serial Monitor for "Bluetooth initialized" message
- Clear Android Bluetooth cache: Settings → Apps → Bluetooth → Storage → Clear Cache
- Restart both ESP32 and Android device

**🔴 Connection Fails or Drops**
- Ensure ESP32 and Android device are within 10 meters
- Check for interference from other 2.4GHz devices
- Verify ESP32 is not connected to another device
- Monitor Serial output for connection error messages

**🔴 No Data Received in App**  
- Confirm ESP32 Serial Monitor shows JSON packet transmission
- Check Android app log for "processPacket" messages
- Verify JSON format is valid (timestamps and 10-sample arrays)
- Restart app and attempt reconnection

**🔴 App Crashes or Performance Issues**
- Grant all required Bluetooth permissions
- Ensure Android device supports Bluetooth Classic (not just BLE)
- Close other apps that might use Bluetooth
- Clear app data and cache if needed

### **Debug Features:**
- **ESP32 Serial Monitor**: Real-time firmware status and packet transmission
- **Android System Log**: Timestamped debug information with log levels
- **Connection Indicators**: Visual feedback for pairing and data flow status
- **Statistics Monitoring**: Data rate and packet count for performance verification

---

## 🧰 Technology Stack

| **Layer** | **Technology** | **Purpose** | **Version** |
|-----------|----------------|-------------|-------------|
| **Hardware** | ESP32 DevKit, GPIO34 ADC | Microcontroller & analog input | - |
| **Firmware** | Arduino C++, BluetoothSerial.h | ESP32 programming & communication | Arduino IDE 2.x |
| **Communication** | Bluetooth Classic SPP | Wireless data transmission | IEEE 802.15.1 |
| **Mobile Platform** | Android (API 21+) | Mobile operating system | Android 5.0+ |
| **App Development** | Java, Android Studio | Native Android development | Studio 2023.x |
| **UI Framework** | Material Design 3, AndroidX | Modern user interface | Latest |
| **Charting** | MPAndroidChart | Professional data visualization | v3.1.x |
| **Data Format** | JSON | Structured data packets | RFC 7159 |
| **Build Tools** | Gradle, Android Gradle Plugin | Project build & dependency management | 8.x |

---

## 🎯 Project Highlights

### **Professional Implementation:**
- **⚡ Performance**: Optimized for real-time data processing
- **🎨 User Experience**: Intuitive interface with visual feedback  
- **🔧 Reliability**: Robust error handling and connection management
- **📱 Modern Design**: Material Design 3 with professional aesthetics
- **📊 Advanced Analytics**: Comprehensive statistics and data visualization

### **Technical Excellence:**
- **🔄 Real-time Processing**: Sub-50ms UI update latency
- **📈 Scalable Architecture**: Efficient memory management for continuous operation
- **🛡️ Error Recovery**: Graceful handling of connection failures
- **📝 Comprehensive Logging**: Multiple log levels with timestamps
- **⚙️ Configurable Parameters**: Easy modification of sampling rates and UI settings

### **Beyond Requirements:**
- **📊 Enhanced Statistics**: More metrics than required (data rate, range, etc.)
- **🎯 Professional Polish**: Production-ready code quality and documentation
- **🔄 Advanced Features**: Smooth animations, zoom/pan charts, auto-reconnect
- **📱 Platform Optimization**: Efficient Android resource utilization

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

**Development Setup:**
1. Fork the repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Make changes and test thoroughly
4. Commit changes: `git commit -m 'Add AmazingFeature'`
5. Push to branch: `git push origin feature/AmazingFeature`
6. Open a Pull Request with detailed description

**Code Standards:**
- Follow existing code style and formatting
- Add comments for complex logic
- Test on multiple Android devices
- Verify ESP32 compatibility

---

## 🙌 Author

**Mohamed Aziz Ben Souissi**  
*Embedded Systems Engineer*

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat&logo=linkedin)](https://www.linkedin.com/in/mohamed-aziz-ben-souissi/)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black?style=flat&logo=github)](https://github.com/azizbns0)
[![Email](https://img.shields.io/badge/Email-Contact-red?style=flat&logo=gmail)](mohamedazizbensouissi@gmail.com)

---

## 🌟 Acknowledgments

- **Espressif Systems** for the ESP32 platform and comprehensive documentation
- **Android Open Source Project** for the mobile development framework
- **PhilJay/MPAndroidChart** for the professional charting library
- **Arduino Community** for the extensive embedded development ecosystem
- **Material Design Team** for the modern UI/UX guidelines

---


**📞 Ready for Technical Discussion**: This implementation showcases embedded systems programming, mobile app development, wireless communication protocols, real-time data processing, and professional software engineering practices.

#include "BluetoothSerial.h"

BluetoothSerial SerialBT;


const int ADC_PIN = 34; 
const int SAMPLE_RATE_MS = 100;  
const int SAMPLES_PER_PACKET = 10; 

// Data storage
float adcSamples[SAMPLES_PER_PACKET];
int sampleIndex = 0;
unsigned long lastSampleTime = 0;
unsigned long lastTransmitTime = 0;

void setup() {
  Serial.begin(115200);
  delay(1000); 
  
  // Initialize Bluetooth
  SerialBT.begin("ESP32_ADC_Streamer"); 
  Serial.println("The device started, now you can pair it with bluetooth!");
  Serial.println("Device name: ESP32_ADC_Streamer");
  
  
  analogReadResolution(12); // 12-bit resolution (0-4095)
  analogSetAttenuation(ADC_11db); 
  
  
  Serial.printf("Testing ADC on GPIO%d...\n", ADC_PIN);
  for(int i = 0; i < 5; i++) {
    int testRead = analogRead(ADC_PIN);
    float testVoltage = (testRead * 3.3) / 4095.0;
    Serial.printf("Test read %d: Raw=%d, Voltage=%.3fV\n", i+1, testRead, testVoltage);
    delay(100);
  }
  
  Serial.println("ADC sampling started - 10 Hz rate");
  Serial.println("Floating pin will show random noise values");
  Serial.println("Connect via Bluetooth to see data transmission");
  
  lastSampleTime = millis();
  lastTransmitTime = millis();
}

void loop() {
  unsigned long currentTime = millis();
  
  
  if (currentTime - lastSampleTime >= SAMPLE_RATE_MS) {
    lastSampleTime = currentTime;
    
   
    long rawSum = 0;
    for(int i = 0; i < 4; i++) {
      rawSum += analogRead(ADC_PIN);
      delayMicroseconds(100);
    }
    int rawValue = rawSum / 4;
    
    float voltage = (rawValue * 3.3) / 4095.0;
    
    // Store sample
    adcSamples[sampleIndex] = voltage;
    sampleIndex++;
    
    // Debug output
    Serial.printf("Sample %d: Raw=%d (0x%03X), Voltage=%.3fV\n", 
                  sampleIndex, rawValue, rawValue, voltage);
    
    if (sampleIndex >= SAMPLES_PER_PACKET) {
      transmitData();
      sampleIndex = 0; 
      lastTransmitTime = currentTime;
    }
  }
  
  
  if (SerialBT.hasClient()) {
    
    if (currentTime - lastTransmitTime > 5000) { 
      Serial.println("Bluetooth client connected, continuing sampling...");
    }
  } else {
    
    static unsigned long lastConnectionMessage = 0;
    if (currentTime - lastConnectionMessage > 3000) {
      Serial.println("Waiting for Bluetooth connection...");
      Serial.println("Look for 'ESP32_ADC_Streamer' in your phone's Bluetooth settings");
      lastConnectionMessage = currentTime;
    }
  }
  
  
  delay(10);
}

void transmitData() {
  if (!SerialBT.hasClient()) {
    Serial.println("No Bluetooth client connected - skipping transmission");
    return;
  }
  
  String jsonPacket = "{";
  jsonPacket += "\"timestamp\":" + String(millis()) + ",";
  jsonPacket += "\"samples\":[";
  
  for (int i = 0; i < SAMPLES_PER_PACKET; i++) {
    jsonPacket += String(adcSamples[i], 3); 
    if (i < SAMPLES_PER_PACKET - 1) {
      jsonPacket += ",";
    }
  }
  
  jsonPacket += "]}";
  
  
  jsonPacket += "\n";

  SerialBT.print(jsonPacket);
  
  Serial.println("✓ Transmitted packet: " + jsonPacket);
  
  
  float min_val = adcSamples[0];
  float max_val = adcSamples[0];
  float avg_val = 0;
  
  for (int i = 0; i < SAMPLES_PER_PACKET; i++) {
    if (adcSamples[i] < min_val) min_val = adcSamples[i];
    if (adcSamples[i] > max_val) max_val = adcSamples[i];
    avg_val += adcSamples[i];
  }
  avg_val /= SAMPLES_PER_PACKET;
  
  Serial.printf("Stats - Min: %.3fV, Max: %.3fV, Avg: %.3fV, Range: %.3fV\n", 
                min_val, max_val, avg_val, (max_val - min_val));
  Serial.println("----------------------------------------");
}
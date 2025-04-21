#include<ESP8266WiFi.h>
#include<PubSubClient.h>
#include<DHT.h>
#include <WifiClientSecure.h>
//thay bang ten, mat khau wifi cua minh
const char* ssid = "AndroidAP";
const char* password = "12345678";


//const char* mqttServer = "7249966839ac4bf68fc9bb228451bd0b.s1.eu.hivemq.cloud";
//const int mqttPort = 8883;                    
//const char* mqttClientID = "ESP8266_DHT11";   
//const char* mqttUser = "quang";       
//const char* mqttPassword = "Quangkk123";  

const char* mqttServer = "7882f49ec5a24abc9c49b6c8332f73e4.s1.eu.hivemq.cloud";
const int mqttPort = 8883;                    
const char* mqttClientID = "ESP8266_DHT11";   
const char* mqttUser = "hayson";       
const char* mqttPassword = "Alo123,./";  

// Khai báo cảm biến DHT11
#define LED D7
#define PUMP D2
#define DHTPIN D4      
#define DHTTYPE DHT11  
DHT dht(DHTPIN, DHTTYPE);

// Khởi tạo WiFi và MQTT Client
WiFiClientSecure espClient;
PubSubClient client(espClient);

unsigned long msg = 0;
#define MSG_BUFFER_SIZE (50)
char message[MSG_BUFFER_SIZE];

// Hàm kết nối WiFi
void setup_wifi() {
  Serial.print("Đang kết nối WiFi...");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi đã kết nối!");
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  
  String message = "";
  for (int i = 0; i < length; i++) {
    message += (char)payload[i];
  }
  Serial.println(message);

  if (String(topic) == "led/control") {
    if (message == "ON") {
      digitalWrite(LED, HIGH);
      Serial.println("LED ON");
    } else if (message == "OFF") {
      digitalWrite(LED, LOW);
      Serial.println("LED OFF");
    }
  }

  if (String(topic) == "pump/control") {
    if (message == "ON") {
      digitalWrite(PUMP, LOW);
      Serial.println("Bật máy bơm");
    } else if (message == "OFF") {
      digitalWrite(PUMP, HIGH);
      Serial.println("Tắt máy bơm");
    }
  }


}

void reconnect_mqtt() {
  while (!client.connected()) {
    Serial.print("Đang kết nối MQTT...");

    if (client.connect(mqttClientID, mqttUser, mqttPassword)) {
      Serial.println("Đã kết nối MQTT có xác thực!");
      client.subscribe("led/control"); 
      client.subscribe("pump/control");
    } else {
      Serial.print("Thất bại, mã lỗi = ");
      Serial.print(client.state());
      Serial.println(" Thử lại sau 5 giây...");
      delay(5000);
    }
  }
}

void setup() {
  pinMode(LED, OUTPUT);
  pinMode(PUMP, OUTPUT);
  digitalWrite(LED, HIGH);
  digitalWrite (PUMP, HIGH); //tat may bom
  Serial.begin(115200);
  setup_wifi();
  espClient.setInsecure();
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  dht.begin();
}

unsigned long lastPublishTime = 0;

void loop() {
  if (!client.connected()) {
    reconnect_mqtt();
  }
  client.loop(); 

  if (millis() - lastPublishTime >= 5000) {
   lastPublishTime = millis();  

    float temperature = dht.readTemperature(); 
    float humidity = dht.readHumidity();       

    if (!isnan(temperature) && !isnan(humidity)) {
      Serial.print("Nhiệt độ: ");
      Serial.print(temperature);
      Serial.print("°C | Độ ẩm: ");
      Serial.print(humidity);
      Serial.println("%");

      String payload = "{\"temperature\": " + String(temperature) + ", \"humidity\": " + String(humidity) + "}";
      client.publish("sensor/data", payload.c_str());
    }

  }
  
}

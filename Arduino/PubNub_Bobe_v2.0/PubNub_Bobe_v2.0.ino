#include <ESP8266WiFi.h>
#define PubNub_BASE_CLIENT WiFiClient
#include <PubNub.h>
static char ssid[] = "CloudX";
static char pass[] = "Oojed1234";
const static char pubkey[]  = "pub-c-f623deb2-63eb-4608-acf4-af48ed3e31cc";
const static char subkey[]  = "sub-c-5251cb74-f12b-11e9-ad72-8e6732c0d56b";
const static char channel[] = "whiteboard";


char result[20] = "\"";
char final1[20] = "\"";

char result1[20] = "";
float lat = -34.592745;

char result2[20] = "";
float lon = -58.430868;




String message;
void setup() {

 

  
    Serial.begin(115200);
    Serial.println("Attempting to connect...");
    WiFi.begin(ssid, pass);
    if(WiFi.waitForConnectResult() != WL_CONNECTED) { // Connect to WiFi.
        Serial.println("Couldn't connect to WiFi.");
        while(1) delay(100);
    }
    else {
        Serial.print("Connected to SSID: ");
        Serial.println(ssid);
        PubNub.begin(pubkey, subkey); // Start PubNub.
        Serial.println("PubNub is set up.");
    }

    

   sprintf(result1, "%f", lat);
  sprintf(result2, "%f", lon);

  strcat(result, result1);
  strcat(result," , ");
  strcat(result, result2);
  strcat(result, final1);

  

    
}
void loop() {

  
  
    publish();
    delay(60000);
}


void publish(){

  { // Subscribe.
        PubSubClient* sclient = PubNub.subscribe(channel); // Subscribe.
        
        if (0 == sclient) { 
            Serial.println("Error subscribing to channel.");
            delay(1000);
            return;
        }
        while (sclient->wait_for_data()) { // Print messages.
            Serial.write(sclient->read());
        }
        sclient->stop();
    }
    { // Publish.
        
        WiFiClient* client = PubNub.publish(channel, result); // Publish message.
        
        if (0 == client) {
            Serial.println("Error publishing message.");
            delay(1000);
            return;
        }
        client->stop();
    }
  
}

/** Adafruit NeoPixel Stuff **/
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

#define PIN 6
//#define NUM_LEDS 288
#define NUM_LEDS 249

// Parameter 1 = number of pixels in strip
// Parameter 2 = Arduino pin n
// Parameter 3 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)
//Adafruit_NeoPixel strip = Adafruit_NeoPixel(60, PIN, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUM_LEDS, PIN, NEO_GRBW + NEO_KHZ800);

uint32_t parts[MAX_PARTS];
uint32_t defaultColor = strip.Color(0,0,0,0);
int defaultBrightness = 100;

/** Ardulink Sutff **/
String inputString = "";         // a string to hold incoming data (this is general code you can reuse)
boolean stringComplete = false;  // whether the string is complete (this is general code you can reuse)

void setup() {
  strip.setBrightness(defaultBrightness);
  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
  /** Ardulink Sutff **/
  Serial.begin(115200);

  Serial.print("alp://rply/");
  Serial.print("ok?id=0");
  Serial.print('\n'); // End of Message
  Serial.flush();
}

void loop() {

  /** Ardulink Sutff **/
  if (stringComplete) {
    log("---");
    //serial message inputString was completed, now check if it was a valid massage
    boolean msgValid = false;
    if(inputString.startsWith("alp://")) { // OK is a message I know (this is general code you can reuse)
      inputString.remove(0,6); // remove "alp://"
    }
    if(inputString.startsWith("cust/")) { //for all custom events
      inputString.remove(0,5); // remove "cust/"
    }

    //extract the 4 char command
    String command = inputString.substring(0,4);
    //extract the parameters (remove the slash between command and parameters)
    String parameters = inputString.substring(5,inputString.length()-1);

    if(command.equals("time")) { //Command example: alp://cust/time/500/100/0A0B0C0D/?id=123
      msgValid = true;
      //Duration
      int nextDelimeter = parameters.indexOf('/');
      int duration = parameters.substring(0, nextDelimeter).toInt(); //in example: 127

      //Brigthness
      int lastDelimeter = nextDelimeter + 1;
      nextDelimeter = parameters.indexOf('/', lastDelimeter);
      int brightness = parameters.substring(lastDelimeter, nextDelimeter).toInt();
      log("Brightness params: "+String(brightness));
      //Color
      lastDelimeter = nextDelimeter + 1;
      nextDelimeter = parameters.indexOf('/', lastDelimeter);
      String colorParams = parameters.substring(lastDelimeter, nextDelimeter);
      log("Color params: "+colorParams);
      uint32_t color = convertToColor(colorParams);

      log("Blinkeffect triggered");

      fullColor(color);
      ledBrightness(brightness);
      ledShow();
      delay(duration);
      resetBrightness();
      resetColor();
      ledShow();

    } else if(command.equals("dimm")) { //Command example: alp://cust/dimm/50/?id=123
      msgValid = true;
      //brightness
      int nextDelimeter = parameters.indexOf('/');
      int brightness = parameters.substring(0, nextDelimeter).toInt();
      log("set brightness to "+String(brightness));
      defaultBrightness = brightness;
      resetBrightness();
      ledShow();
    } else if (command.startsWith("colr")) { //Command example: alp://cust/colr/0A0BFF0D/?id=123
      msgValid = true;
      int nextDelimeter = parameters.indexOf('/');
      String colorParams = parameters.substring(0, nextDelimeter);
      log("InputString:"+inputString);
      log("Color changed"+colorParams);

      defaultColor = convertToColor(colorParams);
      resetColor();
      ledShow();
    } else if(command.startsWith("part")) { //Command example: alp://cust/part/1,3/A0B0C0D0/?id=123
      msgValid = true;

      //Get the part
      int nextDelimeter = parameters.indexOf('/');
      String partParams = parameters.substring(0,nextDelimeter);
      //extract the part
      int partParamEndOfString = partParams.length();
      int partParamStartPos = 0;
      int partParamEndPos = partParams.indexOf(',');
      int partStart = partParams.substring(partParamStartPos, partParamEndPos).toInt();
      partParamStartPos = partParamEndPos + 1;
      int partEnd = partParams.substring(partParamStartPos, partParamEndOfString).toInt();

      //Get the color
      int lastDelimeter = nextDelimeter + 1;
      nextDelimeter = parameters.indexOf('/', lastDelimeter);  //move paramEndPos to next "/"
      String colorParams = parameters.substring(lastDelimeter, nextDelimeter);
      uint32_t requestedColor = convertToColor(colorParams);


      //log("Part from "+String(partStart)+" to "+String(partEnd)+" with color: "+colorParams);

      partColor(requestedColor,partStart,partEnd);
      ledShow();
    } else {
      msgValid = false; // this sketch doesn't know other messages in this case command is ko (not ok)
    }

    // Prepare reply message if caller supply a message id (this is general code you can reuse)
    int idPosition = inputString.indexOf("?id=");
    if(idPosition != -1) {
      String id = inputString.substring(idPosition + 4);
      // print the reply
      Serial.print("alp://rply/");
      if(msgValid) { // this sketch doesn't know other messages in this case command is ko (not ok)
        Serial.print("ok?id=");
      } else {
        Serial.print("ko?id=");
      }
      Serial.print(id);
      Serial.print('\n'); // End of Message
      Serial.flush();
    }

    // clear the inputString:
    inputString = "";
    stringComplete = false;
  } else {
    //serial message inputString was NOT completed
    /** Adafruit NeoPixel Stuff **/
  }
}

void log(String msg) {
  //Send over alp protocol to cevnt (custom event)
  Serial.print("alp://cevnt/");
  //Add "log/" path (on java side, the custom event reciver will print it as "arduino log"
  Serial.print("log/");
  //Add the log message
  Serial.print(msg);
  Serial.print('\n'); //End of msg
  Serial.flush(); //Send
}

/**
 *  converts a string to strip.Color
 *  rgbw example: in: "0A0B0C0D" -> out: strip.Color(10,11,12,13)
 *  rgb example: in: "FF000D" -> out: strip.Color(255,0,13)
**/
uint32_t convertToColor(String params) {

  int endOfString = params.length();
  long colorHex = (long) strtol(&params[0],NULL,16);
  if(params.length() == 6) {
    int red = colorHex >> 16;
    int green = (colorHex & 0x00ff00) >> 8;
    int blue = (colorHex & 0x0000ff);
    log("converted color: "+String(red)+" "+String(green)+" "+String(blue));
    return strip.Color(red,green,blue);
  } else if(params.length() == 8) {
    //TODO a niceer way to parse the parameters. string parsing is expensive!
    int red = strtol(&params.substring(0,2)[0],NULL,16);
    int green = strtol(&params.substring(2,4)[0],NULL,16);
    int blue = strtol(&params.substring(4,6)[0],NULL,16);
    int white = strtol(&params.substring(6,8)[0],NULL,16);
    log("converted color: "+String(red)+" "+String(green)+" "+String(blue)+" "+String(white));
    return strip.Color(red,green,blue,white);
  } else {
    log("Wrong color parameters: "+params);
  }
}

void resetBrightness() {
  ledBrightness(defaultBrightness);
}

void resetColor() {
  fullColor(defaultColor);
}

void fullColor(uint32_t c) {
  for(uint16_t i=0; i<strip.numPixels(); i++) {
    strip.setPixelColor(i, c);
  }
}

void partColor(uint32_t c, int partStart, int partEnd) {
  log("p "+String(partStart)+" "+String(partEnd));
  for(int i = partStart; i <= partEnd; i++) {
    strip.setPixelColor(i, c);
  }
}

void ledShow() {
  strip.show();
}

void ledBrightness(int brightness) {
  strip.setBrightness(brightness);
}

/** Ardulink Sutff **/
/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 This is general code you can reuse.
 */
void serialEvent() {

  while (Serial.available() && !stringComplete) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}

/** Adafruit NeoPixel Stuff **/
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

#define PIN 6
//#define NUM_LEDS 288
#define NUM_LEDS 249
#define BRIGHTNESS 255
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

uint32_t idleColor = strip.Color(0,0,0,0);

/** Ardulink Sutff **/
String inputString = "";         // a string to hold incoming data (this is general code you can reuse)
boolean stringComplete = false;  // whether the string is complete (this is general code you can reuse)

void setup() {
  /** Adafruit NeoPixel Stuff **/
  // This is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
#if defined (__AVR_ATtiny85__)
  if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
#endif
  // End of trinket special code

  strip.setBrightness(BRIGHTNESS);
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
    if(inputString.startsWith("alp://")) { // OK is a message I know (this is general code you can reuse)
      boolean msgRecognized = true;
      if(inputString.substring(6,10) == "cust") //for all custom events
      {
        if(inputString.substring(11,15) == "rgbw") { //Command example: alp://cust/rgbw/500/127,128,129,130/?id=123
          int paramStartPos = 16;
          //Duration
          int paramEndPos = inputString.indexOf('/', paramStartPos); //move paramEndPos to next "/"
          int duration = inputString.substring(paramStartPos, paramEndPos).toInt(); //in example: 127

          paramStartPos = paramEndPos+1; //move paramStartPos to right
          paramEndPos = inputString.indexOf('/', paramStartPos);

          String colorParams = inputString.substring(paramStartPos,paramEndPos);
          uint32_t color = convertToColor(colorParams);

          log("Blinkeffect triggered");

          //Trigger function:
          fullColor(color);
          delay(duration);
          resetColor();

        } else if (inputString.substring(11,15) == "colr") { //Command example: alp://cust/colr/127,128,129,130/?id=123
          int paramStartPos = 16;
          int paramEndPos = inputString.indexOf('/', paramStartPos); //move paramEndPos to next "/"
          String colorParams = inputString.substring(paramStartPos,paramEndPos);

          log("Color changed");

          idleColor = convertToColor(colorParams);
          resetColor();
        } else if(inputString.substring(11,15) == "part") { //Command example: alp://cust/part/127,128,129,130/0,50/?id=123
          int paramStartPos = 16;

          //Get the color
          int paramEndPos = inputString.indexOf('/', paramStartPos); //move paramEndPos to next "/"
          String colorParams = inputString.substring(paramStartPos,paramEndPos);
          uint32_t requestedColor = convertToColor(colorParams);

          //Get the part
          paramStartPos = paramEndPos + 1; //move paramStartPos to right
          paramEndPos = inputString.indexOf('/', paramStartPos);  //move paramEndPos to next "/"
          String partParams = inputString.substring(paramStartPos,paramEndPos);
          //extract the part
          int partParamEndOfString = partParams.length();
          int partParamStartPos = 0;
          int partParamEndPos = partParams.indexOf(',');
          int partStart = partParams.substring(partParamStartPos, partParamEndPos).toInt();
          partParamStartPos = partParamEndPos + 1;
          int partEnd = partParams.substring(partParamStartPos, partParamEndOfString).toInt();


          //log("Part from "+String(partStart)+" to "+String(partEnd));

          partColor(requestedColor,partStart,partEnd);
        } else if(inputString.substring(11,15) == "full") { //Command example: alp://cust/full/ffbc34ffbc34ffbc34ffbc34...ffbc34ffbc34ffbc34ffbc34/?id=123
          int paramStartPos = 16;

          //Get the all colors for 100 pixels
          uint32_t paramEndPos = inputString.indexOf('/', paramStartPos); //move paramEndPos to next "/"
          String fullColorParams = inputString.substring(paramStartPos,paramEndPos);
          log("Color Params: "+String(paramStartPos)+" to "+String(paramEndPos)+" = "+fullColorParams);
          fullLED(fullColorParams);
        } else {
          msgRecognized = false; // this sketch doesn't know other messages in this case command is ko (not ok)
        }
      } else {
         msgRecognized = false; // this sketch doesn't know other messages in this case command is ko (not ok)
      }


      // Prepare reply message if caller supply a message id (this is general code you can reuse)
      int idPosition = inputString.indexOf("?id=");
      if(idPosition != -1) {
        String id = inputString.substring(idPosition + 4);
        // print the reply
        Serial.print("alp://rply/");
        if(msgRecognized) { // this sketch doesn't know other messages in this case command is ko (not ok)
          Serial.print("ok?id=");
        } else {
          Serial.print("ko?id=");
        }
        Serial.print(id);
        Serial.print('\n'); // End of Message
        Serial.flush();
      }
    }

    // clear the string:
    inputString = "";
    stringComplete = false;
  } else {

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
 *  rgbw example: in: "255,100,0,30" -> out: strip.Color(255,100,0,30)
 *  rgb example: in: "255,0,30" -> out: strip.Color(255,0,30)
**/
uint32_t convertToColor(String params) {

  int endOfString = params.length();

  int paramStartPos = 0;
  //Red
  int paramEndPos = params.indexOf(',', paramStartPos); //move paramEndPos to next ","
  int r = params.substring(paramStartPos, paramEndPos).toInt(); //in example: 127

  //Green
  paramStartPos = paramEndPos+1; //move paramStartPos to right
  paramEndPos = params.indexOf(',', paramStartPos); //move paramEndPos to next ","
  int g = params.substring(paramStartPos,paramEndPos).toInt(); //in example: 128

  //Blue
  paramStartPos = paramEndPos+1; //move paramStartPos to right
  paramEndPos = params.indexOf(',', paramStartPos); //move paramEndPos to next ","
  if(paramEndPos == -1) {
    //We have only one parameter (blue) left
     paramEndPos = endOfString; //paramEndPos is the end of string
     //if there is no "," after the blue color, we will read to the paramString end (paramSize): we only have an rgb color
     int b = params.substring(paramStartPos,paramEndPos).toInt(); //in example: 129

     return strip.Color(r,g,b);
  } else {
    //we have more parameters then blue: its an rgbw string
    int b = params.substring(paramStartPos,paramEndPos).toInt(); //in example: 129
    paramStartPos = paramEndPos+1; //move paramStartPos to right

    //White (optional)
    paramEndPos = endOfString; //paramEndPos is the end of string
    int w = params.substring(paramStartPos,paramEndPos).toInt(); //in example: 130


    return strip.Color(r,g,b,w);
  }
}

/** Adafruit NeoPixel Stuff **/
void fullColorDuration(uint32_t c, uint8_t duration) {
  fullColor(c);
  delay(duration);
  resetColor();
}

void resetColor() {
  fullColor(idleColor);
}

void fullColor(uint32_t c) {
  for(uint16_t i=0; i<strip.numPixels(); i++) {
    strip.setPixelColor(i, c);
  }
  strip.show();
}

void partColor(uint32_t c, int partStart, int partEnd) {
  //calculate absoult parts
  int partStartAbs, partEndAbs;
  partStartAbs = (strip.numPixels() * ((double)  partStart / 100));
  partEndAbs = (strip.numPixels() * ((double)  partEnd / 100));
  for(uint16_t i=partStartAbs; i<partEndAbs; i++) {
    strip.setPixelColor(i, c);
  }
  strip.show();
 //log("Absoulute Parts are "+String(partStartAbs)+" to "+String(partEndAbs));
}

void fullLED(String colors) {
  int relativeSize = 99;
  uint32_t relativeColors[relativeSize];
  for(uint8_t pos = 0, pixelPos = 0; pos < colors.length() && pixelPos < relativeSize; pos += 6, pixelPos++) {
    String hexColor = colors.substring(pos, pos + 6);
    log("Pos: "+String(pos)+" to "+String(pos+6));
    log(String(pixelPos)+" "+hexColor);
  }
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

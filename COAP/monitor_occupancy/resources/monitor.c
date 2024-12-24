#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"
#include <stdio.h>
#include "dev/leds.h"


//API FUNCTION DEFINITIONS
static void monitor_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);


//RESOURCE DEFINITION
RESOURCE(monitor,
         "title=\"Monitor\";rt=\"Control\"",
         NULL,
         NULL,
         monitor_put_handler,
         NULL);


//LED BLINKING
static struct ctimer b_leds;
bool blink_condition;
void blink_green_callback(){
    if(blink_condition){ 
	leds_toggle(LEDS_GREEN);
	ctimer_set(&b_leds, 0.5*CLOCK_SECOND,blink_green_callback,NULL);
    }
}
void blink_red_callback(){
    if(blink_condition){
	leds_toggle(LEDS_RED);
	ctimer_set(&b_leds, 0.5*CLOCK_SECOND,blink_red_callback,NULL);
    }
}


uint8_t occupancy = 0;

static void monitor_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
    size_t len = 0;
    const uint8_t *payload = NULL;
  

    len = coap_get_payload(request, &payload);

    if(len > 0)
        occupancy = atoi((const char*)payload);

    if(occupancy == 0){
	blink_condition = false;
        leds_off(LEDS_ALL);
    }
    if(occupancy == 1){
	blink_condition = false;
	leds_off(LEDS_ALL);
        leds_set(LEDS_GREEN);
    }
    if(occupancy == 2){
	leds_off(LEDS_ALL);
	blink_condition = true;
        ctimer_set(&b_leds, 0.5*CLOCK_SECOND,blink_green_callback,NULL);
    }
    if(occupancy == 3){
	leds_off(LEDS_ALL);
	blink_condition = true;
	ctimer_set(&b_leds, 0.5*CLOCK_SECOND,blink_red_callback,NULL);
    }
    if(occupancy == 4){
	blink_condition = false;
	leds_off(LEDS_ALL);
        leds_set(LEDS_RED);
    }

}

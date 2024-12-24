#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"
#include <stdio.h>
#include "dev/leds.h"


//API FUNCTION DEFINITIONS
static void gate_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);


//RESOURCE DEFINITION
RESOURCE(auto_gate,
         "title=\"Automatic Barrier Gate\";rt=\"Control\"",
         NULL,
         NULL,
         gate_put_handler,
         NULL);

bool gate_up = false;

static void gate_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

    gate_up = (gate_up)? false : true;
    leds_set(gate_up? LEDS_GREEN : LEDS_RED); 
}
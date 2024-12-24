#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "auto gate"
#define LOG_LEVEL LOG_LEVEL_APP


#define SERVER "coap://[fd00::1]:5683"


PROCESS(gate, "Automatic Gate Control");
AUTOSTART_PROCESSES(&gate);

extern coap_resource_t auto_gate;


PROCESS_THREAD(gate, ev, data)
{
    PROCESS_BEGIN();

    leds_set(LEDS_RED);

    PROCESS_PAUSE();

    LOG_INFO("Starting Automatic Gate Barrier Controller\n");

    coap_activate_resource(&auto_gate, "auto_gate");

    PROCESS_END();
}
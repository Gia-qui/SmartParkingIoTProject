#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "monitor"
#define LOG_LEVEL LOG_LEVEL_APP


#define SERVER "coap://[fd00::1]:5683"


PROCESS(display, "Monitor Occupancy Display");
AUTOSTART_PROCESSES(&display);

extern coap_resource_t monitor;


PROCESS_THREAD(display, ev, data)
{
    PROCESS_BEGIN();

    PROCESS_PAUSE();

    LOG_INFO("Starting the Display to Monitor Occupancy\n");

    coap_activate_resource(&monitor, "monitor");

    PROCESS_END();
}
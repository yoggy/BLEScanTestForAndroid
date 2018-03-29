#include "SimpleBLE.h"
#include "esp_gap_ble_api.h"

SimpleBLE ble;

#define MANUFACTURE_DATA_LEN 10
static uint8_t manufactor_data[MANUFACTURE_DATA_LEN] =  {
  0x34, 0x12, // manufacture id
  0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88
  };

static esp_ble_adv_data_t adv_config = {
        .set_scan_rsp        = false,
        .include_name        = true,
        .include_txpower     = true,
        .min_interval        = 1024,
        .max_interval        = 2048,
        .appearance          = 0,
        .manufacturer_len    = MANUFACTURE_DATA_LEN,
        .p_manufacturer_data = &manufactor_data[0],
        .service_data_len    = 0,
        .p_service_data      = NULL,
        .service_uuid_len    = 0,
        .p_service_uuid      = NULL,
        .flag                = (ESP_BLE_ADV_FLAG_GEN_DISC|ESP_BLE_ADV_FLAG_BREDR_NOT_SPT)
};

void setup() {
    Serial.begin(115200);
    ble.begin("esp32-01");

    bool rv = esp_ble_gap_config_adv_data(&adv_config);
    Serial.println(rv);
}

void loop() {
  
}


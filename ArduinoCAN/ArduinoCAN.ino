#include "Arduino.h"
#include "frc_mcp2515.h"
#include "frc_CAN.h"
#include "DigitalInputs.h"
#include "PWMInputs.h"

// Define the CS pin and the interrupt pin
#define CAN_CS 53
#define CAN_INTERRUPT 2

// Create an MCP2515 device. Only need to create 1 of these
frc::MCP2515 mcp2515{CAN_CS};

// Create an FRC CAN Device. You can create up to 16 of these in 1 progam
// Any more will overflow a global array
frc::CAN digitalCANDevice{1};
frc::CAN pwmCANDevice{2};

frc::DigitalInputs digitalInputs;
frc::PWMInputs pwmInputs;

// Callback function. This will be called any time a new message is received
// Matching one of the enabled devices.
void CANCallback(frc::CAN* can, int apiId, bool rtr, const frc::CANData& data) 
{
}

// Callback function for any messages not matching a known device.
// This would still have flags for RTR and Extended set, its a raw ID
void UnknownMessageCallback(uint32_t id, const frc::CANData& data) 
{
}

void setup()
{
    pinMode(14, INPUT);         // ID 0   Shooter Angle Encoder
    pinMode(15, INPUT);         // ID 1   Moving Arm Angle Encoder
    pinMode(16, INPUT);         // ID 2   Feeder Angle Encoder
  
    pinMode(30, INPUT_PULLUP);  // ID 0   Shooter Botom Limit
    pinMode(31, INPUT_PULLUP);  // ID 1   Shooter Top Limit

    pinMode(38, INPUT_PULLUP);  // ID 2   Moving Arm Max Limit
    pinMode(39, INPUT_PULLUP);  // ID 3   Fixed Arm Max Limit
  
    pinMode(40, INPUT_PULLUP);  // ID 4   Fixed Arm Rear Right Switch
    pinMode(41, INPUT_PULLUP);  // ID 5   Fixed Arm Front Right Switch
    pinMode(42, INPUT_PULLUP);  // ID 6   Moving Arm Right Switch
    pinMode(43, INPUT_PULLUP);  // ID 7   Fixed Arm Rear Left Switch
    pinMode(44, INPUT_PULLUP);  // ID 8   Fixed Arm Front Left Switch
    pinMode(45, INPUT_PULLUP);  // ID 9   Moving Arm Left Switch
    pinMode(46, INPUT_PULLUP);  // ID 10  Fixed Arm Min Limit
    pinMode(47, INPUT_PULLUP);  // ID 11  Moving Arm Min Limit
    pinMode(48, INPUT_PULLUP);  // ID 12  Feeder Top Limit
    pinMode(49, INPUT_PULLUP);  // ID 13  Feeder Botom Limit
  
    digitalInputs.addInput(30);
    digitalInputs.addInput(31);

    digitalInputs.addInput(38);
    digitalInputs.addInput(39);

    digitalInputs.addInput(40);
    digitalInputs.addInput(41);
    digitalInputs.addInput(42);
    digitalInputs.addInput(43);
    digitalInputs.addInput(44);
    digitalInputs.addInput(45);
    digitalInputs.addInput(46);
    digitalInputs.addInput(47);
    digitalInputs.addInput(48);
    digitalInputs.addInput(49);

    pwmInputs.addInput(14);
    pwmInputs.addInput(15);
    pwmInputs.addInput(16);

    /*
stty -F /dev/ttyACM0 raw 9600
cat /dev/ttyACM0
    */
    Serial.begin(115200);
    
    // Initialize the MCP2515. If any error values are set, initialization failed
    auto err = mcp2515.reset();
    Serial.println(err);
    
    // CAN rate must be 1000KBPS to work with the FRC Ecosystem
    // Clock rate must match clock rate of CAN Board.
    err = mcp2515.setBitrate(frc::CAN_1000KBPS, frc::CAN_CLOCK::MCP_16MHZ);
    Serial.println(err);

    // Set up to normal CAN mode
    err = mcp2515.setNormalMode();
    Serial.println(err);

    // Prepare our interrupt pin
    pinMode(CAN_INTERRUPT, INPUT);
    
    // Set up FRC CAN to be able to use the CAN Impl and callbacks
    // Last parameter can be set to nullptr if unknown messages should be skipped
    frc::CAN::SetCANImpl(&mcp2515, CAN_INTERRUPT, CANCallback, UnknownMessageCallback);

    // All CAN Devices must be added to the read list. Otherwise they will not be handled correctly.
    digitalCANDevice.AddToReadList();
    pwmCANDevice.AddToReadList();
}

unsigned long long lastSent = 0;

void loop()
{
    // Update must be called every loop in order to receive messages
    frc::CAN::Update();

    // Writes can happen any time, this uses a periodic send
    auto now = millis();
    if (now - lastSent > 20) 
    {
        lastSent = now;

        auto digital = digitalInputs.generatePacket(false);
        auto err = digitalCANDevice.WritePacket(digital.data, 8, 0);

        Serial.println((long)digital.num);

        for (int i = 0; i < pwmInputs.inputCount; i++)
        {
            err = pwmCANDevice.WritePacket(pwmInputs.generatePacket(i).data, 4, i);
        }
    }
}

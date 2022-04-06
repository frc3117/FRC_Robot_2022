package frc.robot.System.Data;

import frc.robot.Library.FRC_3117_Tools.Component.CAN.AnalogInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.DigitalInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;

public class FeederData 
{
    public FeederData() { }

    public MotorController AngleMotor;
    public MotorController FeedMotor;

    public AnalogInputCAN AngleEncoder;

    public DigitalInputCAN TopLimitSwitch;
    public DigitalInputCAN BottomLimitSwitch;
}

package frc.robot.System.Data;

import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.AnalogInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.DigitalInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;

public class ClimberData 
{
    public ClimberData() { }

    public MotorController FixedArmLenghtMotor;
    public MotorController MovingArmLenghtMotor;
    public MotorController MovingArmAngleMotor;

    public BaseController FixedArmLenghtController;
    public BaseController MovingArmLenghtController;
    public BaseController MovingArmAngleController;

    public Encoder FixedArmLenghtEncoder;
    public Encoder MovingArmLenghtEncoder;
    public AnalogInputCAN MovingArmAngleEncoder;

    public DigitalInputCAN FixedArmFrontLeftSwitch;
    public DigitalInputCAN FixedArmRearLeftSwitch;
    public DigitalInputCAN FixedArmFrontRightSwitch;
    public DigitalInputCAN FixedArmRearRightSwitch;
    public DigitalInputCAN MovingArmLeftSwitch;
    public DigitalInputCAN MovingArmRightSwitch;
}

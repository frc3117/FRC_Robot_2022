package frc.robot.System.Data;

import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.AnalogInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.DigitalInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;

public class ShooterData 
{
    public ShooterData() { }

    public MotorControllerGroup SpeedMotorGroup;
    public MotorController IntakeMotor;
    public MotorController AngleMotor;

    public BaseController SpeedController;
    public BaseController AngleController;
    public BaseController DirectionController;

    public Encoder SpeedEncoder;

    public AnalogInputCAN AngleEncoder;

    public DigitalInputCAN AngleTopLimit;
    public DigitalInputCAN AngleBotomLimit;
}

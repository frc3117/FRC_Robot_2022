package frc.robot.System.Data;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;

public class ShooterData 
{
    public ShooterData() { }

    public MotorControllerGroup SpeedMotorGroup;
    public MotorController IntakeMotor;

    public BaseController SpeedController;
    public BaseController DirectionController;

    public Encoder SpeedEncoder;
}

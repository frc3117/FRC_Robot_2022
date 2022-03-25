package frc.robot.System.Data;

import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;

public class ConveyorData 
{
    public ConveyorData() { }

    public MotorController HorizontalMotor;
    public MotorController VerticalMotor;

    public BaseController HorizontalController;
    public BaseController VerticalController;

    public Encoder HorizontalEncoder;
    public Encoder VerticalEncoder;
}

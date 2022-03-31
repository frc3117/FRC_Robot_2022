package frc.robot.System.Data.Internal;

import frc.robot.Library.FRC_3117_Tools.Component.FunctionScheduler;

public class ClimberDataInternal 
{
    public ClimberDataInternal() { }

    public FunctionScheduler CalibrationSequence;
    public FunctionScheduler ClimbSequence;
    public FunctionScheduler ClimbSequenceSafe;
    public FunctionScheduler CurrentSequence;

    public double MovingArmTargetAngle;
    public double MovingArmTargetLenght;
    public double FixedArmTargetLenght;
}

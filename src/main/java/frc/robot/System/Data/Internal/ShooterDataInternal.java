package frc.robot.System.Data.Internal;

import frc.robot.Library.FRC_3117_Tools.Component.Swerve;
import frc.robot.Library.FRC_3117_Tools.Math.MovingAverage;

public class ShooterDataInternal 
{
    public ShooterDataInternal() { }

    public MovingAverage ShooterRPMAverage;
    public MovingAverage DistanceAverager; 

    public boolean IsAllign;
    public boolean IsCalibrating;
    public boolean IsManualShooter;

    public int TargerRPM;

    public double ShooterTargetAngle;
    public double ShooterAngleOffset;

    public Swerve Swerve;
}

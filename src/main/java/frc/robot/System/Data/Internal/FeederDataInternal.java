package frc.robot.System.Data.Internal;

import frc.robot.System.Feeder;
import frc.robot.System.Feeder.AngleTarget;

public class FeederDataInternal 
{
    public FeederDataInternal() { }

    public boolean IsCalibrating;
    public boolean IsCalibratingTop;

    public double AngleOffset;
    public double BottomAngle;
    public double FeederSpeed;

    public Feeder.AngleTarget Target = AngleTarget.Up;
}

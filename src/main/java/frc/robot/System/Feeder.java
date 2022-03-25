package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.System.Data.FeederData;
import frc.robot.System.Data.Internal.FeederDataInternal;

public class Feeder implements Component
{
    public Feeder(FeederData data, FeederDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }

    public FeederData Data;
    public FeederDataInternal DataInternal;

    @Override
    public void Awake() 
    {

    }

    @Override
    public void Init() 
    {
        
    }

    @Override
    public void Disabled() 
    {
    }

    @Override
    public void DoComponent() 
    {
        if (Input.GetButton("FeederUpAnalog"))
        {
            Data.AngleMotor.Set(0.25);
        }
        else if (Input.GetButton("FeederDownAnalog"))
        {
            Data.AngleMotor.Set(-0.20);
        }
        else
        {
            Data.AngleMotor.Set(0);
        }
    }
}

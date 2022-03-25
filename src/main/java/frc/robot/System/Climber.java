package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.System.Data.ClimberData;
import frc.robot.System.Data.Internal.ClimberDataInternal;

public class Climber implements Component
{
    public Climber(ClimberData data, ClimberDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }

    public ClimberData Data;
    public ClimberDataInternal DataInternal;

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
        
    }
}

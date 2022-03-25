package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.System.Data.ConveyorData;
import frc.robot.System.Data.Internal.ConveyorDataInternal;

public class ConveyorBelt implements Component
{
    public ConveyorBelt(ConveyorData data, ConveyorDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }   
    
    public ConveyorData Data;
    public ConveyorDataInternal DataInternal;
    
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
        if (Input.GetButton("ConveyorForward"))
        {
            Data.VerticalMotor.Set(-1);
        }
        else if (Input.GetButton("ConveyorBackward"))
        {
            Data.VerticalMotor.Set(0.5);
        }
        else
        {
            Data.VerticalMotor.Set(0);
        }
    }
}

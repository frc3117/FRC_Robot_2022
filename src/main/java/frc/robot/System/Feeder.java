package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Component.CAN.MultiDigitalInput;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;

public class Feeder implements Component
{
    public Feeder(MotorController feederAngleMotor)
    {
        _feederAngleMotor = feederAngleMotor;
    }

    private MotorController _feederAngleMotor;

    private MultiDigitalInput _limitSwitches;

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
            _feederAngleMotor.Set(0.25);
        }
        else if (Input.GetButton("FeederDownAnalog"))
        {
            _feederAngleMotor.Set(-0.20);
        }
        else
        {
            _feederAngleMotor.Set(0);
        }
    }
}

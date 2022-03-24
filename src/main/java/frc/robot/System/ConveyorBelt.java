package frc.robot.System;

import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;

public class ConveyorBelt implements Component
{
    public ConveyorBelt(MotorController towerMotor, BaseController towerController, Encoder towerEncoder)
    {
        _towerMotor = towerMotor;
        _towerController = towerController;
        _towerEncoder = towerEncoder;
    }   
    
    private MotorController _towerMotor;
    private BaseController _towerController;
    private Encoder _towerEncoder;
    
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
            _towerMotor.Set(-1);
        }
        else if (Input.GetButton("ConveyorBackward"))
        {
            _towerMotor.Set(0.5);
        }
        else
        {
            _towerMotor.Set(0);
        }
    }
}

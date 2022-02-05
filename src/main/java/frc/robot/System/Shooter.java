package frc.robot.System;

import edu.wpi.first.wpilibj.Encoder;
import frc.robot.Library.FRC_3117.Component.Data.InputManager;
import frc.robot.Library.FRC_3117.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117.Interface.BaseController;
import frc.robot.Library.FRC_3117.Interface.Component;

public class Shooter implements Component
{
    public Shooter(MotorControllerGroup motorGroup, Encoder shooterEncoder, BaseController shooterController)
    {
        _motorGroup = motorGroup;
        _shooterEncoder = shooterEncoder;
        _shooterController = shooterController;
    }

    private Encoder _shooterEncoder;
    private MotorControllerGroup _motorGroup;
    private BaseController _shooterController;

    private int _targerRPM;

    @Override
    public void Awake() 
    {
        _motorGroup.SetBrake(false);
    }

    @Override
    public void Init() 
    {
        _targerRPM = 0;
    }

    @Override
    public void Disabled() 
    {

    }

    @Override
    public void DoComponent()
    {
        if (InputManager.GetButton("Shooter"))
            _targerRPM = 3000;
        else
            _targerRPM = 0;

        if (_targerRPM > 0)
        {
            var currentSpeed = (_shooterEncoder.getRate() / 2048) * 60;
            var error = _targerRPM - currentSpeed;
            
            _motorGroup.Set(_shooterController.Evaluate(error));
        }
    }    
}
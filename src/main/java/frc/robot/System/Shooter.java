package frc.robot.System;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Debug.CsvLogger;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;

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

    private boolean _isfeedforwardCalculation;
    private CsvLogger _feedforwardCalculationLogger;

    @Override
    public void Awake() 
    {
        _motorGroup.SetBrake(false);
    }

    @Override
    public void Init() 
    {
        _isfeedforwardCalculation = false;
        _targerRPM = 0;
    }

    @Override
    public void Disabled() 
    {

    }

    @Override
    public void DoComponent()
    {
        var currentSpeed = (_shooterEncoder.getRate() / 2048) * 60;
        SmartDashboard.putNumber("shooterRPM", currentSpeed);
        
        if (_isfeedforwardCalculation)
        {
            _shooterController.SetFeedForward(_targerRPM * 0.00015);

            if (InputManager.GetButton("Shooter"))
                _targerRPM = 3000;
            else
                _targerRPM = 0;

            if (_targerRPM > 0)
            {
                var error = _targerRPM - currentSpeed;
            
                _motorGroup.Set(_shooterController.Evaluate(error));
            }
            else
                _motorGroup.Set(0);
        }
        else
        {
            
        }
    }    

    public void StartFeedforwardCalculator()
    {
        _feedforwardCalculationLogger = new CsvLogger();

        _feedforwardCalculationLogger.AddColumn("RPM");
        _feedforwardCalculationLogger.AddColumn("Integral");

        _isfeedforwardCalculation = true;
    }

    public void StopFeedforwardCalculator()
    {
        _feedforwardCalculationLogger.SaveToFile("FeedforwardCalculation");

        _isfeedforwardCalculation = false;
    }
}

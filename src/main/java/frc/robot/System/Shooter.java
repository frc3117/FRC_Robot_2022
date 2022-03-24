package frc.robot.System;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.Library.FRC_3117_Tools.Component.LimeLight;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Debug.CsvLogger;
import frc.robot.Library.FRC_3117_Tools.Interface.BaseController;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Library.FRC_3117_Tools.Math.MovingAverage;

public class Shooter implements Component
{
    public Shooter(MotorControllerGroup motorGroup, MotorController shooterIntakeMotor, MotorController shooterAngleMotor, Encoder shooterEncoder, BaseController shooterController, BaseController directionController)
    {
        _motorGroup = motorGroup;
        _shooterIntakeMotor = shooterIntakeMotor;
        _shooterAngleMotor = shooterAngleMotor;
        _shooterEncoder = shooterEncoder;
        _shooterController = shooterController;
        _directionController = directionController;

        _shooterAngleBotomLimit = new DigitalInput(8);
        _shooterAngleTopLimit = new DigitalInput(9);

        _errorMovingAverageFeedforward = new MovingAverage(15);
        _shooterInputMovingAverage = new MovingAverage(60);
    }

    private Encoder _shooterEncoder;
    private MotorControllerGroup _motorGroup;
    private MotorController _shooterIntakeMotor;
    private MotorController _shooterAngleMotor;
    private BaseController _shooterController;
    private BaseController _directionController;
    private DigitalInput _shooterAngleTopLimit;
    private DigitalInput _shooterAngleBotomLimit;

    private MovingAverage _shooterInputMovingAverage;

    private boolean _isAllign;
    private int _targerRPM;
    private double _shooterTargetAngle;

    private double _currentFeedforwardCalculation;
    private int _frameTotalFeedforwardCalculation;
    private int _frameOverMaxFeedforwardCalculation;
    private MovingAverage _errorMovingAverageFeedforward;
    private boolean _isfeedforwardCalculation;
    private CsvLogger _feedforwardCalculationLogger;

    private Swerve _swerve;

    private Joystick _tempJoystock;

    @Override
    public void Awake() 
    {
        _motorGroup.SetBrake(false);

        _swerve = Robot.GetComponent("Swerve");

        _tempJoystock = new Joystick(0);
    }

    @Override
    public void Init() 
    {
        _isfeedforwardCalculation = false;
        _targerRPM = 0;
        _isAllign = false;
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
        
        if (InputManager.GetButtonDown("Align"))
        {
            System.out.println(_isAllign);
            _isAllign = !_isAllign;
        }

        if (_isAllign)
        {
            var currentLimelight = LimeLight.GetCurrent();

            if(currentLimelight.IsTarget())
            {
                _swerve.OverrideRotationAxis(_directionController.Evaluate(-1 * currentLimelight.GetAngleX()));
            }
        }

        switch (_tempJoystock.getPOV())
        {
            case 0:
                if (!_shooterAngleTopLimit.get())
                    _shooterAngleMotor.Set(0.25);
                else
                    _shooterAngleMotor.Set(0);
                break;

            case 180:
                if (!_shooterAngleBotomLimit.get())
                    _shooterAngleMotor.Set(-0.17);
                else
                    _shooterAngleMotor.Set(0);
                break;

            default:
                _shooterAngleMotor.Set(0);
                break;
        }

        if (!_isfeedforwardCalculation)
        {
            _shooterController.SetFeedForward(_targerRPM * 0.00015);

            if (InputManager.GetButton("Shooter"))
            {
                SetShooterRPM(3000);
                _shooterInputMovingAverage.Evaluate(1);
            }
            else
            {
                SetShooterRPM(0);
                _shooterInputMovingAverage.Evaluate(0);
            }

            if (_targerRPM > 0)
            {
                var error = _targerRPM - currentSpeed;
            
                if (_shooterInputMovingAverage.GetCurrent() > 0.8)
                    _shooterIntakeMotor.Set(-0.5);
                else
                    _shooterIntakeMotor.Set(0);

                _motorGroup.Set(_shooterController.Evaluate(error) * -1);
            }
            else
            {
                _motorGroup.Set(0);
                _shooterIntakeMotor.Set(0);
            }
        }
        else
        {
            _frameTotalFeedforwardCalculation++;
            _errorMovingAverageFeedforward.Evaluate(currentSpeed - _targerRPM);

            if (_currentFeedforwardCalculation > 1)
                _frameOverMaxFeedforwardCalculation++;
            else
                _frameOverMaxFeedforwardCalculation = 0;

            if ((_currentFeedforwardCalculation > 1 && _frameOverMaxFeedforwardCalculation >= 100) || _frameTotalFeedforwardCalculation >= 1000)
            {
                //Failed
                _feedforwardCalculationLogger.SetValue("RPM", _targerRPM);
                _feedforwardCalculationLogger.SetValue("Integral", -9999);

                SetShooterRPM(_targerRPM + 100);
                _errorMovingAverageFeedforward.Clear();
            }
            else if (Math.abs(_errorMovingAverageFeedforward.GetCurrent()) <= 250 )
            {
                //Success
                _feedforwardCalculationLogger.SetValue("RPM", _targerRPM);
                _feedforwardCalculationLogger.SetValue("Integral", _currentFeedforwardCalculation);

                SetShooterRPM(_targerRPM + 100);
                _errorMovingAverageFeedforward.Clear();
            }

            if (_targerRPM >= 6000)
            {
                StopFeedforwardCalculator();
                return;
            }

            _motorGroup.Set(_currentFeedforwardCalculation);
        }
    }    

    public void StartFeedforwardCalculator()
    {
        _feedforwardCalculationLogger = new CsvLogger();

        _feedforwardCalculationLogger.AddColumn("RPM");
        _feedforwardCalculationLogger.AddColumn("Integral");

        _currentFeedforwardCalculation = 0;
        _errorMovingAverageFeedforward.Clear();
        _isfeedforwardCalculation = true;
    }
    public void StopFeedforwardCalculator()
    {
        _feedforwardCalculationLogger.SaveToFile("FeedforwardCalculation");

        _isfeedforwardCalculation = false;
    }

    public void SetShooterRPM(int targetRPM)
    {
        _targerRPM = targetRPM;
    }
    public void SetShooterAngle(double targetAngle)
    {
        _shooterTargetAngle = targetAngle;
    }
}

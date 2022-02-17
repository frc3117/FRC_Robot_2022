package frc.robot.System;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Library.FRC_3117_Tools.Math.Timer;

public class Conveyor implements Component
{
    public Conveyor(AnalogInput ultrasonicSensor, ColorSensorV3 colorSensor, MotorController horizontalMotor, MotorController verticalMotor)
    {
        _ultrasonicSensor = ultrasonicSensor;
        _colorSensor = colorSensor;
        _horizontalMotor = horizontalMotor;
        _verticalMotor = verticalMotor;
    }

    private final Color _redCargo = new Color(0, 0, 0);
    private final Color _blueCargo = new Color(0, 0, 0);

    private AnalogInput _ultrasonicSensor;

    private ColorSensorV3 _colorSensor;
    private ColorMatch _colorMatch;

    private MotorController _horizontalMotor;
    private MotorController _verticalMotor;    

    private double _lastDistance;

    //Speed threshold to consider a ball
    private double _distanceDerivativeThreshold;

    //Could be replace by enum/int array if class is overkill
    private Cargo[] _currentCargo = new Cargo[2];

    @Override
    public void Awake() 
    {
        _colorMatch.addColorMatch(_redCargo);
        _colorMatch.addColorMatch(_blueCargo);
    }

    @Override
    public void Init() 
    {
        _lastDistance = GetDistance();

        _currentCargo[0] = null;
        _currentCargo[1] = null;
    }

    @Override
    public void Disabled() 
    {
        
    }

    @Override
    public void DoComponent() 
    {
        if (DetectNewCargo())
        {
            var detectedColor = _colorSensor.getColor();
            var colorMatchResult = _colorMatch.matchClosestColor(detectedColor);

            var isRed = false;
            if (colorMatchResult.color == _redCargo)
            {
                isRed = true;
            }

            var isOurs = (DriverStation.getAlliance() == Alliance.Red) == isRed;
            System.out.println(isOurs);

            var cargo = new Cargo(isOurs);
            if (_currentCargo[0] == null)
            {
                _currentCargo[0] = cargo;
            }
            else if (_currentCargo[1] == null)
            {
                _currentCargo[1] = cargo;
            }
            else
            {
                //More than 2 cargo
                System.out.println("Bruh!");
            }
        }

        var firstCargo = _currentCargo[0];
        if (firstCargo != null)
        {
            if (firstCargo.IsOurs) 
            {
                _verticalMotor.Set(0.5);
            }

            _horizontalMotor.Set(0.5);
        }
    }

    private boolean DetectNewCargo()
    {
        var distance = GetDistance();

        if ((distance - _lastDistance) / Timer.GetDeltaTime() >= _distanceDerivativeThreshold)
        {
            _lastDistance = distance;
            return true;
        }

        _lastDistance = distance;
        return false;
    }

    private double GetDistance()
    {
        var voltageScaleFactor = 5 / RobotController.getVoltage5V();
        return _ultrasonicSensor.getValue() * voltageScaleFactor * 0.125;
    }

    class Cargo
    {
        public Cargo (boolean isOurs)
        {
            currentSegment = 0;
            IsOurs = isOurs;
        }

        public int currentSegment;
        public boolean IsOurs;
    }
}
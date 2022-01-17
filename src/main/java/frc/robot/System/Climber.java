package frc.robot.System;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import frc.robot.Robot;
import frc.robot.System.Part.LinearActuator;
import frc.robot.System.Part.PID;

public class Climber
{
    public static final int ARM_ANGLE_1_CHANNEL = 0;
    public static final int ARM_ANGLE_2_CHANNEL = 1;

    public static final int ARM_LINEAR_1_MOTOR_CHANNEL = 2;
    public static final int ARM_LINEAR_2_MOTOR_CHANNEL = 3;

    public static final int ARM_LINEAR_1_ENCODER_CHANNEL = 0;
    public static final int ARM_LINEAR_2_ENCODER_CHANNEL = 1;

    public static final int DISTANCE_LEFT_CHANNEL = 2;
    public static final int DISTANCE_RIGHT_CHANNEL = 3;

    public static final int LINE_SENSOR_CHANNEL = 0;

    private Joystick _joystick;

    private Servo _armAngle1;
    private Servo _armAngle2;
    private LinearActuator _armLinear1;
    private LinearActuator _armLinear2;

    private AnalogInput _distanceLeft;
    private AnalogInput _distanceRight;

    private DigitalInput _lineSensor;

    private boolean _isStarted = false;
    private State _currentState = State.Allign;
    private boolean _isFirstLoopCurrentState = true;

    private PID _alignPID = new PID(1, 0, 0);

    public Climber()
    {
        _joystick = new Joystick(0);

        _armAngle1 = new Servo(ARM_ANGLE_1_CHANNEL);
        _armAngle2 = new Servo(ARM_ANGLE_2_CHANNEL);

        _armLinear1 = new LinearActuator(ARM_LINEAR_1_MOTOR_CHANNEL, ARM_LINEAR_1_ENCODER_CHANNEL, new PID(1, 0, 0));
        _armLinear2 = new LinearActuator(ARM_LINEAR_2_MOTOR_CHANNEL, ARM_LINEAR_2_ENCODER_CHANNEL, new PID(1, 0, 0));

        _distanceLeft = new AnalogInput(DISTANCE_LEFT_CHANNEL);
        _distanceRight = new AnalogInput(DISTANCE_RIGHT_CHANNEL);

        _lineSensor = new DigitalInput(LINE_SENSOR_CHANNEL);
    }

    public void Init()
    {

    }

    public void DoSystem()
    {
        _armLinear1.Loop();
        _armLinear2.Loop();

        if(_isStarted)
        {
            if(_joystick.getRawButtonPressed(1))
            {
                _isStarted = false;
                return;
            }

            var canContinue = true;
            while(canContinue)
            {
                switch(_currentState)
                {
                    case Allign:
                        var allignError = _distanceLeft.getValue() - _distanceRight.getValue();
                    
                        if(Math.abs(allignError) <= 0.5)
                        {
                            NextStep();
                        }
                        else
                        {
                            Robot.Drivetrain.OverrideHorizontal(_alignPID.Evaluate(allignError));
                            canContinue = false;
                        }
                        break;

                    case Line:
                        if(_lineSensor.get())
                        {
                            NextStep();
                        }
                        else
                        {
                            Robot.Drivetrain.OverrideVertical(0.5);
                            canContinue = false;
                        }
                        break;

                    case Level2Up:
                        if(_isFirstLoopCurrentState)
                        {
                            _armLinear1.SetPosition(1);
                        }
                        else if(_armLinear1.IsTarget())
                        {
                            NextStep();
                        }
                        else
                        {
                            canContinue = false;
                        }
                        break;

                    default:
                        _isStarted = false;
                        canContinue = false;
                        break;
                }       
            }

            _isFirstLoopCurrentState = false;
        }
        else if(_joystick.getRawButtonPressed(1))
        {
            _isStarted = true;
            _currentState = State.Allign;
        }
    }

    public void NextStep()
    {
        _isFirstLoopCurrentState = true;
        _currentState = _currentState.Next();
    }

    private enum State
    {
        Allign,
        Line,
        Level2Up;

        public State Next() {
            return values()[ordinal() + 1];
        }
    }
} 

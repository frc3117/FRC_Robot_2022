package frc.robot.System.Part;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;

public class LinearActuator 
{
    public LinearActuator(int motorChannel, int encoderChannel, PID pid)
    {
        _motor = new PWMTalonSRX(encoderChannel);
        _encoder = new AnalogInput(encoderChannel);
        _pid = pid;
    }

    private AnalogInput _encoder;
    private PWMTalonSRX _motor;
    private PID _pid;

    private double _targetPosition;

    public void Loop()
    {
        var error = EstimateError();
        _motor.set(_pid.Evaluate(error));
    }

    public void SetPosition(double position)
    {
        _targetPosition = position;
    }

    public boolean IsTarget() 
    {
        return Math.abs(EstimateError()) <= 0.02;
    }

    private double EstimateError()
    {
        return (_encoder.getValue() / 4096.) - _targetPosition;
    }
}

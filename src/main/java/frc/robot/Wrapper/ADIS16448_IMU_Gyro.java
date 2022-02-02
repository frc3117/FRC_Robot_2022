package frc.robot.Wrapper;

import edu.wpi.first.wpilibj.ADIS16448_IMU;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class ADIS16448_IMU_Gyro implements Gyro
{
    public ADIS16448_IMU_Gyro()
    {
        _imu = new ADIS16448_IMU();
    }

    private ADIS16448_IMU _imu;

    @Override
    public void close()
    {
        _imu.close();
    }

    @Override
    public void calibrate() 
    {
        _imu.calibrate();
    }

    @Override
    public void reset() 
    {
        _imu.reset();
    }

    @Override
    public double getAngle() 
    {
        return _imu.getGyroAngleZ();
    }

    @Override
    public double getRate() 
    {
        return _imu.getGyroRateZ();
    }
}

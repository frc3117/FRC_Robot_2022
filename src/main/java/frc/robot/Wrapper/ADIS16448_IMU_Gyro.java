package frc.robot.Wrapper;

import edu.wpi.first.wpilibj.ADIS16448_IMU;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class ADIS16448_IMU_Gyro implements Gyro
{
    public ADIS16448_IMU_Gyro()
    {
        _imu = new ADIS16448_IMU(ADIS16448_IMU.IMUAxis.kZ, Port.kMXP, ADIS16448_IMU.CalibrationTime._1s);
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

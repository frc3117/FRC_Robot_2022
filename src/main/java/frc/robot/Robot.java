package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Library.FRC_3117_Tools.RobotBase;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.MultiAnalogInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.CAN.MultiDigitalInputCAN;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Component.Data.WheelData;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController.MotorControllerType;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Tupple.Pair;
import frc.robot.Library.FRC_3117_Tools.Component.FRC_Robot_Server.RobotServerClient;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve.DrivingMode;
import frc.robot.Library.FRC_3117_Tools.Math.SimplePID;
import frc.robot.System.Climber;
import frc.robot.System.Feeder;
import frc.robot.System.Shooter;
import frc.robot.System.Data.ClimberData;
import frc.robot.System.Data.FeederData;
import frc.robot.System.Data.ShooterData;
import frc.robot.System.Data.Internal.ClimberDataInternal;
import frc.robot.System.Data.Internal.FeederDataInternal;
import frc.robot.System.Data.Internal.ShooterDataInternal;
import frc.robot.Wrapper.ADIS16448_IMU_Gyro;

public class Robot extends RobotBase {

  public enum AutonomousMode
  {
    
  }

  public static Robot instance;
  public static RobotServerClient serverClient;
  public static AutonomousMode currentAutonomous;

  private SendableChooser<AutonomousMode> _autoChooser;
  private MultiDigitalInputCAN _digitalInputs;
  private MultiAnalogInputCAN _analogInputs;

  @Override
  public void robotInit()
  {
    instance = this;

    _autoChooser = new SendableChooser<>();

    for(var mode : AutonomousMode.values())
    {
      _autoChooser.addOption(mode.toString(), mode);
    }
    SmartDashboard.putData("AutonomousSelector", _autoChooser);
    
    serverClient = new RobotServerClient("10.31.17.14");
    serverClient.Connect(() -> 
    {
      serverClient.Register("rio");

      serverClient.Subscribe("imu", x ->
      {
        System.out.println(x.Data);
      });
    });

    _digitalInputs = new MultiDigitalInputCAN(1);
    _analogInputs = new MultiAnalogInputCAN(2, 1024);

    super.robotInit();
  }

  @Override
  public void CreateComponentInstance()
  {
    super.CreateComponentInstance();

    //Swerve
    var wheelsData = new WheelData[] 
    {
      new WheelData(new MotorController(MotorControllerType.TalonFX, 22, true), new MotorController(MotorControllerType.SparkMax, 16, true), new Pair<>(0, 0), 1, new Vector2d(-0.62320, 0.78206), 0.17640258),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 23, true), new MotorController(MotorControllerType.SparkMax, 15, true), new Pair<>(0, 0), 2, new Vector2d(0.62320, 0.78206), 5.1831684 - Math.PI),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 21, true), new MotorController(MotorControllerType.SparkMax, 17, true), new Pair<>(0, 0), 3, new Vector2d(0.62320, -0.78206), 4.19378 - Math.PI),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 20, true), new MotorController(MotorControllerType.SparkMax, 14, true), new Pair<>(0, 0), 0, new Vector2d(-0.62320, -0.78206), 3.2458076)
    };

    var swerve = new Swerve(wheelsData, new ADIS16448_IMU_Gyro());
    swerve.SetPIDGain(0, 1, 0, 0);
    swerve.SetPIDGain(1, 1, 0, 0);
    swerve.SetPIDGain(2, 1, 0, 0);
    swerve.SetPIDGain(3, 1, 0, 0);

    swerve.SetCurrentMode(DrivingMode.Local);
    AddComponent("Swerve", swerve);

    //Shooter
    var shooterData = new ShooterData();
    var shooterDataInternal = new ShooterDataInternal();

    shooterData.SpeedMotorGroup = new MotorControllerGroup();
    shooterData.SpeedMotorGroup.AddPositiveController(new MotorController(MotorControllerType.SparkMax, 6, true));

    shooterData.IntakeMotor = new MotorController(MotorControllerType.TalonSRX, 3, false);
    shooterData.IntakeMotor.SetInverted(true);

    shooterData.AngleMotor = new MotorController(MotorControllerType.TalonSRX, 2, false);
    shooterData.AngleMotor.SetBrake(true);
    shooterData.AngleMotor.SetInverted(true);

    shooterData.SpeedEncoder = new Encoder(0, 1);

    shooterData.ShooterAngleEncoder = _analogInputs.GetAnalogInput(0);

    shooterData.SpeedController = new SimplePID(0.001, 0, 0, "Shooter");
    shooterData.DirectionController = new SimplePID(0.03, 0, 0.002, "Direction");
    shooterData.AngleController = new SimplePID(0, 0, 0 ,"ShooterAngle");

    AddComponent("Shooter", new Shooter(shooterData, shooterDataInternal));

    //Feeder
    var feederData = new FeederData();
    var feederDataInternal = new FeederDataInternal();

    feederData.AngleMotor = new MotorController(MotorControllerType.SparkMax, 10, true);
    feederData.AngleMotor.SetInverted(true);

    AddComponent("Feeder", new Feeder(feederData, feederDataInternal));  
    
    //Climber
    var climberData = new ClimberData();
    var climberDataInternal = new ClimberDataInternal();

    climberData.FixedArmLenghtMotor = new MotorController(MotorControllerType.SparkMax, 30, true);
    climberData.MovingArmLenghtMotor = new MotorController(MotorControllerType.SparkMax, 31, true);
    climberData.MovingArmAngleMotor = new MotorController(MotorControllerType.SparkMax, 32, true);

    climberData.FixedArmFrontLeftSwitch = _digitalInputs.GetDigitalInput(0);
    climberData.FixedArmRearLeftSwitch = _digitalInputs.GetDigitalInput(1);
    climberData.FixedArmFrontRightSwitch = _digitalInputs.GetDigitalInput(2);
    climberData.FixedArmRearRightSwitch = _digitalInputs.GetDigitalInput(3);
    climberData.MovingArmLeftSwitch = _digitalInputs.GetDigitalInput(4);
    climberData.MovingArmRightSwitch = _digitalInputs.GetDigitalInput(5);

    AddComponent("Climber", new Climber(climberData, climberDataInternal));
  }

  @Override
  public void CreateInput()
  {
    super.CreateInput();

    Input.CreateAxis("Horizontal", 0, 0, false);
    Input.CreateAxis("Vertical", 0, 1, true);
    Input.CreateAxis("Rotation", 0, 2, false);

    Input.SetAxisNegative("Rotation", 0, 3, false);

    Input.SetAxisDeadzone("Horizontal", 0.15);
    Input.SetAxisDeadzone("Vertical", 0.15);
    Input.SetAxisDeadzone("Rotation", 0.15);

    Input.CreateButton("Shooter", 0, 1);
    Input.CreateButton("Align", 0, 2);

    Input.CreateButton("FeedBackward", 0, 5);
    Input.CreateButton("FeedForward", 0, 6);

    Input.CreateButton("FeederUpAnalog", 1, 6);
    Input.CreateButton("FeederDownAnalog", 1, 5);

    Input.CreateButton("ClimberSequence", 0, 10);
    Input.CreateButton("ClimberSequenceSafe", 1, 10);
  }

  @Override
  public void Init()
  {
    super.Init();
  }

  @Override
  public void ComponentLoop()
  {
    serverClient.FeedData("digitalInputs", _digitalInputs.GetValues());

    super.ComponentLoop();
  }
}

package frc.robot;

import javax.swing.text.FlowView.FlowStrategy;

import com.ctre.phoenix.CANifier;

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
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input.XboxAxis;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input.XboxButton;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController.MotorControllerType;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Tupple.Pair;
import frc.robot.Library.FRC_3117_Tools.Component.FRC_Robot_Server.RobotServerClient;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve.DrivingMode;
import frc.robot.Library.FRC_3117_Tools.Math.Mathf;
import frc.robot.Library.FRC_3117_Tools.Math.SimplePID;
import frc.robot.System.Autonomous;
import frc.robot.System.Climber;
import frc.robot.System.Feeder;
import frc.robot.System.LED;
import frc.robot.System.Shooter;
import frc.robot.System.Data.AutonomousData;
import frc.robot.System.Data.ClimberData;
import frc.robot.System.Data.Color;
import frc.robot.System.Data.FeederData;
import frc.robot.System.Data.ShooterData;
import frc.robot.System.Data.Internal.AutonomousDataInternal;
import frc.robot.System.Data.Internal.ClimberDataInternal;
import frc.robot.System.Data.Internal.FeederDataInternal;
import frc.robot.System.Data.Internal.ShooterDataInternal;
import frc.robot.Wrapper.ADIS16448_IMU_Gyro;

public class Robot extends RobotBase {

  public enum AutonomousMode
  {
    Nothing,
    SimpleBackward
  }

  public static Robot instance;
  public static RobotServerClient serverClient;
  public static AutonomousMode currentAutonomous;
  public static LED led;

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
    
    led = new LED(new CANifier(30));

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

    _color = new Color(Math.random(), Math.random(), Math.random());

    super.robotInit();
  }

  @Override
  public void autonomousInit()
  {
    super.autonomousInit();

    currentAutonomous = _autoChooser.getSelected();
    ((Autonomous)GetComponent("Autonomous")).Start(currentAutonomous);
  }

  @Override
  public void CreateComponentInstance()
  {
    super.CreateComponentInstance();

    var autoData = new AutonomousData();
    var autoDataInternal = new AutonomousDataInternal();

    AddComponent("Autonomous", new Autonomous(autoData, autoDataInternal));

    //Swerve
    var wheelsData = new WheelData[] 
    {
      new WheelData(new MotorController(MotorControllerType.TalonFX, 22, true), new MotorController(MotorControllerType.SparkMax, 16, true), new Pair<>(0, 0), 1, new Vector2d(-0.62320, 0.78206), 0.17640258),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 23, true), new MotorController(MotorControllerType.SparkMax, 15, true), new Pair<>(0, 0), 2, new Vector2d(0.62320, 0.78206), 5.1831684 - Math.PI - 0.785398163),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 21, true), new MotorController(MotorControllerType.SparkMax, 17, true), new Pair<>(0, 0), 3, new Vector2d(0.62320, -0.78206), 4.19378 - Math.PI),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 20, true), new MotorController(MotorControllerType.SparkMax, 14, true), new Pair<>(0, 0), 0, new Vector2d(-0.62320, -0.78206), 3.2458076)
    };

    var swerve = new Swerve(wheelsData, new ADIS16448_IMU_Gyro());
    swerve.SetPIDGain(0, 1, 0, 0);
    swerve.SetPIDGain(1, 1, 0, 0);
    swerve.SetPIDGain(2, 1, 0, 0);
    swerve.SetPIDGain(3, 1, 0, 0);

    swerve.SetCurrentMode(DrivingMode.World);
    swerve.SetHeadingOffset(Math.PI / -2);
    AddComponent("Swerve", swerve);

    //Shooter
    var shooterData = new ShooterData();
    var shooterDataInternal = new ShooterDataInternal();

    shooterData.SpeedMotorGroup = new MotorControllerGroup();
    shooterData.SpeedMotorGroup.AddNegativeController(new MotorController(MotorControllerType.SparkMax, 6, true));

    shooterData.IntakeMotor = new MotorController(MotorControllerType.TalonSRX, 3, false);
    shooterData.IntakeMotor.SetInverted(true);

    shooterData.AngleMotor = new MotorController(MotorControllerType.TalonSRX, 2, false);
    shooterData.AngleMotor.SetBrake(true);
    shooterData.AngleMotor.SetInverted(true);

    shooterData.AngleTopLimit = _digitalInputs.GetDigitalInput(1);
    shooterData.AngleBottomLimit = _digitalInputs.GetDigitalInput(0);

    shooterData.SpeedEncoder = new Encoder(6, 7);

    shooterData.AngleEncoder = _analogInputs.GetAnalogInput(0);

    shooterData.SpeedController = new SimplePID(0, 0.0001, 0, "Shooter");
    shooterData.DirectionController = new SimplePID(0.03, 0, 0.002, "Direction");
    shooterData.AngleController = new SimplePID(0, 0, 0 ,"ShooterAngle");

    AddComponent("Shooter", new Shooter(shooterData, shooterDataInternal));

    //Feeder
    var feederData = new FeederData();
    var feederDataInternal = new FeederDataInternal();

    feederData.AngleMotor = new MotorController(MotorControllerType.SparkMax, 10, true);
    feederData.AngleMotor.SetInverted(true);

    feederData.FeedMotor = new MotorController(MotorControllerType.SparkMax, 5, true);

    feederData.TopLimitSwitch = _digitalInputs.GetDigitalInput(12).SetReversed(true);
    feederData.BottomLimitSwitch = _digitalInputs.GetDigitalInput(13);

    AddComponent("Feeder", new Feeder(feederData, feederDataInternal));  
    
    //Climber
    var climberData = new ClimberData();
    var climberDataInternal = new ClimberDataInternal();

    climberData.FixedArmLenghtMotor = new MotorController(MotorControllerType.SparkMax, 7, true);
    climberData.FixedArmLenghtMotor.SetInverted(true);
    climberData.FixedArmLenghtMotor.SetBrake(true);

    climberData.MovingArmLenghtMotor = new MotorController(MotorControllerType.SparkMax, 8, true);
    climberData.MovingArmLenghtMotor.SetInverted(false);
    climberData.MovingArmLenghtMotor.SetBrake(true);

    climberData.MovingArmAngleMotor = new MotorController(MotorControllerType.SparkMax, 9, true);
    climberData.MovingArmAngleMotor.SetInverted(true);
    climberData.MovingArmAngleMotor.SetBrake(true);

    climberData.MovingArmAngleEncoder = new Encoder(8, 9);
    climberData.MovingArmAngleEncoder.setDistancePerPulse(0.5980066 / -12.577777);

    climberData.FixedArmLenghtEncoder = new Encoder(0, 1);
    climberData.FixedArmLenghtEncoder.setDistancePerPulse((0.64 / -27366) * 1.125);

    climberData.MovingArmLenghtEncoder = new Encoder(3, 4);
    climberData.MovingArmLenghtEncoder.setDistancePerPulse(0.64 / -27366);

    climberData.FixedArmBottomSwitch = _digitalInputs.GetDigitalInput(10);
    climberData.MovingArmBottomSwitch = _digitalInputs.GetDigitalInput(11);

    climberData.FixedArmTopSwitch = _digitalInputs.GetDigitalInput(3);
    climberData.MovingArmTopSwitch = _digitalInputs.GetDigitalInput(2);

    climberData.FixedArmFrontLeftSwitch = _digitalInputs.GetDigitalInput(8);
    climberData.FixedArmRearLeftSwitch = _digitalInputs.GetDigitalInput(7);
    climberData.FixedArmFrontRightSwitch = _digitalInputs.GetDigitalInput(5);
    climberData.FixedArmRearRightSwitch = _digitalInputs.GetDigitalInput(4);
    climberData.MovingArmLeftSwitch = _digitalInputs.GetDigitalInput(9);
    climberData.MovingArmRightSwitch = _digitalInputs.GetDigitalInput(6);

    AddComponent("Climber", new Climber(climberData, climberDataInternal));
  }

  @Override
  public void CreateInput()
  {
    super.CreateInput();

    Input.CreateAxis("Horizontal", 0, XboxAxis.LEFTX, false);
    Input.CreateAxis("Vertical", 0, XboxAxis.LEFTY, true);
    Input.CreateAxis("Rotation", 0, XboxAxis.LEFT_TRIGGER, false);

    Input.SetAxisNegative("Rotation", 0, XboxAxis.RIGHT_TRIGGER, false);

    Input.SetAxisDeadzone("Horizontal", 0.15);
    Input.SetAxisDeadzone("Vertical", 0.15);
    Input.SetAxisDeadzone("Rotation", 0.15);

    Input.CreateButton("Shooter", 0, XboxButton.A);
    Input.CreateButton("Align", 0, XboxButton.B);

    Input.CreateButton("FeederToggle", 0, XboxButton.Y);

    Input.CreateButton("FeedBackward", 0, XboxButton.LB);
    Input.CreateButton("FeedForward", 0, XboxButton.X);

    Input.CreateButton("FeederUpAnalog", 1, XboxButton.LB);
    Input.CreateButton("FeederDownAnalog", 1, XboxButton.RB);

    Input.CreateButton("ClimberSequence", 0, XboxButton.START);
    Input.CreateButton("ClimberSequenceSafe", 1, XboxButton.START);

    Input.CreateButton("ClimberZeroAngle", 0, XboxButton.BACK);

    Input.CreateButton("ClimberSequenceCancel", 1, XboxButton.B);

    Input.CreateAxis("ClimberManual", 1, XboxAxis.LEFT_TRIGGER, true);
    Input.SetAxisNegative("ClimberManual", 1, XboxAxis.RIGHT_TRIGGER, false);
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
    IdleLED();

    super.ComponentLoop();

    led.Refresh();
  }

  private Color _color;
  public void IdleLED()
  {
    _color.A = Mathf.Lerp(0, 1, Math.abs(Math.sin(frc.robot.Library.FRC_3117_Tools.Math.Timer.GetCurrentTime())));
    led.SetColor(_color, 0.01);
  }
}

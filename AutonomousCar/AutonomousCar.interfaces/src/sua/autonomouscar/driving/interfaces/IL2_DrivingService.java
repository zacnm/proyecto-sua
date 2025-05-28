package sua.autonomouscar.driving.interfaces;

public interface IL2_DrivingService extends IL1_DrivingService {
	
	public static String DRIVINGSERVICE_Required_Engine = "req_e";
	public static String DRIVINGSERVICE_Required_Steering = "req_s";
	public static String DRIVINGSERVICE_Required_RearDistanceSensor = "req_rear_ds";
	public static String DRIVINGSERVICE_Required_RightDistanceSensor = "req_right_ds";
	public static String DRIVINGSERVICE_Required_LeftDistanceSensor = "req_left_ds";
		
	public void setEngine(String engine);
	public void setSteering(String steering);
	
	public void setRearDistanceSensor(String sensor);
	public void setRightDistanceSensor(String sensor);
	public void setLeftDistanceSensor(String sensor);

	public void setLateralSecurityDistance(int distance);
	public int getLateralSecurityDistance();
	
}

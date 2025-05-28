package sua.autonomouscar.driving.interfaces;

public interface IL3_DrivingService extends IL2_DrivingService {
	
	public static String DRIVINGSERVICE_Required_HumanSensors = "req_hs";
	public static String DRIVINGSERVICE_Required_RoadSensor = "req_rs";
	public static String DRIVINGSERVICE_Required_FallbackPlan = "req_fb";
	
	public void setHumanSensors(String humanSensors);
	public void setRoadSensor(String roadSensors);
	
	public void setFallbackPlan(String plan);

	public void setReferenceSpeed(int speed);
	public int getReferenceSpeed();
	
	public IL3_DrivingService performTheTakeOver();
	public IL3_DrivingService activateTheFallbackPlan();
	
}

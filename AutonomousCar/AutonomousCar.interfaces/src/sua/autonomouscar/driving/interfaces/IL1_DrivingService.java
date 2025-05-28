package sua.autonomouscar.driving.interfaces;

public interface IL1_DrivingService extends IL0_DrivingService {

	public static String DRIVINGSERVICE_Required_FrontDistanceSensor= "req_front_ds";
	public static String DRIVINGSERVICE_Required_RightLineSensor = "req_right_ls";
	public static String DRIVINGSERVICE_Required_LeftLineSensor = "req_left_ls";
	public static String DRIVINGSERVICE_Required_NotificationService = "req_ns";
	
	public void setFrontDistanceSensor(String sensor);
	public void setRightLineSensor(String sensor);
	public void setLeftLineSensor(String sensor);
	
	public void setLongitudinalSecurityDistance(int distance);
	public int getLongitudinalSecurityDistance();
	
	public void setNotificationService(String service);


}

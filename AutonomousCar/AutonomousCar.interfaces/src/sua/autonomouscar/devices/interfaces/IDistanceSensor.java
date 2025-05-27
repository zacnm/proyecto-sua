package sua.autonomouscar.devices.interfaces;

public interface IDistanceSensor {
	
	public int getDistance();

	public IDistanceSensor setDistance(int distance); // for simulation purposes only
	
	public boolean getError();
	
	public IDistanceSensor setError(boolean error); // for simulation purposes only
}

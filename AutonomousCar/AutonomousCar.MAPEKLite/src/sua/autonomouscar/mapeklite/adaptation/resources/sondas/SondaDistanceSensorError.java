package sua.autonomouscar.mapeklite.adaptation.resources.sondas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.devices.DistanceSensor;
import sua.autonomouscar.interfaces.IIdentifiable;

public class SondaDistanceSensorError extends Probe implements ActionListener {
	public static String Id = "Sonda Distance Sensor Error";
	
	HashMap<String, DistanceSensor> distanceSensors = new HashMap<String, DistanceSensor>();
	String[] directions = { "Front", "Left", "Rear", "Right" };

	public SondaDistanceSensorError(BundleContext context) {
		super(context, Id);
		
		for (String direction : directions) {
			String name = String.format("%sDistanceSensor", direction);
			DistanceSensor sensor = (DistanceSensor) OSGiUtils.getService(context, IDistanceSensor.class, String.format("(%s=%s)", IIdentifiable.ID, name));
			
			distanceSensors.put(direction, sensor);
			
			sensor.addPropertyChangeListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == DistanceSensor.ERROR) {
			DistanceSensor sensor = (DistanceSensor) e.getSource();
			this.reportMeasure(sensor.getId());
		}
	}
}

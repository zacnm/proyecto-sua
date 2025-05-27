package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import java.util.HashMap;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

import sua.autonomouscar.devices.interfaces.IDistanceSensor;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.devices.DistanceSensor;
import sua.autonomouscar.interfaces.IIdentifiable;

public class MonitorDistanceSensorError extends Monitor {
	
	public static String ID = "Monitor Sensor Fallo";

	public MonitorDistanceSensorError(BundleContext context) {
		super(context, ID);
		this.logger.debug("Starting distance sensor error monitor");
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			String sensorId = (String) measure;
			
			IKnowledgeProperty sensorErrorKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("sensor-funcionamiento");
			
			HashMap<String, Boolean> sensors = (HashMap<String, Boolean>) sensorErrorKp.getValue();
			
			DistanceSensor sensor = (DistanceSensor) OSGiUtils.getService(context, IDistanceSensor.class, String.format("(%s=%s)", IIdentifiable.ID, sensorId));
		
			boolean savedError = sensors.get(sensorId);
			boolean currentError = sensor.getError();
			
			if (savedError != currentError) {
				this.logger.debug(String.format("%s error changed: %s -> %s", sensorId, savedError, currentError));
				
				sensors.put(sensorId, sensor.getError());
				sensorErrorKp.setValue(new HashMap<String, Boolean>(sensors));
			}
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

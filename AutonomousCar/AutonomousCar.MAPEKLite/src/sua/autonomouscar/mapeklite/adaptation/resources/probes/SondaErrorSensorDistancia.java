package sua.autonomouscar.mapeklite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.mapeklite.adaptation.resources.enums.EDireccion;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaErrorSensorDistancia extends Probe {
	
	public static String ID = "Sonda Error Sensor Distancia";

	public SondaErrorSensorDistancia(BundleContext context) {
		super(context, ID);
	}
	
	public void reportSensorError(EDireccion sensorDirection) {
		this.reportMeasure(sensorDirection);
	}

}

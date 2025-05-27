package sua.autonomouscar.mapeklite.adaptation.resources.sondas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.devices.RoadSensor;
import sua.autonomouscar.interfaces.ERoadType;

public class SondaTipoVia extends Probe implements ActionListener {
	
	public static String ID = "Sonda Tipo Via";
	RoadSensor roadSensor = null;

	public SondaTipoVia(BundleContext context) {
		super(context, ID);
		this.roadSensor = (RoadSensor) OSGiUtils.getService(context, IRoadSensor.class);
		
		this.roadSensor.addPropertyChangeListener(this);
	}

	public void report() {
		this.reportMeasure(this.roadSensor.getRoadType());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == RoadSensor.ROAD_TYPE) {
			ERoadType roadType = this.roadSensor.getRoadType();
			this.reportMeasure(roadType);
		}
	}
}

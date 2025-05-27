package sua.autonomouscar.mapeklite.adaptation.resources.sondas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.devices.RoadSensor;
import sua.autonomouscar.interfaces.ERoadStatus;

public class SondaEstadoVia extends Probe implements ActionListener {
	
	public static String Id = "Sonda Estado Via";
	RoadSensor roadSensor = null;

	public SondaEstadoVia(BundleContext context) {
		super(context, Id);
		this.roadSensor = (RoadSensor) OSGiUtils.getService(context, IRoadSensor.class);
		this.logger.debug("Starting road state probe");
		
		this.roadSensor.addPropertyChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == RoadSensor.ROAD_STATUS) {
			ERoadStatus roadStatus = this.roadSensor.getRoadStatus();
			this.reportMeasure(roadStatus);
		}
	}
		
}

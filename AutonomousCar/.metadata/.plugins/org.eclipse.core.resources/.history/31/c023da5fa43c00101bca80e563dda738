package sua.autonomouscar.mapeklite.adaptation.resources.sondas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.interfaces.IIdentifiable;
import sua.autonomouscar.devices.interfaces.ISeatSensor;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.devices.SeatSensor;

public class SondaAsientoConductor extends Probe implements ActionListener {
	
	public static String ID = "Sonda Seats";
	SeatSensor seatSensor = null;

	public SondaAsientoConductor(BundleContext context) {
		super(context, ID);
		this.seatSensor = (SeatSensor) OSGiUtils.getService(context, ISeatSensor.class, String.format("%s=%s", IIdentifiable.ID, "DriverSeatSensor"));
		this.logger.debug("Seat sensor found: " + this.seatSensor);
		
		seatSensor.addPropertyChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == SeatSensor.OCCUPIED) {
			SeatSensor seatSensor = (SeatSensor) e.getSource();
			this.reportMeasure(seatSensor.isSeatOccuppied());
		}
	}
}

package sua.autonomouscar.mapeklite.adaptation.resources.sondas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.devices.interfaces.IHandsOnWheelSensor;
import sua.autonomouscar.devices.interfaces.IHumanSensors;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.devices.HandsOnWheelSensor;
import sua.autonomouscar.infrastructure.devices.HumanSensors;
import sua.autonomouscar.infrastructure.devices.RoadSensor;
import sua.autonomouscar.interfaces.ERoadType;

public class SondaHandsOnWheel extends Probe implements ActionListener {
	
	public static String ID = "Sonda Hands on Wheel";
	HandsOnWheelSensor howSensor = null;

	public SondaHandsOnWheel(BundleContext context) {
		super(context, ID);
		this.howSensor = (HandsOnWheelSensor) OSGiUtils.getService(context, IHandsOnWheelSensor.class);
		
		this.howSensor.addPropertyChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == HandsOnWheelSensor.HANDS_ON_WHEEL) {
			this.reportMeasure(this.howSensor.areTheHandsOnTheSteeringWheel());
	}
}

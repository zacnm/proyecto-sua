package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;

public class MonitorHandsOnWheel extends Monitor {
	
	public static String ID = "Monitor Estado Vía";

	public MonitorHandsOnWheel(BundleContext context) {
		super(context, ID);
		this.logger.debug("Starting hands on wheel monitor");
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			boolean handsOnWheel = (boolean) measure;
			
			IKnowledgeProperty handsOnWheelKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("hands-on-wheel");
			
			handsOnWheelKp.setValue(handsOnWheel);
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

public class MonitorAsientoConductor extends Monitor {
	
	public static String ID = "Monitor Asiento Conductor";

	public MonitorAsientoConductor(BundleContext context) {
		super(context, ID);
		this.logger.debug("Starting drivers seat monitor");
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			boolean driversSeatOccupied = (boolean) measure;
			
			IKnowledgeProperty asientoConductorKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("asiento-conductor-ocupado");
			
			asientoConductorKp.setValue(driversSeatOccupied);
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

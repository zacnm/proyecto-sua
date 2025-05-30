package sua.autonomouscar.mapeklite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;

import sua.autonomouscar.interfaces.ERoadStatus;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaEstadoVia extends Probe {
	
	public static String ID = "Sonda Estado Via";

	public SondaEstadoVia(BundleContext context) {
		super(context, ID);
	}
	
	public void reportRoadStatus(ERoadStatus roadStatus) {
		this.reportMeasure(roadStatus);
	}

}

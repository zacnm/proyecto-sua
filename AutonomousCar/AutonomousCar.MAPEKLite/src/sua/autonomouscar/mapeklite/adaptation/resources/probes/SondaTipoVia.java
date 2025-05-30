package sua.autonomouscar.mapeklite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import sua.autonomouscar.interfaces.ERoadType;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaTipoVia extends Probe {
	
	public static String ID = "Sonda Tipo Via";

	public SondaTipoVia(BundleContext context) {
		super(context, ID);
	}
	
	public void reportRoadType(ERoadType roadType) {
		this.reportMeasure(roadType);
	}

}

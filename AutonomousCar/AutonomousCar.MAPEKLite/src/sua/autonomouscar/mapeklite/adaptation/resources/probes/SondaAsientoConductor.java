package sua.autonomouscar.mapeklite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaAsientoConductor extends Probe {
	
	public static String ID = "Sonda Asiento Conductor";

	public SondaAsientoConductor(BundleContext context) {
		super(context, ID);
	}

	public void reportAsientoConductorOcupado(boolean asientoConductorOcupado) {
		this.reportMeasure(asientoConductorOcupado);
	}
}

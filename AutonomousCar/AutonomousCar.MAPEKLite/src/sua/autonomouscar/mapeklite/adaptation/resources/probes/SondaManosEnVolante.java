package sua.autonomouscar.mapeklite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;

public class SondaManosEnVolante extends Probe {
	
	public static String ID = "Sonda Manos en Volante";

	public SondaManosEnVolante(BundleContext context) {
		super(context, ID);
	}

	public void reportManosEnVolante(boolean manosEnVolante) {
		this.reportMeasure(manosEnVolante);
	}
}

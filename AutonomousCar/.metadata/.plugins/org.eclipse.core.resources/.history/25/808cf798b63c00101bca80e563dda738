package sua.autonomouscar.mapeklite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;

public class SondaNivelAutonomia extends Probe {
	
	public static String ID = "Sonda Nivel Autonomia";

	public SondaNivelAutonomia(BundleContext context) {
		super(context, ID);
	}
	
	public void reportNivelAutonomia(ENivelAutonomia nivelAutonomia) {
		this.reportMeasure(nivelAutonomia);
	}

}

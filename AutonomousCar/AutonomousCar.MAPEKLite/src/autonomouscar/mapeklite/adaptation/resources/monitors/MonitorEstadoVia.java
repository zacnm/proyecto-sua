package autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import autonomouscar.mapeklite.adaptation.resources.enums.FuncionConduccion;
import autonomouscar.mapeklite.adaptation.resources.enums.NivelAutonomia;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.driving.interfaces.IL3_TrafficJamChauffer;
import sua.autonomouscar.interfaces.ERoadStatus;

public class MonitorEstadoVia extends Monitor {
	
	public static String ID = "Monitor Estado Vía";

	public MonitorEstadoVia(BundleContext context) {
		super(context, ID);
		this.logger.debug("Starting road state monitor");
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			ERoadStatus roadStatus = (ERoadStatus) measure;
			
			IKnowledgeProperty nivelAutonomiaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("nivel-autonomia");
			IKnowledgeProperty funcionConduccionKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion");
			IKnowledgeProperty estadoViaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("estado-via");
			
			NivelAutonomia nivelAutonomia = (NivelAutonomia) nivelAutonomiaKp.getValue();
			FuncionConduccion funcionConduccion = (FuncionConduccion) funcionConduccionKp.getValue();
			
			this.logger.debug(String.format("NM: %s, FC: %s, EV: %s", nivelAutonomia, funcionConduccion, roadStatus));
			
			if (nivelAutonomiaKp.getValue() == NivelAutonomia.AutomatizacionParcial && funcionConduccionKp.getValue() == FuncionConduccion.L3_HighwayChauffer &&
					roadStatus != ERoadStatus.FLUID) {
				this.logger.debug("Road state monitor detects traffic jam");
				
				estadoViaKp.setValue(roadStatus);
				funcionConduccionKp.setValue(FuncionConduccion.L3_TrafficJamChauffer);
				
			} else if (nivelAutonomiaKp.getValue() == NivelAutonomia.AutomatizacionParcial && funcionConduccionKp.getValue() == FuncionConduccion.L3_TrafficJamChauffer &&
					roadStatus == ERoadStatus.FLUID) {
				this.logger.debug("Road state monitor detects traffic jam ending");

				estadoViaKp.setValue(ERoadStatus.FLUID);
				funcionConduccionKp.setValue(FuncionConduccion.L3_HighwayChauffer);
				
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

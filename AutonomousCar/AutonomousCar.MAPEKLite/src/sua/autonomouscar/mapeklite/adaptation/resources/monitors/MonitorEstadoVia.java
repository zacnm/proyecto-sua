package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;

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
			
			ENivelAutonomia nivelAutonomia = (ENivelAutonomia) nivelAutonomiaKp.getValue();
			EFuncionConduccion funcionConduccion = (EFuncionConduccion) funcionConduccionKp.getValue();
			
			this.logger.debug(String.format("NM: %s, FC: %s, EV: %s", nivelAutonomia, funcionConduccion, roadStatus));
			
			estadoViaKp.setValue(roadStatus);
			
			// ADS_L3-2
			if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_HighwayChauffer &&
					roadStatus != ERoadStatus.FLUID) {
				this.logger.debug("Road status monitor detects traffic jam");
				
				funcionConduccionKp.setValue(EFuncionConduccion.L3_TrafficJamChauffer);
				
			// ADS_L3-4
			} else if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_TrafficJamChauffer &&
					roadStatus == ERoadStatus.FLUID) {
				this.logger.debug("Road status monitor detects traffic jam ending");

				funcionConduccionKp.setValue(EFuncionConduccion.L3_HighwayChauffer);
				
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

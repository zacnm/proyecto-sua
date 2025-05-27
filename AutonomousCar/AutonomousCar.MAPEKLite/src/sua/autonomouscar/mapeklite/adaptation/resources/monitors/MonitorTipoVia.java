package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;

public class MonitorTipoVia extends Monitor {
	
	public static String ID = "Monitor Tipo Via";

	public MonitorTipoVia(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			ERoadType roadType = (ERoadType) measure;
			
			IKnowledgeProperty nivelAutonomiaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("nivel-autonomia");
			IKnowledgeProperty funcionConduccionKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion");
			IKnowledgeProperty estadoViaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("estado-via");
			IKnowledgeProperty tipoViaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("tipo-via");
			
			ENivelAutonomia nivelAutonomia = (ENivelAutonomia) nivelAutonomiaKp.getValue();
			EFuncionConduccion funcionConduccion = (EFuncionConduccion) funcionConduccionKp.getValue();
			ERoadStatus estadoVia = (ERoadStatus) estadoViaKp.getValue();
			
			this.logger.debug(String.format("NM: %s, FC: %s, TV: %s", nivelAutonomia, funcionConduccion, roadType));
			
			tipoViaKp.setValue(roadType);
			
			// ADS_L3-1
			if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && roadType == ERoadType.STD_ROAD) {
				this.logger.debug("Road status monitor detects standard road, lowering autonomy to 2");
				
				nivelAutonomiaKp.setValue(ENivelAutonomia.L2_AutomatizacionParcial);
				funcionConduccionKp.setValue(EFuncionConduccion.None);
				
			} else if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && roadType == ERoadType.OFF_ROAD) {
				this.logger.debug("Road status monitor detects off road, lowering autonomy to 1");

				nivelAutonomiaKp.setValue(ENivelAutonomia.L1_ConduccionAsistida);
				funcionConduccionKp.setValue(EFuncionConduccion.None);
				
			// ADS_L3-3
			} else if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_HighwayChauffer &&
					estadoVia == ERoadStatus.FLUID && roadType == ERoadType.CITY) {
				this.logger.debug("Road status monitor detects transition from highway to city");

				funcionConduccionKp.setValue(EFuncionConduccion.L3_CityChauffer);
				
			// ADS_L3-5
			} else if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_TrafficJamChauffer &&
					roadType == ERoadType.CITY) {
				this.logger.debug("Road status monitor detects traffic ending in city");

				funcionConduccionKp.setValue(EFuncionConduccion.L3_CityChauffer);
				
			// ADS_L3-6
			} else if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_CityChauffer &&
					estadoVia == ERoadStatus.FLUID && roadType == ERoadType.HIGHWAY) {
				this.logger.debug("Road status monitor detects transition from city to highway");

				funcionConduccionKp.setValue(EFuncionConduccion.L3_HighwayChauffer);
			} else if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_CityChauffer &&
					estadoVia != ERoadStatus.FLUID && roadType == ERoadType.HIGHWAY) {
				this.logger.debug("Road status monitor detects transition from city to traffic");

				funcionConduccionKp.setValue(EFuncionConduccion.L3_TrafficJamChauffer);
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

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
			
			IKnowledgeProperty kp_FuncionConduccionActual = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
			IKnowledgeProperty kp_FuncionConduccionAnterior = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ANTERIOR);
			IKnowledgeProperty kp_EstadoVia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ESTADO_VIA);
			IKnowledgeProperty kp_TipoVia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.TIPO_VIA);
			
			EFuncionConduccion funcionConduccionActual = (EFuncionConduccion) kp_FuncionConduccionActual.getValue();
			ERoadStatus estadoVia = (ERoadStatus) kp_EstadoVia.getValue();
			
						
			// ADS_L3-3
			if (funcionConduccionActual == EFuncionConduccion.L3_HighwayChauffer && estadoVia == ERoadStatus.FLUID && roadType == ERoadType.CITY) {
				this.logger.debug("Road status monitor detects transition from highway to city");

				kp_FuncionConduccionAnterior.setValue(funcionConduccionActual);
				kp_FuncionConduccionActual.setValue(EFuncionConduccion.L3_CityChauffer);
				
			// ADS_L3-5
			} else if (funcionConduccionActual == EFuncionConduccion.L3_TrafficJamChauffer && roadType == ERoadType.CITY) {
				this.logger.debug("Road status monitor detects transition from traffic to city");

				kp_FuncionConduccionAnterior.setValue(funcionConduccionActual);
				kp_FuncionConduccionActual.setValue(EFuncionConduccion.L3_CityChauffer);
				
			// ADS_L3-6
			} else if (funcionConduccionActual == EFuncionConduccion.L3_CityChauffer && estadoVia == ERoadStatus.FLUID && roadType == ERoadType.HIGHWAY) {
				this.logger.debug("Road status monitor detects transition from city to highway");

				kp_FuncionConduccionAnterior.setValue(funcionConduccionActual);
				kp_FuncionConduccionActual.setValue(EFuncionConduccion.L3_HighwayChauffer);
				
			} else if (funcionConduccionActual == EFuncionConduccion.L3_CityChauffer && estadoVia != ERoadStatus.FLUID && roadType == ERoadType.HIGHWAY) {
				this.logger.debug("Road status monitor detects transition from city to traffic");

				kp_FuncionConduccionAnterior.setValue(funcionConduccionActual);
				kp_FuncionConduccionActual.setValue(EFuncionConduccion.L3_TrafficJamChauffer);
			}

			kp_TipoVia.setValue(roadType);
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

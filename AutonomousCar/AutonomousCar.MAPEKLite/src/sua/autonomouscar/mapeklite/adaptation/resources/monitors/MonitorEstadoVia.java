package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class MonitorEstadoVia extends Monitor {
	
	public static String ID = "Monitor Estado Via";

	public MonitorEstadoVia(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			ERoadStatus roadStatus = (ERoadStatus) measure;
			
			IKnowledgeProperty kp_EstadoVia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ESTADO_VIA);
			IKnowledgeProperty kp_FuncionConduccionActual = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
			IKnowledgeProperty kp_FuncionConduccionAnterior = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ANTERIOR);
			
			ERoadStatus estadoVia = (ERoadStatus) kp_EstadoVia.getValue();
			EFuncionConduccion funcionConduccionActual = (EFuncionConduccion) kp_FuncionConduccionActual.getValue();
			
			if (estadoVia != roadStatus) {
				
				// ADS_L3-2
				if (roadStatus != ERoadStatus.FLUID && funcionConduccionActual == EFuncionConduccion.L3_HighwayChauffer) {
					kp_FuncionConduccionAnterior.setValue(funcionConduccionActual);
					kp_FuncionConduccionActual.setValue(EFuncionConduccion.L3_TrafficJamChauffer);
				}
				
				// ADS_L3-4
				if (roadStatus == ERoadStatus.FLUID && funcionConduccionActual == EFuncionConduccion.L3_TrafficJamChauffer) {
					kp_FuncionConduccionAnterior.setValue(funcionConduccionActual);
					kp_FuncionConduccionActual.setValue(EFuncionConduccion.L3_HighwayChauffer);
				}
				
				kp_EstadoVia.setValue(roadStatus);
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

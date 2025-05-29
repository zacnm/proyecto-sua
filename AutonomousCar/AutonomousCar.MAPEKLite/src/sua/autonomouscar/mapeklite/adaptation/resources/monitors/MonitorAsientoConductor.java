package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class MonitorAsientoConductor extends Monitor {
	
	public static String ID = "Monitor Asiento Conductor";

	public MonitorAsientoConductor(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			boolean asientoConductorOcupado = (boolean) measure;
			
			IKnowledgeProperty kp_AsientoConductor = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ASIENTO_CONDUCTOR_OCUPADO);
			IKnowledgeProperty kp_FuncionConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
			IKnowledgeProperty kp_VibracionAsientoConductor = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.VIBRACION_ASIENTO_CONDUCTOR);
			
			EFuncionConduccion funcionConduccion = (EFuncionConduccion) kp_FuncionConduccion.getValue();
			
			if ((boolean) kp_AsientoConductor.getValue() != asientoConductorOcupado) {
				
				kp_AsientoConductor.setValue(asientoConductorOcupado);
				
				if (funcionConduccion == EFuncionConduccion.L3_CityChauffer ||
						funcionConduccion == EFuncionConduccion.L3_HighwayChauffer ||
						funcionConduccion == EFuncionConduccion.L3_TrafficJamChauffer) {
					
					kp_VibracionAsientoConductor.setValue(asientoConductorOcupado);
				}				
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

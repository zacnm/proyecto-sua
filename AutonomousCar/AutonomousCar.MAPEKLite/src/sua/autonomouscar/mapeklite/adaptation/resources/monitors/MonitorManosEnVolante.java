package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class MonitorManosEnVolante extends Monitor {
	
	public static String ID = "Monitor Manos en Volante";

	public MonitorManosEnVolante(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			boolean manosEnVolante = (boolean) measure;
			
			IKnowledgeProperty kp_ManosEnVolante = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.MANOS_EN_VOLANTE);
			IKnowledgeProperty kp_AsientoConductor = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ASIENTO_CONDUCTOR_OCUPADO);
			IKnowledgeProperty kp_FuncionConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
			IKnowledgeProperty kp_VibracionVolante = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.VIBRACION_VOLANTE);
			IKnowledgeProperty kp_VibracionAsientoConductor = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.VIBRACION_ASIENTO_CONDUCTOR);
			
			boolean asientoConductorOcupado = (boolean) kp_AsientoConductor.getValue();
			EFuncionConduccion funcionConduccion = (EFuncionConduccion) kp_FuncionConduccion.getValue();
			
			if ((boolean) kp_ManosEnVolante.getValue() != manosEnVolante) {
				
				kp_ManosEnVolante.setValue(manosEnVolante);
				
				if (funcionConduccion == EFuncionConduccion.L3_CityChauffer ||
						funcionConduccion == EFuncionConduccion.L3_HighwayChauffer ||
						funcionConduccion == EFuncionConduccion.L3_TrafficJamChauffer) {
					
					kp_VibracionVolante.setValue(manosEnVolante);
					
					if (asientoConductorOcupado) {
						kp_VibracionAsientoConductor.setValue(!manosEnVolante);
					}
				}				
			}
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

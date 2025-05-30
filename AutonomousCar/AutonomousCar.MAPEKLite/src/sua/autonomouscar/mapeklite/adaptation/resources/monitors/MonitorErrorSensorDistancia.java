package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import java.util.HashMap;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

import sua.autonomouscar.mapeklite.adaptation.resources.enums.EDireccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class MonitorErrorSensorDistancia extends Monitor {
	
	public static String ID = "Monitor Error Sensor Distancia";

	public MonitorErrorSensorDistancia(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			EDireccion direccion = (EDireccion) measure;
			
			IKnowledgeProperty kp_ErrorSensorDistanciaActual = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ACTUAL);
			IKnowledgeProperty kp_ErrorSensorDistanciaAnterior = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ANTERIOR);
			
			HashMap<EDireccion, Boolean> erroresSensorDistanciaActual = (HashMap<EDireccion, Boolean>) kp_ErrorSensorDistanciaActual.getValue();
			
			
			kp_ErrorSensorDistanciaAnterior.setValue(new HashMap<EDireccion, Boolean>(erroresSensorDistanciaActual));
			
			boolean errorActual = erroresSensorDistanciaActual.get(direccion);
			erroresSensorDistanciaActual.put(direccion, !errorActual);
			
			kp_ErrorSensorDistanciaActual.setValue(new HashMap<EDireccion, Boolean>(erroresSensorDistanciaActual));
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

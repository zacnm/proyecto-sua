package autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import autonomouscar.mapeklite.adaptation.resources.enums.FuncionConduccion;
import autonomouscar.mapeklite.adaptation.resources.enums.NivelAutonomia;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.interfaces.ERoadStatus;

public class MonitorTipoVia extends Monitor {
	
	public static String ID = "Monitor Tipo Via";

	public MonitorTipoVia(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		this.logger.debug(String.format("Received measure: %s", measure.toString()));
		
		try {
			ERoadStatus roadStatus = (ERoadStatus) measure;
			
			IKnowledgeProperty nivelAutonomiaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("nivel-autonomia");
			IKnowledgeProperty funcionConduccionKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion-actual");
			IKnowledgeProperty tipoViaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("tipo-via");
			
			if ( nivelAutonomiaKp.getValue() == NivelAutonomia.AutomatizacionParcial && funcionConduccionKp.getValue() == FuncionConduccion.L3_HighwayChauffer &&
					tipoViaKp.getValue() != roadStatus) {
				this.logger.debug(String.format("Updating Knowledge Property %s TO %s", tipoViaKp.getId(), roadStatus));
				
				tipoViaKp.setValue(roadStatus);
			}
			
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

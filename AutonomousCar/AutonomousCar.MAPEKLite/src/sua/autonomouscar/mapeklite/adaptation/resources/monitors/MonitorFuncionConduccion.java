package sua.autonomouscar.mapeklite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;

public class MonitorFuncionConduccion extends Monitor {
	
	public static String ID = "Monitor Funcion Conduccion";

	public MonitorFuncionConduccion(BundleContext context) {
		super(context, ID);
	}

	@Override
	public IMonitor report(Object measure) {
		EFuncionConduccion funcionConduccion = (EFuncionConduccion) measure;
		
		try {

			IKnowledgeProperty funcionConduccionKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion");
			
			if (funcionConduccion != funcionConduccionKp.getValue()) {
				this.logger.debug(String.format("Activated driving service: %s", funcionConduccion));
				funcionConduccionKp.setValue(funcionConduccion);
			}
			 
		} catch (Exception e) {
			return this;
		}
		
		return this;
	}

}

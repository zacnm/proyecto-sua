package autonomouscar.mapeklite.adaptation;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import autonomouscar.mapeklite.adaptation.resources.enums.FuncionConduccion;
import autonomouscar.mapeklite.adaptation.resources.enums.NivelAutonomia;
import autonomouscar.mapeklite.adaptation.resources.monitors.MonitorEstadoVia;
import autonomouscar.mapeklite.adaptation.resources.rules.HighwayToTrafficJamTransitionAdaptationRule;
import autonomouscar.mapeklite.adaptation.resources.sondas.SondaEstadoVia;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		BasicMAPEKLiteLoopHelper.BUNDLECONTEXT = bundleContext;
		BasicMAPEKLiteLoopHelper.REFERENCE_MODEL = "AutonomousCar-SUA";
		
		// Declaring the the initial system configuration
		
		IComponentsSystemConfiguration initialSystemConfiguration = 
				SystemConfigurationHelper.createSystemConfiguration("InitialConfiguration");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.RoadSensor", "1.0.0");
		
		BasicMAPEKLiteLoopHelper.INITIAL_SYSTEMCONFIGURATION = initialSystemConfiguration;

		// STARTING THE MAPE-K LOOP
		
		BasicMAPEKLiteLoopHelper.MODELSREPOSITORY_FOLDER = System.getProperty("modelsrepository.folder");
		BasicMAPEKLiteLoopHelper.ADAPTATIONREPORTS_FOLDER = System.getProperty("adaptationreports.folder");
		
		BasicMAPEKLiteLoopHelper.startLoopModules();
		
		// ADAPTATION PROPERTIES
		IKnowledgeProperty estadoViaKp = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("estado-via");
		IKnowledgeProperty tipoViaKp = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("tipo-via");
		IKnowledgeProperty nivelAutonomiaKp = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("nivel-autonomia");
		IKnowledgeProperty funcionConduccionKp = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("funcion-conduccion");
		
		estadoViaKp.setValue(ERoadStatus.FLUID);
		tipoViaKp.setValue(ERoadType.HIGHWAY);
		nivelAutonomiaKp.setValue(NivelAutonomia.AutomatizacionParcial);
		funcionConduccionKp.setValue(FuncionConduccion.L3_HighwayChauffer);

		// ADAPTATION RULES
		//BasicMAPEKLiteLoopHelper.deployAdaptationRule(new HighwayToTrafficJamTransitionAdaptationRule(bundleContext));

		// MONITORS
		IAdaptiveReadyComponent estadoViaMonitorARC =  BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorEstadoVia(bundleContext));
		// IAdaptiveReadyComponent estadoViaMonitorARC =  BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorTipoVia(bundleContext));

		// PROBES
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaEstadoVia(bundleContext), estadoViaMonitorARC);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}

package sua.autonomouscar.mapeklite.adaptation;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorEstadoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorTipoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.CityTransitionAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.HighwayTransitionAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TrafficJamTransitionAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.sondas.SondaEstadoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.sondas.SondaFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.sondas.SondaTipoVia;

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
		
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.Engine", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.FrontDistanceSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.HumanSensors", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.LeftDistanceSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.LeftLineSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.LIDAR", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.RearDistanceSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.RightDistanceSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.RightLineSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.RoadSensor", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.Speedometer", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "device.Steering", "1.0.0");

//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.FallbackPlan.Emergency", "1.0.0");
//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.L0.ManualDriving", "1.0.0");
//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.L1.AssistedDriving", "1.0.0");
//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.L2.AdaptiveCruiseControl", "1.0.0");
//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.L3.CityChauffer", "1.0.0");
//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.L3.HighwayChauffer", "1.0.0");
//		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
//		
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.DashboardDisplay", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.DashboardIcon", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.DrivingDisplay", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.NotificationService", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.Seats", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.Speakers", "1.0.0");
		SystemConfigurationHelper.addComponent(initialSystemConfiguration, "interaction.SteeringWheel", "1.0.0");
		
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
		nivelAutonomiaKp.setValue(ENivelAutonomia.L3_AutomatizacionCondicional);
		funcionConduccionKp.setValue(EFuncionConduccion.L0_ManualDriving);

		// ADAPTATION RULES
		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TrafficJamTransitionAdaptationRule(bundleContext));
		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new HighwayTransitionAdaptationRule(bundleContext));
		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new CityTransitionAdaptationRule(bundleContext));

		// MONITORS
		IAdaptiveReadyComponent estadoViaMonitor =  BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorEstadoVia(bundleContext));
		IAdaptiveReadyComponent tipoViaMonitor =  BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorTipoVia(bundleContext));
		IAdaptiveReadyComponent funcionConduccionMonitor =  BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorFuncionConduccion(bundleContext));

		// PROBES
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaEstadoVia(bundleContext), estadoViaMonitor);
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaTipoVia(bundleContext), tipoViaMonitor);
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaFuncionConduccion(bundleContext), funcionConduccionMonitor);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}

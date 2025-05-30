package sua.autonomouscar.mapek.lite.adaptation.starter;

import java.util.HashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;

import sua.autonomouscar.driving.l3.highwaychauffer.L3_HighwayChaufferARC;
import sua.autonomouscar.driving.parkintheroadshoulderfallbackplan.ParkInTheRoadShoulderFallbackPlanARC;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.AuditorySoundARC;
import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EDireccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorAsientoConductor;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorErrorSensorDistancia;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorEstadoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorManosEnVolante;
import sua.autonomouscar.mapeklite.adaptation.resources.monitors.MonitorTipoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.probes.SondaAsientoConductor;
import sua.autonomouscar.mapeklite.adaptation.resources.probes.SondaErrorSensorDistancia;
import sua.autonomouscar.mapeklite.adaptation.resources.probes.SondaEstadoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.probes.SondaManosEnVolante;
import sua.autonomouscar.mapeklite.adaptation.resources.probes.SondaTipoVia;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.DriverSeatHapticVibrationAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.ErrorDistanceSensorAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.SteeringWheelHapticVibrationAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TransitionCityToHighwayAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TransitionCityToTrafficJamAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TransitionHighwayToCityAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TransitionHighwayToTrafficJamAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TransitionTrafficJamToCityAdaptationRule;
import sua.autonomouscar.mapeklite.adaptation.resources.rules.TransitionTrafficJamToHighwayAdaptationRule;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		BasicMAPEKLiteLoopHelper.BUNDLECONTEXT = bundleContext;
		BasicMAPEKLiteLoopHelper.REFERENCE_MODEL = "AutonomousCar";

		// ... adding the initial system configuration
		IComponentsSystemConfiguration theInitialSystemConfiguration = 
				SystemConfigurationHelper.createSystemConfiguration("InititalConfiguration");
		SystemConfigurationHelper.addComponent(theInitialSystemConfiguration, "device.RoadSensor", "1.0.0");
		BasicMAPEKLiteLoopHelper.INITIAL_SYSTEMCONFIGURATION = theInitialSystemConfiguration;

		BasicMAPEKLiteLoopHelper.MODELSREPOSITORY_FOLDER = System.getProperty("modelsrepository.folder");
		BasicMAPEKLiteLoopHelper.ADAPTATIONREPORTS_FOLDER = System.getProperty("adaptationreports.folder");
		
		// STARTING THE MAPE-K LOOP
		BasicMAPEKLiteLoopHelper.startLoopModules();
		
		BasicMAPEKLiteLoopHelper.addInitialSelfConfigurationCapabilities(createInitialSystemConfiguration());
		
		// ADAPTATION PROPERTIES
		IKnowledgeProperty kp_tipoVia = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.TIPO_VIA);
		kp_tipoVia.setValue(ERoadType.HIGHWAY);
		
		IKnowledgeProperty kp_estadoVia = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.ESTADO_VIA);
		kp_estadoVia.setValue(ERoadStatus.FLUID);

		IKnowledgeProperty kp_funcionConduccionActual = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
		kp_funcionConduccionActual.setValue(EFuncionConduccion.L3_HighwayChauffer);
		
		BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ANTERIOR);

		IKnowledgeProperty kp_manosEnVolante = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.MANOS_EN_VOLANTE);
		kp_manosEnVolante.setValue(true);

		IKnowledgeProperty kp_AsientoConductor = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.ASIENTO_CONDUCTOR_OCUPADO);
		kp_AsientoConductor.setValue(true);

		IKnowledgeProperty kp_vibracionVolante = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.VIBRACION_VOLANTE);
		kp_vibracionVolante.setValue(true);

		IKnowledgeProperty kp_vibracionAsientoConductor = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.VIBRACION_ASIENTO_CONDUCTOR);
		kp_vibracionAsientoConductor.setValue(false);
		
		IKnowledgeProperty kp_errorSensoresDistancia = BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ACTUAL);
		kp_errorSensoresDistancia.setValue(new HashMap<EDireccion, Boolean>() {{
			put(EDireccion.FRONT, false);
			put(EDireccion.RIGHT, false);
			put(EDireccion.LEFT, false);
			put(EDireccion.REAR, false);
		}});
		
		BasicMAPEKLiteLoopHelper.createKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ANTERIOR);
		
		
		// ADAPTATION RULES
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TransitionHighwayToTrafficJamAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TransitionHighwayToCityAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TransitionCityToHighwayAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TransitionCityToTrafficJamAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TransitionTrafficJamToHighwayAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new TransitionTrafficJamToCityAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new SteeringWheelHapticVibrationAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new DriverSeatHapticVibrationAdaptationRule(bundleContext));
 		BasicMAPEKLiteLoopHelper.deployAdaptationRule(new ErrorDistanceSensorAdaptationRule(bundleContext));
 		
		// MONITORS
		IAdaptiveReadyComponent monitorTipoVia = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorTipoVia(bundleContext));
		IAdaptiveReadyComponent monitorEstadoVia = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorEstadoVia(bundleContext));
		IAdaptiveReadyComponent monitorManosEnVolante = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorManosEnVolante(bundleContext));
		IAdaptiveReadyComponent monitorAsientoConductor = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorAsientoConductor(bundleContext));
		IAdaptiveReadyComponent monitorErrorSensorDistancia = BasicMAPEKLiteLoopHelper.deployMonitor(new MonitorErrorSensorDistancia(bundleContext));

		// PROBES
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaTipoVia(bundleContext), monitorTipoVia);
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaEstadoVia(bundleContext), monitorEstadoVia);
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaManosEnVolante(bundleContext), monitorManosEnVolante);
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaAsientoConductor(bundleContext), monitorAsientoConductor);
		BasicMAPEKLiteLoopHelper.deployProbe(new SondaErrorSensorDistancia(bundleContext), monitorErrorSensorDistancia);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	protected IRuleComponentsSystemConfiguration createInitialSystemConfiguration() {
		IRuleComponentsSystemConfiguration initialSystemConfiguration = 
				SystemConfigurationHelper.createPartialSystemConfiguration("InitialConfiguration_" + ITimeStamped.getCurrentTimeStamp());

		// ADD COMPONENTS
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.Engine", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.Steering", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.RoadSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.FrontDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.LeftDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.RearDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.RightDistanceSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.LeftLineSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.RightLineSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "device.HumanSensors", "1.0.0");
		
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "driving.L3.HighwayChauffer", "1.0.0");
		
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "interaction.NotificationService", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "interaction.Speakers.AuditoryBeep", "1.0.0");
		SystemConfigurationHelper.componentToAdd(initialSystemConfiguration, "interaction.SteeringWheel", "1.0.0");
		
		// ADD BINDINGS
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.REQUIRED_SERVICE,
				"interaction.Speakers.AuditoryBeep", "1.0.0", AuditorySoundARC.PROVIDED_MECHANISM);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.REQUIRED_SERVICE,
				"interaction.SteeringWheel", "1.0.0", HapticVibrationARC.PROVIDED_MECHANISM);
		
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);

		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);

		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_ROADSENSOR,
				"device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_FRONTDISTANCESENSOR,
				"device.FrontDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_LEFTDISTANCESENSOR,
				"device.LeftDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_REARDISTANCESENSOR,
				"device.RearDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_RIGHTDISTANCESENSOR,
				"device.RightDistanceSensor", "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_HUMANSENSORS,
				"device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);
		
		SystemConfigurationHelper.bindingToAdd(initialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_HighwayChaufferARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0", ParkInTheRoadShoulderFallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		
		return initialSystemConfiguration;
	}
	
}

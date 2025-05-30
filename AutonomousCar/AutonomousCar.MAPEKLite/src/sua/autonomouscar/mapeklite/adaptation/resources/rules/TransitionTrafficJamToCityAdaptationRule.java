package sua.autonomouscar.mapeklite.adaptation.resources.rules;

import java.util.HashMap;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.exceptions.analyzing.RuleException;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;

import sua.autonomouscar.driving.l3.citychauffer.L3_CityChaufferARC;
import sua.autonomouscar.driving.parkintheroadshoulderfallbackplan.ParkInTheRoadShoulderFallbackPlanARC;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EDireccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class TransitionTrafficJamToCityAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(TransitionTrafficJamToCityAdaptationRule.class);
	public static String ID = "Regla Transición Atasco a Ciudad";
	
	IKnowledgeProperty kp_FuncionConduccionActual = null;
	IKnowledgeProperty kp_FuncionConduccionAnterior = null;
	IKnowledgeProperty kp_ErrorSensorDistancia = null;

	public TransitionTrafficJamToCityAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);

		kp_FuncionConduccionActual = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
		kp_FuncionConduccionAnterior = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ANTERIOR);
		kp_ErrorSensorDistancia = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ACTUAL);
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_FuncionConduccionActual == null || 
				kp_FuncionConduccionAnterior == null ||
				kp_FuncionConduccionActual.getValue() == null ||
				kp_FuncionConduccionAnterior.getValue() == null || 
				kp_ErrorSensorDistancia == null ||
				kp_ErrorSensorDistancia.getValue() == null) {
			return false;
		}
		
		EFuncionConduccion funcionConduccionActual = (EFuncionConduccion) kp_FuncionConduccionActual.getValue();
		EFuncionConduccion funcionConduccionAnterior = (EFuncionConduccion) kp_FuncionConduccionAnterior.getValue();
		
		return funcionConduccionAnterior == EFuncionConduccion.L3_TrafficJamChauffer &&
				funcionConduccionActual == EFuncionConduccion.L3_CityChauffer;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	
		IRuleComponentsSystemConfiguration nextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		SystemConfigurationHelper.componentToRemove(nextSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
		
		SystemConfigurationHelper.componentToAdd(nextSystemConfiguration, "driving.L3.CityChauffer", "1.0.0");
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);

		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_ROADSENSOR,
				"device.RoadSensor", "1.0.0", RoadSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_FRONTDISTANCESENSOR,
				getDistanceSensorId(EDireccion.FRONT), "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_LEFTDISTANCESENSOR,
				getDistanceSensorId(EDireccion.LEFT), "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_REARDISTANCESENSOR,
				getDistanceSensorId(EDireccion.REAR), "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_RIGHTDISTANCESENSOR,
				getDistanceSensorId(EDireccion.RIGHT), "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_LEFTLINESENSOR,
				"device.LeftLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_RIGHTLINESENSOR,
				"device.RightLineSensor", "1.0.0", LineSensorARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_HUMANSENSORS,
				"device.HumanSensors", "1.0.0", HumanSensorsARC.PROVIDED_SENSOR);

		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_NOTIFICATIONSERVICE,
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.PROVIDED_SERVICE);
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_CityChaufferARC.REQUIRED_FALLBACKPLAN,
				"driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0", ParkInTheRoadShoulderFallbackPlanARC.PROVIDED_DRIVINGSERVICE);
		
		// SET PARAMETERS
		SystemConfigurationHelper.setParameter(nextSystemConfiguration, 
				"driving.L3.CityChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_REFERENCESPEED, "100");

		
		return nextSystemConfiguration;
		
	}
	
	private String getDistanceSensorId(EDireccion direccion) {
		
		boolean errorSensorDistancia = ((HashMap<EDireccion, Boolean>) kp_ErrorSensorDistancia.getValue()).get(direccion);
		
		String sensorName = null;
		
		switch (direccion) {
			case FRONT:
				sensorName =  "FrontDistanceSensor";
				break;
			case REAR:
				sensorName =  "RearDistanceSensor";
				break;
			case LEFT:
				sensorName =  "LeftDistanceSensor";
				break;
			case RIGHT:
				sensorName =  "RightDistanceSensor";
				break;
			default:
				throw new IllegalArgumentException("Dirección no válida: " + direccion);
		}
		
		return "device." + (errorSensorDistancia ? "LIDAR." : "") + sensorName;
	}

	
}

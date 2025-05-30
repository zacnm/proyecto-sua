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
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EDireccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class ErrorDistanceSensorAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(ErrorDistanceSensorAdaptationRule.class);
	public static String ID = "Regla Error Sensor Distancia";
	
	IKnowledgeProperty kp_ErrorSensorDistanciaActual = null;
	IKnowledgeProperty kp_ErrorSensorDistanciaAnterior = null;
	IKnowledgeProperty kp_FuncionConduccion = null;

	public ErrorDistanceSensorAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges(KnowledgeId.ERROR_SENSORES_DISTANCIA_ACTUAL);

		kp_ErrorSensorDistanciaActual = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ACTUAL);
		kp_ErrorSensorDistanciaAnterior = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.ERROR_SENSORES_DISTANCIA_ANTERIOR);
		kp_FuncionConduccion = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.FUNCION_CONDUCCION_ACTUAL);
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_ErrorSensorDistanciaActual == null || kp_ErrorSensorDistanciaActual.getValue() == null ||
				kp_ErrorSensorDistanciaAnterior == null || kp_ErrorSensorDistanciaAnterior.getValue() == null ||
				kp_FuncionConduccion == null || kp_FuncionConduccion.getValue() == null || 
				(EFuncionConduccion)kp_FuncionConduccion.getValue() == EFuncionConduccion.L0_ManualDriving) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	
		HashMap<EDireccion, Boolean> erroresActuales = (HashMap<EDireccion, Boolean>) kp_ErrorSensorDistanciaActual.getValue();
		HashMap<EDireccion, Boolean> erroresAnteriores = (HashMap<EDireccion, Boolean>) kp_ErrorSensorDistanciaAnterior.getValue();
		EFuncionConduccion funcionConduccion = (EFuncionConduccion) kp_FuncionConduccion.getValue();
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		for (EDireccion direccion : erroresActuales.keySet()) {
			
			boolean errorActual = erroresActuales.get(direccion);
			boolean errorAnterior = erroresAnteriores.get(direccion);
			
			if (errorActual != errorAnterior) {
				if (funcionConduccion == EFuncionConduccion.L1_AssistedDriving && direccion != EDireccion.FRONT) {
					continue;
				}
				
				if (errorActual) {
					nextSystemConfiguration = reemplazarSensorDistanciaPorLIDAR(direccion, nextSystemConfiguration);
				} else {
					nextSystemConfiguration = reemplazarLIDARPorSensorDistancia(direccion, nextSystemConfiguration);
				}
			}
		}
		
		return nextSystemConfiguration;
	}
	
	private IRuleComponentsSystemConfiguration reemplazarSensorDistanciaPorLIDAR(EDireccion direccion, IRuleComponentsSystemConfiguration configuration) {
		
		String drivingServiceId = null;
		
		switch ((EFuncionConduccion)kp_FuncionConduccion.getValue()) {
			case L3_CityChauffer:
				drivingServiceId = "driving.L3.CityChauffer";
				break;
			case L3_HighwayChauffer:
				drivingServiceId = "driving.L3.HighwayChauffer";
				break;
			case L3_TrafficJamChauffer:
				drivingServiceId = "driving.L3.TrafficJamChauffer";
				break;
			case L2_AdaptiveCruiseControl:
				drivingServiceId = "driving.L2.AdaptiveCruiseControl";
				break;
			case L2_LaneKeepingAssist:
				drivingServiceId = "driving.L2.LaneKeepingAssist";
				break;
			case L1_AssistedDriving:
				drivingServiceId = "driving.L1.AssistedDriving";
				break;
			default:
				logger.error("No se puede reemplazar el sensor de distancia por LIDAR en la función de conducción actual: " + kp_FuncionConduccion.getValue());
				return configuration;
		}
		
		String sensorReq = null;
		String sensorId = null;
		
		switch (direccion) {
			case FRONT:
				sensorReq = L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR;
				sensorId = "FrontDistanceSensor";
				break;
			case REAR:
				sensorReq = L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR;
				sensorId = "RearDistanceSensor";
				break;
			case LEFT:
				sensorReq = L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR;
				sensorId = "LeftDistanceSensor";
				break;
			case RIGHT:
				sensorReq = L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR;
				sensorId = "RightDistanceSensor";
				break;
			default:
				logger.error("Dirección no válida para reemplazar el sensor de distancia por LIDAR: " + direccion);
				return configuration;
		}
		
		SystemConfigurationHelper.componentToRemove(configuration, "device." + sensorId, "1.0.0");
		
		SystemConfigurationHelper.componentToAdd(configuration, "device.LIDAR." + sensorId, "1.0.0");

		SystemConfigurationHelper.bindingToAdd(configuration, 
				drivingServiceId, "1.0.0", sensorReq,
				"device.LIDAR." + sensorId, "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);

		

		return configuration;
	}
	
	private IRuleComponentsSystemConfiguration reemplazarLIDARPorSensorDistancia(EDireccion direccion, IRuleComponentsSystemConfiguration configuration) {
		
		String drivingServiceId = null;
		
		switch ((EFuncionConduccion)kp_FuncionConduccion.getValue()) {
			case L3_CityChauffer:
				drivingServiceId = "driving.L3.CityChauffer";
				break;
			case L3_HighwayChauffer:
				drivingServiceId = "driving.L3.HighwayChauffer";
				break;
			case L3_TrafficJamChauffer:
				drivingServiceId = "driving.L3.TrafficJamChauffer";
				break;
			case L2_AdaptiveCruiseControl:
				drivingServiceId = "driving.L2.AdaptiveCruiseControl";
				break;
			case L2_LaneKeepingAssist:
				drivingServiceId = "driving.L2.LaneKeepingAssist";
				break;
			case L1_AssistedDriving:
				drivingServiceId = "driving.L1.AssistedDriving";
				break;
			default:
				logger.error("No se puede reemplazar el sensor de distancia por LIDAR en la función de conducción actual: " + kp_FuncionConduccion.getValue());
				return configuration;
		}
		
		String sensorReq = null;
		String sensorId = null;
		
		switch (direccion) {
			case FRONT:
				sensorReq = L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR;
				sensorId = "FrontDistanceSensor";
				break;
			case REAR:
				sensorReq = L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR;
				sensorId = "RearDistanceSensor";
				break;
			case LEFT:
				sensorReq = L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR;
				sensorId = "LeftDistanceSensor";
				break;
			case RIGHT:
				sensorReq = L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR;
				sensorId = "RightDistanceSensor";
				break;
			default:
				logger.error("Dirección no válida para reemplazar el sensor de distancia por LIDAR: " + direccion);
				return configuration;
		}
		
		SystemConfigurationHelper.componentToRemove(configuration, "device.LIDAR." + sensorId, "1.0.0");

		SystemConfigurationHelper.componentToAdd(configuration, "device." + sensorId, "1.0.0");

		SystemConfigurationHelper.bindingToAdd(configuration, 
				drivingServiceId, "1.0.0", sensorReq,
				"device." + sensorId, "1.0.0", DistanceSensorARC.PROVIDED_SENSOR);
		

		return configuration;
	}

	
}

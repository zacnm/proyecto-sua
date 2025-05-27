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
import sua.autonomouscar.driving.interfaces.IL1_AssistedDriving;
import sua.autonomouscar.driving.interfaces.IL2_AdaptiveCruiseControl;
import sua.autonomouscar.driving.interfaces.IL2_DrivingService;
import sua.autonomouscar.driving.interfaces.IL2_LaneKeepingAssist;
import sua.autonomouscar.driving.interfaces.IL3_CityChauffer;
import sua.autonomouscar.driving.interfaces.IL3_HighwayChauffer;
import sua.autonomouscar.driving.interfaces.IL3_TrafficJamChauffer;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;

public class DistanceSensorErrorAdaptationRule extends AdaptationRule {
	
	public static String ID = "Regla Errors en Sensors de Distancia";
	
	IKnowledgeProperty sensorErrorKp = null;
	IKnowledgeProperty funcionConduccionKp = null;

	public DistanceSensorErrorAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("sensor-funcionamiento");
		this.setListenToKnowledgePropertyChanges("funcion-conduccion");
		
		sensorErrorKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("sensor-funcionamiento");
		funcionConduccionKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		if (sensorErrorKp == null || sensorErrorKp.getValue() == null ||
				funcionConduccionKp == null || funcionConduccionKp.getValue() == null || 
				funcionConduccionKp.getValue() == EFuncionConduccion.L0_ManualDriving) {
			return false;
		}
		
		return true;
	}

	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		HashMap<String, Boolean> sensorErrors = (HashMap<String, Boolean>) sensorErrorKp.getValue();
		EFuncionConduccion funcionConduccion = (EFuncionConduccion) funcionConduccionKp.getValue();
		
		switch (funcionConduccion) {
			case L3_HighwayChauffer:
				IL3_HighwayChauffer L3HighwayChaufferService = OSGiUtils.getService(context, IL3_HighwayChauffer.class);
				setDistanceSensors(L3HighwayChaufferService);
				break;
			case L3_TrafficJamChauffer:
				IL3_TrafficJamChauffer L3TrafficJamChaufferService = OSGiUtils.getService(context, IL3_TrafficJamChauffer.class);
				setDistanceSensors(L3TrafficJamChaufferService);
				break;
			case L3_CityChauffer:
				IL3_CityChauffer L3CityChaufferService = OSGiUtils.getService(context, IL3_CityChauffer.class);
				setDistanceSensors(L3CityChaufferService);
				break;
			case L2_LaneKeepingAssist:
				IL2_LaneKeepingAssist L2LaneKeepingAssistService = OSGiUtils.getService(context, IL2_LaneKeepingAssist.class);
				setDistanceSensors(L2LaneKeepingAssistService);
				break;
			case L2_AdaptiveCruiseControl:
				IL2_AdaptiveCruiseControl L2AdaptiveCruiseControlService = OSGiUtils.getService(context, IL2_AdaptiveCruiseControl.class);
				setDistanceSensors(L2AdaptiveCruiseControlService);
				break;
			case L1_AssistedDriving:
				IL1_AssistedDriving L1AssistedDrivingService = OSGiUtils.getService(context, IL1_AssistedDriving.class);
				String sensorId = "FrontDistanceSensor";
				boolean error = sensorErrors.get(sensorId);
				String sensor = error ? String.format("LIDAR-%s", sensorId) : sensorId;
				L1AssistedDrivingService.setFrontDistanceSensor(sensor);
			default:
				break;
		}
			
		
		return this.configuracionSistemaActivarTrafficJamChauffer();
	}
	
	private void setDistanceSensors(IL2_DrivingService drivingService) {
		HashMap<String, Boolean> sensorErrors = (HashMap<String, Boolean>) sensorErrorKp.getValue();
		
		for (String sensorId : sensorErrors.keySet()) {
			boolean error = sensorErrors.get(sensorId);
			String sensor = error ? String.format("LIDAR-%s", sensorId) : sensorId;
			
			switch (sensorId) {
				case "FrontDistanceSensor":
					drivingService.setFrontDistanceSensor(sensor);
					break;
				case "LeftistanceSensor":
					drivingService.setLeftDistanceSensor(sensor);
					break;
				case "RearDistanceSensor":
					drivingService.setRearDistanceSensor(sensor);
					break;
				case "RightDistanceSensor":
					drivingService.setRightDistanceSensor(sensor);
					break;
				default:
					break;
			}
		}
	}

	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarTrafficJamChauffer() {
		IRuleComponentsSystemConfiguration nextSystemConfiguration = 
				SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		return nextSystemConfiguration;		
		
	}
}

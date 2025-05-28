package sua.autonomouscar.driving.l3.trafficjamchauffer;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;

import sua.autonomouscar.driving.interfaces.IL3_DrivingService;

public class L3_TrafficJamChaufferARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent  {

	private L3_TrafficJamChauffer trafficJamChauffer = null;
	
	public L3_TrafficJamChaufferARC(BundleContext context) {
		super(context, context.getBundle().getSymbolicName());
	}

	@Override
	public IAdaptiveReadyComponent deploy() {
		try {
			this.trafficJamChauffer = new L3_TrafficJamChauffer(context, "L3_TrafficJamChauffer");
			this.trafficJamChauffer.registerThing();
			this.trafficJamChauffer.startDriving();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
		
		return this;
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		if (this.trafficJamChauffer != null) {
			this.trafficJamChauffer.stopDriving();
			this.trafficJamChauffer.unregisterThing();
			this.trafficJamChauffer = null;
		}
		
		return this;
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		switch (req) {
			case IL3_DrivingService.DRIVINGSERVICE_Required_HumanSensors: 
				this.trafficJamChauffer.setHumanSensors((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RoadSensor: 
				this.trafficJamChauffer.setRoadSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FallbackPlan: 
				this.trafficJamChauffer.setFallbackPlan((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Engine: 
				this.trafficJamChauffer.setEngine((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Steering: 
				this.trafficJamChauffer.setSteering((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RearDistanceSensor: 
				this.trafficJamChauffer.setRearDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightDistanceSensor: 
				this.trafficJamChauffer.setRightDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftDistanceSensor: 
				this.trafficJamChauffer.setLeftDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FrontDistanceSensor: 
				this.trafficJamChauffer.setFrontDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightLineSensor: 
				this.trafficJamChauffer.setRightLineSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftLineSensor: 
				this.trafficJamChauffer.setLeftLineSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_NotificationService: 
				this.trafficJamChauffer.setNotificationService((String)value);
				break;
			default:
				logger.error("No se ha podido realizar el BIND del servicio: " + req);
		}
		return this;
	}
	
	@Override 
	public IAdaptiveReadyComponent unbindService(String req, Object value) {
		switch(req) {
			case IL3_DrivingService.DRIVINGSERVICE_Required_HumanSensors: 
				this.trafficJamChauffer.setHumanSensors(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RoadSensor: 
				this.trafficJamChauffer.setRoadSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FallbackPlan: 
				this.trafficJamChauffer.setFallbackPlan(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Engine: 
				this.trafficJamChauffer.setEngine(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Steering: 
				this.trafficJamChauffer.setSteering(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RearDistanceSensor: 
				this.trafficJamChauffer.setRearDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightDistanceSensor: 
				this.trafficJamChauffer.setRightDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftDistanceSensor: 
				this.trafficJamChauffer.setLeftDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FrontDistanceSensor: 
				this.trafficJamChauffer.setFrontDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightLineSensor: 
				this.trafficJamChauffer.setRightLineSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftLineSensor: 
				this.trafficJamChauffer.setLeftLineSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_NotificationService: 
				this.trafficJamChauffer.setNotificationService(null);
				break;
			default:
				logger.error("No se ha podido realizar el UNBIND del servicio: " + req);
		}
		return this;
	}
}

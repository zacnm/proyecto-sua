package sua.autonomouscar.driving.l3.highwaychauffer;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;

import sua.autonomouscar.driving.interfaces.IL3_DrivingService;

public class L3_HighwayChaufferARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent  {

	private L3_HighwayChauffer highwayChauffer = null;
	
	public L3_HighwayChaufferARC(BundleContext context) {
		super(context, context.getBundle().getSymbolicName());
	}

	@Override
	public IAdaptiveReadyComponent deploy() {
		try {
			this.highwayChauffer = new L3_HighwayChauffer(context, "L3_HighwayChauffer");
			this.highwayChauffer.registerThing();
			this.highwayChauffer.startDriving();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
		
		return this;
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		if ( this.highwayChauffer != null ) {
			this.highwayChauffer.stopDriving();
			this.highwayChauffer.unregisterThing();
			this.highwayChauffer = null;
		}
		
		return this;
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		switch (req) {
			case IL3_DrivingService.DRIVINGSERVICE_Required_HumanSensors: 
				this.highwayChauffer.setHumanSensors((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RoadSensor: 
				this.highwayChauffer.setRoadSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FallbackPlan: 
				this.highwayChauffer.setFallbackPlan((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Engine: 
				this.highwayChauffer.setEngine((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Steering: 
				this.highwayChauffer.setSteering((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RearDistanceSensor: 
				this.highwayChauffer.setRearDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightDistanceSensor: 
				this.highwayChauffer.setRightDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftDistanceSensor: 
				this.highwayChauffer.setLeftDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FrontDistanceSensor: 
				this.highwayChauffer.setFrontDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightLineSensor: 
				this.highwayChauffer.setRightLineSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftLineSensor: 
				this.highwayChauffer.setLeftLineSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_NotificationService: 
				this.highwayChauffer.setNotificationService((String)value);
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
				this.highwayChauffer.setHumanSensors(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RoadSensor: 
				this.highwayChauffer.setRoadSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FallbackPlan: 
				this.highwayChauffer.setFallbackPlan(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Engine: 
				this.highwayChauffer.setEngine(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Steering: 
				this.highwayChauffer.setSteering(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RearDistanceSensor: 
				this.highwayChauffer.setRearDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightDistanceSensor: 
				this.highwayChauffer.setRightDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftDistanceSensor: 
				this.highwayChauffer.setLeftDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FrontDistanceSensor: 
				this.highwayChauffer.setFrontDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightLineSensor: 
				this.highwayChauffer.setRightLineSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftLineSensor: 
				this.highwayChauffer.setLeftLineSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_NotificationService: 
				this.highwayChauffer.setNotificationService(null);
				break;
			default:
				logger.error("No se ha podido realizar el UNBIND del servicio: " + req);
		}
		return this;
	}
}

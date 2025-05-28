package sua.autonomouscar.driving.l3.citychauffer;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.impl.AdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;

import sua.autonomouscar.driving.interfaces.IL3_DrivingService;

public class L3_CityChaufferARC extends AdaptiveReadyComponent implements IAdaptiveReadyComponent  {

	private L3_CityChauffer cityChauffer = null;
	
	public L3_CityChaufferARC(BundleContext context) {
		super(context, context.getBundle().getSymbolicName());
	}

	@Override
	public IAdaptiveReadyComponent deploy() {
		try {
			this.cityChauffer = new L3_CityChauffer(context, "L3_CityChauffer");
			this.cityChauffer.registerThing();
			this.cityChauffer.startDriving();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
		
		return this;
	}
	
	@Override
	public IAdaptiveReadyComponent undeploy() {
		if (this.cityChauffer != null) {
			this.cityChauffer.stopDriving();
			this.cityChauffer.unregisterThing();
			this.cityChauffer = null;
		}
		
		return this;
	}
	
	@Override
	public IAdaptiveReadyComponent bindService(String req, Object value) {
		switch (req) {
			case IL3_DrivingService.DRIVINGSERVICE_Required_HumanSensors: 
				this.cityChauffer.setHumanSensors((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RoadSensor: 
				this.cityChauffer.setRoadSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FallbackPlan: 
				this.cityChauffer.setFallbackPlan((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Engine: 
				this.cityChauffer.setEngine((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Steering: 
				this.cityChauffer.setSteering((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RearDistanceSensor: 
				this.cityChauffer.setRearDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightDistanceSensor: 
				this.cityChauffer.setRightDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftDistanceSensor: 
				this.cityChauffer.setLeftDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FrontDistanceSensor: 
				this.cityChauffer.setFrontDistanceSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightLineSensor: 
				this.cityChauffer.setRightLineSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftLineSensor: 
				this.cityChauffer.setLeftLineSensor((String)value);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_NotificationService: 
				this.cityChauffer.setNotificationService((String)value);
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
				this.cityChauffer.setHumanSensors(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RoadSensor: 
				this.cityChauffer.setRoadSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FallbackPlan: 
				this.cityChauffer.setFallbackPlan(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Engine: 
				this.cityChauffer.setEngine(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_Steering: 
				this.cityChauffer.setSteering(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RearDistanceSensor: 
				this.cityChauffer.setRearDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightDistanceSensor: 
				this.cityChauffer.setRightDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftDistanceSensor: 
				this.cityChauffer.setLeftDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_FrontDistanceSensor: 
				this.cityChauffer.setFrontDistanceSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_RightLineSensor: 
				this.cityChauffer.setRightLineSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_LeftLineSensor: 
				this.cityChauffer.setLeftLineSensor(null);
				break;
			case IL3_DrivingService.DRIVINGSERVICE_Required_NotificationService: 
				this.cityChauffer.setNotificationService(null);
				break;
			default:
				logger.error("No se ha podido realizar el UNBIND del servicio: " + req);
		}
		return this;
	}
}

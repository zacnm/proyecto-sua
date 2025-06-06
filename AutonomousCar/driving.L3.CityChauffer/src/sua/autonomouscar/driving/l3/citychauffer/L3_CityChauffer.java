package sua.autonomouscar.driving.l3.citychauffer;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.osgi.utils.logger.SmartLogger;
import sua.autonomouscar.devices.interfaces.ISpeedometer;
import sua.autonomouscar.driving.interfaces.IDrivingService;
import sua.autonomouscar.driving.interfaces.IL3_CityChauffer;
import sua.autonomouscar.infraestructure.OSGiUtils;
import sua.autonomouscar.infraestructure.devices.Engine;
import sua.autonomouscar.infraestructure.devices.Steering;
import sua.autonomouscar.infraestructure.driving.L3_DrivingService;
import sua.autonomouscar.interfaces.EFaceStatus;
import sua.autonomouscar.interfaces.ERoadType;

public class L3_CityChauffer extends L3_DrivingService implements IL3_CityChauffer {
		
	public static final int DEFAULT_LONGITUDINAL_SECURITY_DISTANCE = 3000;
	public static final int DEFAULT_LATERAL_SECURITY_DISTANCE = 100;
	public static final int DEFAULT_REFERENCE_SPEED = 40;

	public static final int MY_FINE_ACCELERATION_RPM = 5;
	public static final int MY_SMOOTH_ACCELERATION_RPM = 30;
	public static final int MY_MEDIUM_ACCELERATION_RPM = 70;
	public static final int MY_HIGH_ACCELERATION_RPM = 100;
	public static final int MY_AGGRESSIVE_ACCELERATION_RPM = 150;

	public L3_CityChauffer(BundleContext context, String id) {
		super(context, id);
		logger = SmartLogger.getLogger(context.getBundle().getSymbolicName());
		this.setReferenceSpeed(DEFAULT_REFERENCE_SPEED);
		this.setLongitudinalSecurityDistance(DEFAULT_LONGITUDINAL_SECURITY_DISTANCE);
		this.setLateralSecurityDistance(DEFAULT_LATERAL_SECURITY_DISTANCE);
	}

	
	
	@Override
	public IDrivingService performTheDrivingFunction() {
		
		// L3 city chauffer
		
		// Comprobamos que NO podemos mantener la conducción en nivel 3 de autonomia
		if ( this.getRoadSensor().getRoadType() == ERoadType.OFF_ROAD || this.getRoadSensor().getRoadType() == ERoadType.STD_ROAD ) {
			// No podemos seguir conduciendo de manera autónoma
			logger.info("Cannot drive in L3 Autonomy level ...");
			this.getNotificationService().notify("Cannot drive in L3 Autonomy level ...");
			
			// Realizamos TakeOver (devolver el control al conductor) si está preparado ...
			if ( this.getHumanSensors().getFaceStatus() == EFaceStatus.LOOKING_FORWARD &&
				 this.getHumanSensors().areTheHandsOnTheWheel() &&
				 this.getHumanSensors().isDriverSeatOccupied() ) {

				logger.info("The driver is ready to TakeOver ...");
				this.getNotificationService().notify("The driver is ready to TakeOver ...");
				this.performTheTakeOver();
				
			} else {
				// ... o si no podemos, activamos el Fallback Plan
				logger.info("Activating the Fallback Plan ...");
				this.activateTheFallbackPlan();
			}
			
			return this;
		}

		//
		// Control de la función primaria: MOVIMIENTO LONGITUDINAL
		//
		
		boolean longitudinal_correction_performed = false;
		ISpeedometer speedometer = OSGiUtils.getService(context, ISpeedometer.class);
		int currentSpeed = speedometer.getCurrentSpeed();
		logger.info(String.format("Current Speed: %d Km/h", currentSpeed));

		// Reducimos la velocidad un poco si detectamos distancia frontal inferior a distancia de seguridad
		if ( this.getLongitudinalSecurityDistance() > this.getFrontDistanceSensor().getDistance() ) {

			this.getEngine().decelerate(Engine.MEDIUM_ACCELERATION_RPM);
			longitudinal_correction_performed = true;
			logger.info("Font Distance Warning: ⊼");
			this.getNotificationService().notify("Font Distance Warning: Braking ...");

		} else {
			

			// Intentamos acercarnos a la velocidad referencia
			int diffSpeed = this.getReferenceSpeed() - currentSpeed;
			
			int rpmCorrection = MY_FINE_ACCELERATION_RPM;
			String rpmAppliedCorrection = "fine";
			if ( Math.abs(diffSpeed) > 30 ) { rpmCorrection = MY_AGGRESSIVE_ACCELERATION_RPM; rpmAppliedCorrection = "aggressive"; }
			else if ( Math.abs(diffSpeed) > 15 ) { rpmCorrection = MY_HIGH_ACCELERATION_RPM; rpmAppliedCorrection = "high"; }
			else if ( Math.abs(diffSpeed) > 5 ) { rpmCorrection = MY_MEDIUM_ACCELERATION_RPM; rpmAppliedCorrection = "medium"; }
			else if ( Math.abs(diffSpeed) > 1 ) { rpmCorrection = MY_SMOOTH_ACCELERATION_RPM; rpmAppliedCorrection = "smooth"; }


			if ( diffSpeed > 0  ) {
				this.getEngine().accelerate(rpmCorrection);
				longitudinal_correction_performed = true;
				logger.info(String.format("Accelerating (%s) to get the reference speeed of %d Km/h", rpmAppliedCorrection, this.getReferenceSpeed()));
			} else if ( diffSpeed < 0 ) {
				this.getEngine().decelerate(rpmCorrection);
				longitudinal_correction_performed = true;
				logger.info(String.format("Decelerating (%s) to get the reference speeed of %d Km/h", rpmAppliedCorrection, this.getReferenceSpeed()));
			}
			
		}

		//
		// Control de la función primaria: MOVIMIENTO LATERAL
		//
		
		boolean lateral_correction_performed = false;
		// Control de las distancias laterales
		if ( this.getRightDistanceSensor().getDistance() < this.getLateralSecurityDistance() ) {
			if ( this.getLeftDistanceSensor().getDistance() > this.getLateralSecurityDistance() ) {
				logger.info("Right Distance Warning: > @ . Turning the Steering to the left ...");
				this.getSteering().rotateLeft(Steering.SEVERE_CORRECTION_ANGLE);
				lateral_correction_performed = true;
			} else {
				logger.info("Right and Left Distance Warning: @ <  > @ . Obstacles too close!!");
				this.getNotificationService().notify("Right and Left Distance Warning: @ <  > @ . Obstacles too close!!");
				lateral_correction_performed = true;
			}
		}

		if ( !lateral_correction_performed && 
			 this.getLeftDistanceSensor().getDistance() < this.getLateralSecurityDistance() ) {
			if ( this.getRightDistanceSensor().getDistance() > this.getLateralSecurityDistance() ) {
				logger.info("Left Distance Warning: @ < . Turning the Steering to the right ...");
				this.getSteering().rotateRight(Steering.SEVERE_CORRECTION_ANGLE);
				lateral_correction_performed = true;
			}
		}

		if ( !lateral_correction_performed ) {
			// Control de la dirección si nos salimos del carril
			if ( this.getLeftLineSensor().isLineDetected() ) {
				this.getSteering().rotateRight(Steering.SMOOTH_CORRECTION_ANGLE);
				lateral_correction_performed = true;
				logger.info("Left Line Sensor Warning: |< . Turning the Steering to the right ...");
				this.getNotificationService().notify("Left Line Sensor Warning: Turning the Steering to the right ...");
			}
			
			if ( this.getRightLineSensor().isLineDetected() ) {
				this.getSteering().rotateLeft(Steering.SMOOTH_CORRECTION_ANGLE);
				lateral_correction_performed = true;
				logger.info("Right Line Sensor Warning: >| . Turning the Steering to the left ...");
				this.getNotificationService().notify("Right Line Sensor Warning: Turning to the left ...");
			}
		}
		
		
			
		// Si todo va bien, indicamos que seguimos como estamos ...
		if ( !longitudinal_correction_performed && !lateral_correction_performed) {
			logger.info("Controlling the driving function. Mantaining the current configuration ...");
		}
		
		
		//
		// Interacción con el conductor
		//

		// Advertimos al humano sí ...

		// ... està distraído o dormido ...
		switch (this.getHumanSensors().getFaceStatus()) {
		case DISTRACTED:
			this.getNotificationService().notify("Please, look forward!");
			break;
		case SLEEPING:
			this.getNotificationService().notify("Please, WAKE UP! ... and look forward!");
			break;
		default:
			break;
		}
		
				
		// ... el conductor no tiene las manos en el volante ...
		if ( !this.getHumanSensors().areTheHandsOnTheWheel() ) {
			this.getNotificationService().notify("Please, put the hands on the wheel!");
		}
		
		// ... el conductor no está en el asiento del conductor ...
		if ( !this.getHumanSensors().isDriverSeatOccupied() ) {
			if ( this.getHumanSensors().isCopilotSeatOccupied() )
				this.getNotificationService().notify("Please, move to the driver seat!");
			else {
				// No se puede conducir en L3 sin conductor. Activamos plan de emergencia
				this.getNotificationService().notify("Cannot drive with a driver! Activating the Fallback Plan ...");
				this.activateTheFallbackPlan();
			}
		}

		
		return this;
	}



}

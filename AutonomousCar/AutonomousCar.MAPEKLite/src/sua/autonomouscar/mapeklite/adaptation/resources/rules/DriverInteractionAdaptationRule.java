package sua.autonomouscar.mapeklite.adaptation.resources.rules;

import java.util.List;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.exceptions.analyzing.RuleException;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;

import sua.autonomouscar.driving.interfaces.IL3_CityChauffer;
import sua.autonomouscar.driving.interfaces.IL3_HighwayChauffer;
import sua.autonomouscar.driving.interfaces.IL3_TrafficJamChauffer;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.infrastructure.interaction.NotificationService;
import sua.autonomouscar.interaction.interfaces.IInteractionMechanism;
import sua.autonomouscar.interaction.interfaces.INotificationService;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;

public class DriverInteractionAdaptationRule extends AdaptationRule {
	
	public static String ID = "Regla Interacción con Conductor";
	
	IKnowledgeProperty handsOnWheelKp = null;
	IKnowledgeProperty asientoConductorKp = null;

	public DriverInteractionAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("hands-on-wheel");
		this.setListenToKnowledgePropertyChanges("asiento-conductor-ocupado");
		
		handsOnWheelKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("hands-on-wheel");
		asientoConductorKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("asiento-conductor-ocupado");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		if (handsOnWheelKp == null || handsOnWheelKp.getValue() == null ||
				asientoConductorKp == null || asientoConductorKp.getValue() == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		boolean handsOnWheel = (boolean) handsOnWheelKp.getValue();
		boolean asientoConductorOcupado = (boolean) asientoConductorKp.getValue();
		
		NotificationService notificationService = (NotificationService) OSGiUtils.getService(this.context, INotificationService.class);
		List<IInteractionMechanism> interactionMechanisms = OSGiUtils.getServices(this.context, IInteractionMechanism.class);
		var t = interactionMechanisms.stream().map;
		
		if (asientoConductorOcupado) {
			if (handsOnWheel) {
				notificationService.addInteractionMechanism("SteeringWheel_HapticVibration");
				notificationService.addInteractionMechanism("DashboardIcon_VisualIcon");
			}
			
		} else {
			throw new RuleException("Cannot understand knowledge", "Not executing");
		}
		
		return this.configuracionSistemaActivarTrafficJamChauffer();
	}

	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarTrafficJamChauffer() {
		IRuleComponentsSystemConfiguration nextSystemConfiguration = 
				SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		return nextSystemConfiguration;		
		
	}
}

package sua.autonomouscar.mapeklite.adaptation.resources.rules;

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

import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.mapeklite.adaptation.resources.knowledge.KnowledgeId;

public class DriverSeatHapticVibrationAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(DriverSeatHapticVibrationAdaptationRule.class);
	public static String ID = "Regla Vibración Asiento Conductor";
	
	IKnowledgeProperty kp_VibracionAsientoConductor = null;

	public DriverSeatHapticVibrationAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges(KnowledgeId.VIBRACION_ASIENTO_CONDUCTOR);

		kp_VibracionAsientoConductor = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.VIBRACION_ASIENTO_CONDUCTOR);
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_VibracionAsientoConductor == null || kp_VibracionAsientoConductor.getValue() == null ) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	
		boolean vibracionAsientoConductor = (boolean) kp_VibracionAsientoConductor.getValue();
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = vibracionAsientoConductor ? 
				activarVibracionAsientoConductor() : desactivarVibracionAsientoConductor();
		
		return nextSystemConfiguration;
	}
	
	private IRuleComponentsSystemConfiguration activarVibracionAsientoConductor() {
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		SystemConfigurationHelper.componentToAdd(nextSystemConfiguration, "interaction.Seat.Driver", "1.0.0");
		
		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.REQUIRED_SERVICE,
				"interaction.Seat.Driver", "1.0.0", HapticVibrationARC.PROVIDED_MECHANISM);

		return nextSystemConfiguration;
	}
	
	private IRuleComponentsSystemConfiguration desactivarVibracionAsientoConductor() {
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		SystemConfigurationHelper.componentToRemove(nextSystemConfiguration, "interaction.Seat.Driver", "1.0.0");

		return nextSystemConfiguration;
	}

	
}

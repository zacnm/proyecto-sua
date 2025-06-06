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

public class SteeringWheelHapticVibrationAdaptationRule extends AdaptationRule {
	
	protected static SmartLogger logger = SmartLogger.getLogger(SteeringWheelHapticVibrationAdaptationRule.class);
	public static String ID = "Regla Vibración Volante";
	
	IKnowledgeProperty kp_VibracionVolante = null;

	public SteeringWheelHapticVibrationAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges(KnowledgeId.VIBRACION_VOLANTE);

		kp_VibracionVolante = BasicMAPEKLiteLoopHelper.getKnowledgeProperty(KnowledgeId.VIBRACION_VOLANTE);
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		
		if (kp_VibracionVolante == null || kp_VibracionVolante.getValue() == null ) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
	
		boolean vibracionVolante = (boolean) kp_VibracionVolante.getValue();
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = vibracionVolante ? 
				activarVibracionVolante() : desactivarVibracionVolante();
		
		return nextSystemConfiguration;
	}
	
	private IRuleComponentsSystemConfiguration activarVibracionVolante() {
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		SystemConfigurationHelper.componentToAdd(nextSystemConfiguration, "interaction.SteeringWheel", "1.0.0");

		SystemConfigurationHelper.bindingToAdd(nextSystemConfiguration, 
				"interaction.NotificationService", "1.0.0", NotificationServiceARC.REQUIRED_SERVICE,
				"interaction.SteeringWheel", "1.0.0", HapticVibrationARC.PROVIDED_MECHANISM);

		return nextSystemConfiguration;
	}
	
	private IRuleComponentsSystemConfiguration desactivarVibracionVolante() {
		
		IRuleComponentsSystemConfiguration nextSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		SystemConfigurationHelper.componentToRemove(nextSystemConfiguration, "interaction.SteeringWheel", "1.0.0");

		return nextSystemConfiguration;
	}

	
}

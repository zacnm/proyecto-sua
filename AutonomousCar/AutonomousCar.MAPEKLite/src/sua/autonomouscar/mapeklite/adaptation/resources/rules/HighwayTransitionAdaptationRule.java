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

import sua.autonomouscar.driving.interfaces.IL3_CityChauffer;
import sua.autonomouscar.driving.interfaces.IL3_HighwayChauffer;
import sua.autonomouscar.driving.interfaces.IL3_TrafficJamChauffer;
import sua.autonomouscar.infrastructure.OSGiUtils;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.EFuncionConduccion;
import sua.autonomouscar.mapeklite.adaptation.resources.enums.ENivelAutonomia;

public class HighwayTransitionAdaptationRule extends AdaptationRule {
	
	public static String ID = "Regla Transición a Highway";
	
	IKnowledgeProperty nivelAutonomiaKp = null;
	IKnowledgeProperty funcionConduccionKp = null;

	public HighwayTransitionAdaptationRule(BundleContext context) {
		super(context, ID);
		this.setListenToKnowledgePropertyChanges("nivel-autonomia");
		this.setListenToKnowledgePropertyChanges("funcion-conduccion");
		
		nivelAutonomiaKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("nivel-autonomia");
		funcionConduccionKp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("funcion-conduccion");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		if (nivelAutonomiaKp == null || nivelAutonomiaKp.getValue() != ENivelAutonomia.L3_AutomatizacionCondicional ||
				funcionConduccionKp == null || funcionConduccionKp.getValue() != EFuncionConduccion.L3_HighwayChauffer) {
			return false;
		}
		
		return true;
	}

	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) throws RuleException {
		ENivelAutonomia nivelAutonomia = (ENivelAutonomia) nivelAutonomiaKp.getValue();
		EFuncionConduccion funcionConduccion = (EFuncionConduccion) funcionConduccionKp.getValue();
		
		if (nivelAutonomia == ENivelAutonomia.L3_AutomatizacionCondicional && funcionConduccion == EFuncionConduccion.L3_HighwayChauffer) {

			IL3_CityChauffer L3CityChaufferService = OSGiUtils.getService(context, IL3_CityChauffer.class);
			IL3_HighwayChauffer L3HighwayChaufferService = OSGiUtils.getService(context, IL3_HighwayChauffer.class);
			IL3_TrafficJamChauffer L3TrafficJamChaufferService = OSGiUtils.getService(context, IL3_TrafficJamChauffer.class);
			
			L3CityChaufferService.stopDriving();
			L3TrafficJamChaufferService.stopDriving();
			
			L3HighwayChaufferService.startDriving();
		
			return this.configuracionSistemaActivarTrafficJamChauffer();
		} else {
			throw new RuleException("Cannot understand knowledge", "Not executing");
		}
	}

	protected IRuleComponentsSystemConfiguration configuracionSistemaActivarTrafficJamChauffer() {
		IRuleComponentsSystemConfiguration nextSystemConfiguration = 
				SystemConfigurationHelper.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		return nextSystemConfiguration;		
		
	}
}
